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
import ulb.repositories.exceptions.TeamNotFoundException;
import ulb.services.exceptions.NoActiveTeamException;

@RunWith(MockitoJUnitRunner.class)
public class TestPlayerService {

    @Mock
    private PlayerRepository playerRepository;

    private PlayerService playerService;
    private static final String PLAYER_NAME = "Player";
    private static final int PLAYER_ID = 1;

    @Before
    public void setUp() throws Exception {
        when(this.playerRepository.getPlayerIdOrCreatePlayer(PLAYER_NAME)).thenReturn(PLAYER_ID);
        when(this.playerRepository.loadTeams(PLAYER_ID)).thenReturn(new ArrayList<>());

        this.playerService = new PlayerService(this.playerRepository, PLAYER_NAME);
    }

    @Test
    public void shouldSetAndGetActiveTeam_whenTeamExists() throws Exception {
        String teamName = "DreamTeam";
        BugemonTeam team = new BugemonTeam(teamName);

        when(this.playerRepository.loadTeams(PLAYER_ID)).thenReturn(List.of(team));
        this.playerService = new PlayerService(this.playerRepository, PLAYER_NAME);

        this.playerService.setActiveTeam(teamName);

        assertTrue(this.playerService.getActiveTeam().isPresent());
        assertEquals(teamName, this.playerService.getActiveTeam().get().getName());
    }

    @Test(expected = TeamNotFoundException.class)
    public void shouldThrowException_whenSettingNonExistentTeam() throws Exception {
        this.playerService.setActiveTeam("Unknown");
    }

    @Test
    public void shouldSaveTeam_whenActiveTeamIsValid() throws Exception {
        Bugemon bugemon = new BugemonBuilder().name("Pikachu").build();

        this.playerService.addOrRemoveBugemonOfActiveTeam(bugemon);
        String teamName = "NewTeam";

        this.playerService.saveTeam(teamName);

        verify(this.playerRepository).createTeam(PLAYER_ID, teamName);
        assertEquals(teamName, this.playerService.getActiveTeam().get().getName());
        assertTrue(this.playerService.getTeamNames().contains(teamName));
    }

    @Test(expected = NoActiveTeamException.class)
    public void shouldThrowException_whenSavingWithoutActiveTeam() throws Exception {
        this.playerService.saveTeam("AnyName");
    }

    @Test
    public void shouldDeleteActiveTeam_whenRequested() throws Exception {
        BugemonTeam team = new BugemonTeam();
        team.setName("ToDelete");
        when(this.playerRepository.loadTeams(PLAYER_ID)).thenReturn(new ArrayList<>(List.of(team)));
        this.playerService = new PlayerService(this.playerRepository, PLAYER_NAME);
        this.playerService.setActiveTeam("ToDelete");

        this.playerService.deleteActiveTeam();

        verify(this.playerRepository).deleteTeam(PLAYER_ID, "ToDelete");
        assertTrue(this.playerService.getActiveTeam().isEmpty());
        assertFalse(this.playerService.getTeamNames().contains("ToDelete"));
    }

    @Test
    public void shouldReturnTrue_whenActiveTeamIsSaved() throws Exception {
        Bugemon bugemon = new BugemonBuilder().name("Pikachu").build();

        BugemonTeam team = new BugemonTeam();
        team.setName("TeamA");
        team.addOrRemoveBugemon(bugemon);

        when(this.playerRepository.loadTeams(PLAYER_ID)).thenReturn(List.of(team));
        this.playerService = new PlayerService(this.playerRepository, PLAYER_NAME);
        this.playerService.setActiveTeam("TeamA");

        assertTrue("L'équipe devrait être considérée comme sauvegardée", this.playerService.isActiveTeamSaved());
    }

    @Test
    public void shouldCorrectlyRenameTeam() throws Exception {
        String oldName = "Old";
        String newName = "New";
        BugemonTeam team = new BugemonTeam();
        team.setName(oldName);

        when(this.playerRepository.loadTeams(PLAYER_ID)).thenReturn(new ArrayList<>(List.of(team)));
        this.playerService = new PlayerService(this.playerRepository, PLAYER_NAME);
        this.playerService.setActiveTeam(oldName);

        this.playerService.renameTeam(oldName, newName);

        verify(this.playerRepository).renameTeam(PLAYER_ID, oldName, newName);
        assertEquals(newName, this.playerService.getActiveTeam().get().getName());
        assertTrue(this.playerService.getTeamNames().contains(newName));
        assertFalse(this.playerService.getTeamNames().contains(oldName));
    }

    @Test
    public void shouldReturnFalse_whenActiveTeamHasUnsavedChanges() throws Exception {
        Bugemon b1 = new BugemonBuilder().name("Pikachu").build();
        Bugemon b2 = new BugemonBuilder().name("Bulbasaur").build();
        BugemonTeam teamInDb = new BugemonTeam("TeamA");
        teamInDb.add(b1);

        when(this.playerRepository.loadTeams(PLAYER_ID)).thenReturn(List.of(teamInDb));
        this.playerService = new PlayerService(this.playerRepository, PLAYER_NAME);
        this.playerService.setActiveTeam("TeamA");

        this.playerService.addOrRemoveBugemonOfActiveTeam(b2);

        assertFalse("L'équipe ne devrait PAS être considérée comme sauvegardée après modif",
                this.playerService.isActiveTeamSaved());
    }

    @Test
    public void shouldUpdateEveryBugemonInDb_whenSavingState() throws Exception {
        Bugemon b1 = new BugemonBuilder().name("P1").build();
        Bugemon b2 = new BugemonBuilder().name("P2").build();
        this.playerService.addOrRemoveBugemonOfActiveTeam(b1);
        this.playerService.addOrRemoveBugemonOfActiveTeam(b2);

        this.playerService.saveBugemonStateOfActiveTeam();

        verify(this.playerRepository, times(2)).updatePlayerBugemon(org.mockito.ArgumentMatchers.any());
    }
}
