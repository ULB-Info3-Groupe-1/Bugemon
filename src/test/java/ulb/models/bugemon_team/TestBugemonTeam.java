/**
 * File name : TestBugemonTeam.java
 * Description : Data class representing a team of Bugemons
 *
 * @author Brisbois Philippe
 * @coauthor Morbee Matteo
 * @date 27 feb. 2026
 * @version 1.1
 */

package ulb.models.bugemon_team;

import static org.junit.Assert.*;

import java.io.InputStream;
import org.junit.Test;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException;
import ulb.utils.Parser;
import ulb.utils.TestUtilsBugemons;

public class TestBugemonTeam {

    @Test
    public void testGetBugemon() {
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");
        BugemonTeam team = new BugemonTeam();
        team.addBugemon(expectedBugemon);

        assertEquals(expectedBugemon, team.getBugemon("1"));
    }

    @Test
    public void testEmptySlot() {
        Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
        BugemonTeam team = new BugemonTeam();
        team.addBugemon(expectedBugemon1);

        assertThrows(IllegalArgumentException.class, () -> {
            team.removeBugemon("0");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            team.getBugemon("0");
        });
    }

    @Test
    public void testAddBugemon() {
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");
        BugemonTeam team = new BugemonTeam();
        team.addBugemon(expectedBugemon);
        Bugemon bugemon = team.getBugemon("1");

        assertEquals(1, team.size());
        assertEquals(expectedBugemon, bugemon);
    }

    @Test
    public void testRemoveBugemon() {
        Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon expectedBugemon2 = TestUtilsBugemons.createDefaultBugemon("2");

        BugemonTeam team = new BugemonTeam();
        team.addBugemon(expectedBugemon1);
        team.removeBugemon(expectedBugemon1);

        assertEquals(0, team.size());

        team.addBugemon(expectedBugemon1);
        team.removeBugemon("1");

        assertEquals(0, team.size());

        team.addBugemon(expectedBugemon1);
        assertThrows(IllegalArgumentException.class, () -> {
            team.removeBugemon(expectedBugemon2);
        });
    }

    @Test
    public void testAddDuplicate() {
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon expectedBugemonDuplicate =
            TestUtilsBugemons.createDefaultBugemon("1");
        BugemonTeam team = new BugemonTeam();

        team.addBugemon(expectedBugemon);

        assertThrows(BugemonAlreadyExistsException.class, () -> {
            team.addBugemon(expectedBugemonDuplicate);
        });
    }

    @Test
    public void testTeamAlreadyEmpty() {
        BugemonTeam team = new BugemonTeam();

        assertThrows(IllegalStateException.class, () -> {
            team.removeBugemon("1");
        });
    }

    @Test
    public void testTeamSize() {
        BugemonTeam team = new BugemonTeam();
        assertTrue(team.isEmpty());

        BugemonTeam fullTeam = TestUtilsBugemons.createDefaultTeam(6);
        assertTrue(fullTeam.isFull());

        Bugemon extraBugemon = TestUtilsBugemons.createDefaultBugemon("7");
        assertThrows(IllegalStateException.class, () -> {
            fullTeam.addBugemon(extraBugemon);
        });
    }

    @Test
    public void testRandomTeamNumber() {
        InputStream attacksStream = getClass().getResourceAsStream(
            "/json/attaques.json"
        );
        InputStream bugemonsStream = getClass().getResourceAsStream(
            "/json/bugemons.json"
        );
        Parser.ParseResult parseResult = Parser.parse(
            attacksStream,
            bugemonsStream
        );
        BugemonTeam teamOfSix = BugemonTeam.createRandomTeam(
            parseResult.getBugemonsList(),
            6
        );
        assertEquals(6, teamOfSix.size());
    }
}
