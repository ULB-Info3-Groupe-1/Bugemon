package ulb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repositories.PlayerRepository;
import ulb.repositories.exceptions.TeamEmptyException;
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

        this.teamService.setActiveTeam(teamName);

        assertTrue(this.teamService.getActiveTeam().isPresent());
        assertEquals(teamName, this.teamService.getActiveTeam().get().getName());
    }

    @Test(expected = TeamNotFoundException.class)
    public void shouldThrowException_whenSettingNonExistentTeam() throws Exception {
        this.teamService.setActiveTeam("Unknown");
    }

    @Test
    public void shouldSaveTeam_whenActiveTeamIsValid() throws Exception {
        Bugemon bugemon = new BugemonBuilder().name("Pikachu").build();

        this.teamService.addOrRemoveBugemon(bugemon);
        String teamName = "NewTeam";

        this.teamService.saveTeam(teamName);

        verify(this.playerRepository).createTeam(PLAYER_NAME, teamName);
        assertEquals(teamName, this.teamService.getActiveTeam().get().getName());
        assertTrue(this.teamService.getTeamNames().contains(teamName));
    }

    @Test
    public void shouldDeleteActiveTeam_whenRequested() throws Exception {
        BugemonTeam team = new BugemonTeam();
        team.setName("ToDelete");
        when(this.playerRepository.loadTeams(PLAYER_NAME)).thenReturn(new ArrayList<>(List.of(team)));
        this.teamService = new TeamService(this.playerRepository, PLAYER_NAME);
        this.teamService.setActiveTeam("ToDelete");

        this.teamService.deleteTeam("ToDelete");

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
        this.teamService.setActiveTeam("TeamA");

        assertTrue("The team should be considered saved.", this.teamService.isWorkingTeamSaved());
    }

    @Test
    public void shouldCorrectlyRenameTeam() throws Exception {
        String oldName = "Old";
        String newName = "New";
        BugemonTeam team = new BugemonTeam();
        team.setName(oldName);

        when(this.playerRepository.loadTeams(PLAYER_NAME)).thenReturn(new ArrayList<>(List.of(team)));
        this.teamService = new TeamService(this.playerRepository, PLAYER_NAME);
        this.teamService.setActiveTeam(oldName);

        this.teamService.renameTeam(oldName, newName);

        verify(this.playerRepository).renameTeam(PLAYER_NAME, oldName, newName);
        assertEquals(newName, this.teamService.getActiveTeam().get().getName());
        assertTrue(this.teamService.getTeamNames().contains(newName));
        assertFalse(this.teamService.getTeamNames().contains(oldName));
    }

    @Test
    public void shouldReturnFalse_whenActiveTeamHasUnsavedChanges() throws Exception {
        Bugemon b1 = new BugemonBuilder().name("Pikachu").build();
        Bugemon b2 = new BugemonBuilder().name("Bulbasaur").build();
        BugemonTeam teamInDb = new BugemonTeam("TeamA");
        teamInDb.add(b1);

        when(this.playerRepository.loadTeams(PLAYER_NAME)).thenReturn(List.of(teamInDb));
        this.teamService = new TeamService(this.playerRepository, PLAYER_NAME);
        this.teamService.setActiveTeam("TeamA");

        this.teamService.addOrRemoveBugemon(b2);

        assertFalse("The team should not be considered saved after modification.",
                this.teamService.isWorkingTeamSaved());
    }

    @Test
    public void shouldUpdateEveryBugemonInDb_whenSavingState() throws Exception {
        Bugemon b1 = new BugemonBuilder().name("P1").build();
        Bugemon b2 = new BugemonBuilder().name("P2").build();
        this.teamService.addOrRemoveBugemon(b1);
        this.teamService.addOrRemoveBugemon(b2);
        this.teamService.saveTeam("Team");

        this.teamService.setActiveTeam("Team");

        this.teamService.saveBugemonStateOfActiveTeam();

        verify(this.playerRepository, times(2)).updatePlayerBugemon(org.mockito.ArgumentMatchers.any());
    }

    @Test(expected = TeamEmptyException.class)
    public void shouldThrowException_whenSavingEmptyWorkingTeam() throws Exception {
        this.teamService.saveTeam("EmptyTeam");
    }

    @Test(expected = NoActiveTeamException.class)
    public void shouldThrowException_whenRestoringHpWithoutActiveTeam() throws Exception {
        this.teamService.restoreHpActiveTeam();
    }

    @Test
    public void shouldSaveNewBugemonInDb_whenNotAlreadyOwned() throws Exception {
        Bugemon newBugemon = new BugemonBuilder().name("NotOwned").build();
        this.teamService.addOrRemoveBugemon(newBugemon);

        when(this.playerRepository.getPlayerBugemons(PLAYER_NAME)).thenReturn(new ArrayList<>());

        this.teamService.saveTeam("LegendaryTeam");

        verify(this.playerRepository).savePlayerBugemon(org.mockito.ArgumentMatchers.any());
        verify(this.playerRepository).addTeamMember(org.mockito.ArgumentMatchers.any());
    }

    @Test
    public void shouldUpdateDbAndActiveTeam_whenModifyingTeam() throws Exception {
        Bugemon b1 = new BugemonBuilder().name("Pikachu").build();
        this.teamService.addOrRemoveBugemon(b1);
        this.teamService.saveTeam("Original");
        this.teamService.setActiveTeam("Original");

        Bugemon b2 = new BugemonBuilder().name("Charmander").build();
        this.teamService.addOrRemoveBugemon(b2);

        this.teamService.modifyTeam("Original");

        verify(this.playerRepository).modifyTeam(org.mockito.ArgumentMatchers.eq(PLAYER_NAME),
                org.mockito.ArgumentMatchers.eq("Original"), org.mockito.ArgumentMatchers.anyList());
        assertEquals(2, this.teamService.getActiveTeam().get().size());
    }

    @Test
    public void shouldClearWorkingTeam_afterSaving() throws Exception {
        Bugemon bugemon = new BugemonBuilder().name("Pikachu").build();
        this.teamService.addOrRemoveBugemon(bugemon);

        this.teamService.saveTeam("SomeTeam");

        assertTrue("The workingTeam should be empty after saving", this.teamService.getWorkingTeam().isEmpty());
    }

    @Test
    public void shouldSyncWorkingTeamWithActiveTeam() throws Exception {
        Bugemon bugemon = new BugemonBuilder().name("Pikachu").build();
        BugemonTeam team = new BugemonTeam("Active");
        team.addOrRemoveBugemon(bugemon);

        this.teamService.addOrRemoveBugemon(bugemon);
        this.teamService.saveTeam("Active");
        this.teamService.setActiveTeam("Active");

        this.teamService.clearWorkingTeam();

        this.teamService.setWorkingTeamEqualsActiveTeam();

        assertEquals(team, this.teamService.getWorkingTeam());
    }
}
