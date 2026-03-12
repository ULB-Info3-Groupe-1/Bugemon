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

import ulb.factory.TeamFactory;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException;
import ulb.models.bugemon_team.exceptions.BugemonNotInTeamException;
import ulb.models.bugemon_team.exceptions.TeamAlreadyEmptyException;
import ulb.models.bugemon_team.exceptions.TeamAlreadyFullException;
import ulb.utils.Parser;
import ulb.utils.test.TestUtilsBugemons;

public class TestBugemonTeam {
    @Test
    public void testGetBugemon() {
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");
        BugemonTeam team = new BugemonTeam();
        team.addBugemon(expectedBugemon);

        assertEquals(expectedBugemon, team.getBugemon("1").get());
    }

    @Test
    public void testAddBugemon() {
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");
        BugemonTeam team = new BugemonTeam();
        team.addBugemon(expectedBugemon);
        Bugemon bugemon = team.getBugemon("1").get();

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
        assertThrows(BugemonNotInTeamException.class,
                     () -> { team.removeBugemon(expectedBugemon2); });
    }

    @Test
    public void testAddDuplicate() {
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon expectedBugemonDuplicate = TestUtilsBugemons.createDefaultBugemon("1");
        BugemonTeam team = new BugemonTeam();

        team.addBugemon(expectedBugemon);

        assertThrows(BugemonAlreadyExistsException.class,
                     () -> { team.addBugemon(expectedBugemonDuplicate); });
    }

    @Test
    public void testTeamAlreadyEmpty() {
        BugemonTeam team = new BugemonTeam();

        assertThrows(TeamAlreadyEmptyException.class, () -> { team.removeBugemon("1"); });
    }

    @Test
    public void testTeamSize() {
        BugemonTeam team = new BugemonTeam();
        assertTrue(team.isEmpty());

        BugemonTeam fullTeam = TestUtilsBugemons.createDefaultTeam(6);
        assertTrue(fullTeam.isFull());

        Bugemon extraBugemon = TestUtilsBugemons.createDefaultBugemon("7");
        assertThrows(TeamAlreadyFullException.class, () -> { fullTeam.addBugemon(extraBugemon); });
    }

    @Test
    public void testRandomTeamNumber() {
        InputStream attacksStream = getClass().getResourceAsStream("/json/attaques.json");
        InputStream bugemonsStream = getClass().getResourceAsStream("/json/bugemons.json");
        InputStream objectsStream = getClass().getResourceAsStream("/json/objets.json");
        Parser.ParseResult parseResult = Parser.parse(attacksStream, bugemonsStream, objectsStream);
        BugemonTeam teamOfSix = TeamFactory.createRandomTeam(parseResult.getBugemonsList(), 6);
        assertEquals(6, teamOfSix.size());
    }

    @Test
    public void testResetTeam() {
        Bugemon expectedBugemon1 = new Bugemon.Builder().id("1").hp(100).build();
        Bugemon expectedBugemon2 = new Bugemon.Builder().id("2").hp(100).build();

        BugemonTeam team = new BugemonTeam();
        team.addBugemon(expectedBugemon1);
        team.addBugemon(expectedBugemon2);

        expectedBugemon1.takeDamage(50);
        expectedBugemon2.takeDamage(30);

        assertEquals(50, expectedBugemon1.getHp());
        assertEquals(70, expectedBugemon2.getHp());

        team.reset();

        assertEquals(100, expectedBugemon1.getHp());
        assertEquals(100, expectedBugemon2.getHp());
    }
}
