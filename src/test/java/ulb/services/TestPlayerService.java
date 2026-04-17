package ulb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.player.Player;
import ulb.models.player.exceptions.NoActiveTeamException;
import ulb.repositories.PlayerRepository;
import ulb.repositories.exceptions.TeamNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class TestPlayerService {

    @Mock
    private PlayerRepository playerRepository;

    private PlayerService playerService;
    private static final int PLAYER_ID = 1;

    @Before
    public void setUp() {
        this.setupMockTeams();
    }

    private void setupMockTeams(BugemonTeam... teams) {
        List<BugemonTeam> teamList = java.util.Arrays.asList(teams);
        when(this.playerRepository.loadTeams(PLAYER_ID)).thenReturn(teamList);

        Player player = new Player(PLAYER_ID);
        this.playerService = new PlayerService(this.playerRepository, player);
    }

    @Test
    public void shouldSetAndGetActiveTeam_whenTeamExists() throws Exception {
        String teamName = "DreamTeam";
        this.setupMockTeams(new BugemonTeam(teamName));

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
        String teamName = "ToDelete";
        this.setupMockTeams(new BugemonTeam(teamName));
        this.playerService.setActiveTeam(teamName);
        this.playerService.deleteActiveTeam();

        verify(this.playerRepository).deleteTeam(PLAYER_ID, teamName);
        assertTrue(this.playerService.getActiveTeam().isEmpty());
    }

    @Test
    public void shouldReturnTrue_whenActiveTeamIsSaved() throws Exception {
        Bugemon bugemon = new BugemonBuilder().name("Pikachu").build();
        BugemonTeam team = new BugemonTeam("TeamA");
        team.add(bugemon);

        this.setupMockTeams(team);

        this.playerService.setActiveTeam("TeamA");

        assertTrue("L'équipe devrait être considérée comme sauvegardée", this.playerService.isActiveTeamSaved());
    }

    @Test
    public void shouldCorrectlyRenameTeam() throws Exception {
        String oldName = "Old";
        String newName = "New";
        this.setupMockTeams(new BugemonTeam(oldName));

        this.playerService.setActiveTeam(oldName);
        this.playerService.renameTeam(oldName, newName);

        verify(this.playerRepository).renameTeam(PLAYER_ID, oldName, newName);
        assertEquals(newName, this.playerService.getActiveTeam().get().getName());
    }

    @Test
    public void shouldReturnFalse_whenActiveTeamHasUnsavedChanges() throws Exception {
        Bugemon b1 = new BugemonBuilder().name("Pikachu").build();
        BugemonTeam teamInDb = new BugemonTeam("TeamA");
        teamInDb.add(b1);

        this.setupMockTeams(teamInDb);

        this.playerService.setActiveTeam("TeamA");

        Bugemon b2 = new BugemonBuilder().name("Bulbasaur").build();
        this.playerService.addOrRemoveBugemonOfActiveTeam(b2);

        assertFalse("L'équipe ne devrait PAS être considérée comme sauvegardée après modif",
                this.playerService.isActiveTeamSaved());
    }

    @Test
    public void shouldUpdateTeamsNames() throws Exception {
        this.setupMockTeams();

        Bugemon b1 = new BugemonBuilder().name("P1").build();
        this.playerService.addOrRemoveBugemonOfActiveTeam(b1);
        this.playerService.saveTeam("team1");

        assertEquals("team1", this.playerService.getTeamNames().get(0));
    }
}
