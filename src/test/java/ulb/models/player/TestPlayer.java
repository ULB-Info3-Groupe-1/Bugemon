package ulb.models.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.player.exceptions.NoActiveTeamException;

public class TestPlayer {

    @Test
    public void shouldThrowExceptionWhenNoActiveTeam() {
        Player player = new Player(1);
        Bugemon bugemon = new BugemonBuilder().name("bugemon").build();

        assertThrows(NoActiveTeamException.class, player::getActiveTeamName);
        assertThrows(NoActiveTeamException.class, () -> player.setActiveTeamName("teamName"));
        assertThrows(NoActiveTeamException.class, () -> player.addOrRemoveBugemonOfActiveTeam(bugemon));
    }

    @Test
    public void shouldSetActiveTeamWithCopy() {
        Player player = new Player(1);
        BugemonTeam team = new BugemonTeam("Original");
        player.setActiveTeam(team);

        assertTrue(player.getActiveTeam().isPresent());
        assertEquals("Original", player.getActiveTeam().get().getName());

        team.setName("Modified");
        assertNotEquals(team.getName(), player.getActiveTeam().get().getName());
    }
}
