package ulb.models.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.player.exceptions.NoActiveTeamException;

public class TestPlayer {

    private Player player;
    private List<BugemonTeam> teams;
    private Inventory inventory;

    @Before
    public void setUp() {
        this.teams = new ArrayList<>();
        this.inventory = Mockito.mock(Inventory.class); // To isolate the Player from the Inventory
        this.player = new Player(1, this.teams, this.inventory);
    }

    @Test
    public void shouldThrowExceptionWhenNoActiveTeam() {
        Bugemon bugemon = new BugemonBuilder().name("bugemon").build();

        assertThrows(NoActiveTeamException.class, () -> this.player.getActiveTeamName());
        assertThrows(NoActiveTeamException.class, () -> this.player.restoreHp());
        assertThrows(NoActiveTeamException.class, () -> this.player.addActiveTeamToCache());
        assertThrows(NoActiveTeamException.class, () -> this.player.setActiveTeamName("teamName"));
        assertThrows(NoActiveTeamException.class, () -> this.player.addOrRemoveBugemonOfActiveTeam(bugemon));
    }

    @Test
    public void shouldSetActiveTeamWithCopy() {
        BugemonTeam team = new BugemonTeam("Original");
        this.player.setActiveTeam(team);

        assertTrue(this.player.getActiveTeam().isPresent());
        assertEquals("Original", this.player.getActiveTeam().get().getName());

        team.setName("Modified");
        assertNotEquals(team.getName(), this.player.getActiveTeam().get().getName());
    }

    @Test
    public void shouldAddActiveTeamToCache() throws NoActiveTeamException {
        BugemonTeam team = new BugemonTeam("Team 1");
        this.player.setActiveTeam(team);

        this.player.addActiveTeamToCache();

        assertEquals(1, this.player.getTeams().size());
        assertEquals("Team 1", this.player.getTeams().get(0).getName());
    }

    @Test
    public void shouldReturnTrueIfActiveTeamIsEmptyForSavedStatus() {
        this.player.clearActiveTeam();
        assertTrue(this.player.isActiveTeamSaved());
    }

    @Test
    public void shouldCheckIfActiveTeamIsSaved() throws NoActiveTeamException {
        BugemonTeam team = new BugemonTeam("Alpha");
        this.player.setActiveTeam(team);

        assertFalse(this.player.isActiveTeamSaved());

        this.player.addActiveTeamToCache();

        assertTrue(this.player.isActiveTeamSaved());
    }
}
