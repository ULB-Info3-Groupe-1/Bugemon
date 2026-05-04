package ulb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repositories.PlayerRepository;
import ulb.repositories.exceptions.TeamNotFoundException;
import ulb.services.exceptions.NoActiveTeamException;

@RunWith(MockitoJUnitRunner.class)
public class TestTeamService {

    @Mock
    private PlayerRepository playerRepository;

    private TeamService teamService;
    private static final String PLAYER_NAME = "Player";

    @Before
    public void setUp() {
        when(this.playerRepository.loadTeams(PLAYER_NAME)).thenReturn(new ArrayList<>());
        this.teamService = new TeamService(this.playerRepository, PLAYER_NAME);
    }

    @Test
    public void shouldSetAndGetActiveTeam_whenTeamExists() throws Exception {
        String teamName = "DreamTeam";
        BugemonTeam team = new BugemonTeam(teamName);

        when(this.playerRepository.loadTeams(PLAYER_NAME)).thenReturn(List.of(team));
        this.teamService = new TeamService(this.playerRepository, PLAYER_NAME);
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam(teamName);

        assertTrue(this.teamService.getActiveTeam().isPresent());
        assertEquals(teamName, this.teamService.getActiveTeam().get().getName());
    }

    @Test(expected = TeamNotFoundException.class)
    public void shouldThrowException_whenSettingNonExistentTeam() throws Exception {
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam("Unknown");
    }

    @Test
    public void shouldSaveTeam_whenActiveTeamIsValid() throws Exception {
        this.teamService.loadTeamsAndActiveTeam();

        Bugemon bugemon = new BugemonBuilder().name("Pikachu").build();
        this.teamService.addOrRemoveBugemonOfActiveTeam(bugemon);
        String teamName = "NewTeam";
        this.teamService.saveTeam(teamName);
        this.teamService.setActiveTeam(teamName);

        verify(this.playerRepository).createTeam(PLAYER_NAME, teamName);
        assertEquals(teamName, this.teamService.getActiveTeam().get().getName());
        assertTrue(this.teamService.getTeamNames().contains(teamName));
    }

    @Test(expected = NoActiveTeamException.class)
    public void shouldThrowException_whenSavingWithoutActiveTeam() throws Exception {
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.saveTeam("AnyName");
    }

    @Test
    public void shouldDeleteActiveTeam_whenRequested() throws Exception {
        BugemonTeam team = new BugemonTeam();
        team.setName("ToDelete");
        when(this.playerRepository.loadTeams(PLAYER_NAME)).thenReturn(new ArrayList<>(List.of(team)));
        this.teamService = new TeamService(this.playerRepository, PLAYER_NAME);
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam("ToDelete");

        this.teamService.deleteActiveTeam();

        verify(this.playerRepository).deleteTeam(PLAYER_NAME, "ToDelete");
        assertTrue(this.teamService.getActiveTeam().isEmpty());
        assertFalse(this.teamService.getTeamNames().contains("ToDelete"));
    }

    @Test
    public void shouldReturnTrue_whenActiveTeamIsSaved() throws Exception {
        Bugemon bugemon = new BugemonBuilder().name("Pikachu").build();

        BugemonTeam team = new BugemonTeam();
        team.setName("TeamA");
        team.addOrRemoveBugemon(bugemon);

        when(this.playerRepository.loadTeams(PLAYER_NAME)).thenReturn(List.of(team));
        this.teamService = new TeamService(this.playerRepository, PLAYER_NAME);
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam("TeamA");

        assertTrue("L'équipe devrait être considérée comme sauvegardée", this.teamService.isActiveTeamSaved());
    }

    @Test
    public void shouldCorrectlyRenameTeam() throws Exception {
        String oldName = "Old";
        BugemonTeam team = new BugemonTeam();
        team.setName(oldName);

        when(this.playerRepository.loadTeams(PLAYER_NAME)).thenReturn(new ArrayList<>(List.of(team)));
        this.teamService = new TeamService(this.playerRepository, PLAYER_NAME);
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam(oldName);

        String newName = "New";
        this.teamService.renameTeam(oldName, newName);

        verify(this.playerRepository).renameTeam(PLAYER_NAME, oldName, newName);
        assertEquals(newName, this.teamService.getActiveTeam().get().getName());
        assertTrue(this.teamService.getTeamNames().contains(newName));
        assertFalse(this.teamService.getTeamNames().contains(oldName));
    }

    @Test
    public void shouldReturnFalse_whenActiveTeamHasUnsavedChanges() throws Exception {
        Bugemon b1 = new BugemonBuilder().name("Pikachu").build();
        BugemonTeam teamInDb = new BugemonTeam("TeamA");
        teamInDb.add(b1);
        when(this.playerRepository.loadTeams(PLAYER_NAME)).thenReturn(List.of(teamInDb));

        this.teamService = new TeamService(this.playerRepository, PLAYER_NAME);
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam("TeamA");

        Bugemon b2 = new BugemonBuilder().name("Bulbasaur").build();
        this.teamService.addOrRemoveBugemonOfActiveTeam(b2);

        assertFalse("L'équipe ne devrait PAS être considérée comme sauvegardée après modif",
                this.teamService.isActiveTeamSaved());
    }

    @Test
    public void shouldUpdateEveryBugemonInDb_whenSavingState() throws Exception {
        this.teamService.loadTeamsAndActiveTeam();

        Bugemon b1 = new BugemonBuilder().name("P1").build();
        Bugemon b2 = new BugemonBuilder().name("P2").build();
        this.teamService.addOrRemoveBugemonOfActiveTeam(b1);
        this.teamService.addOrRemoveBugemonOfActiveTeam(b2);

        this.teamService.saveBugemonStateOfActiveTeam();

        verify(this.playerRepository, times(2)).updatePlayerBugemon(org.mockito.ArgumentMatchers.any());
    }

    @Test
    public void shouldEraseAllBugemonTeamsAndClearActiveTeam_whenclearTeamsAndActiveTeam() throws Exception {
        BugemonTeam team1 = new BugemonTeam("Team1");
        BugemonTeam team2 = new BugemonTeam("Team2");

        when(this.playerRepository.loadTeams(PLAYER_NAME)).thenReturn(new ArrayList<>(List.of(team1, team2)));
        this.teamService = new TeamService(this.playerRepository, PLAYER_NAME);
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam("Team1");

        this.teamService.clearTeamsAndActiveTeam();

        verify(this.playerRepository).clearTeams(PLAYER_NAME);
        assertTrue(this.teamService.getActiveTeam().isEmpty());
        assertTrue(this.teamService.getTeamNames().isEmpty());
    }

    @Test
    public void shouldLoadTeamsAndActiveTeam_whenLoadTeamsAndActiveTeam() {
        BugemonTeam team1 = new BugemonTeam("Team1");
        BugemonTeam team2 = new BugemonTeam("Team2");

        when(this.playerRepository.loadTeams(PLAYER_NAME)).thenReturn(List.of(team1, team2));
        when(this.playerRepository.loadCurrentTeam(PLAYER_NAME)).thenReturn(Optional.of(team1));

        this.teamService.loadTeamsAndActiveTeam();

        assertTrue(this.teamService.getActiveTeam().isPresent());
        assertEquals("Team1", this.teamService.getActiveTeam().get().getName());
        assertTrue(this.teamService.getTeamNames().contains("Team1"));
        assertTrue(this.teamService.getTeamNames().contains("Team2"));
    }
}
