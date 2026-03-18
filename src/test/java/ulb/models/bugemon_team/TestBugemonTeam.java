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

import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException;
import ulb.models.bugemon_team.exceptions.BugemonNotInTeamException;
import ulb.models.bugemon_team.exceptions.TeamAlreadyEmptyException;
import ulb.models.bugemon_team.exceptions.TeamAlreadyFullException;
import ulb.utils.test.TestUtilsBugemons;

public class TestBugemonTeam {
    @Test
    public void testGetBugemon() {
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");
        BugemonTeam team = new BugemonTeam();
        team.add(expectedBugemon);

        assertEquals(expectedBugemon, team.get("1").get());
    }

    @Test
    public void testAddBugemon() {
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");
        BugemonTeam team = new BugemonTeam();
        team.add(expectedBugemon);
        Bugemon bugemon = team.get("1").get();

        assertEquals(1, team.size());
        assertEquals(expectedBugemon, bugemon);
    }

    @Test
    public void testRemoveBugemon() {
        Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon expectedBugemon2 = TestUtilsBugemons.createDefaultBugemon("2");

        BugemonTeam team = new BugemonTeam();
        team.add(expectedBugemon1);
        team.remove(expectedBugemon1);

        assertEquals(0, team.size());

        team.add(expectedBugemon1);
        assertThrows(BugemonNotInTeamException.class, () -> { team.remove(expectedBugemon2); });
    }

    @Test
    public void testAddDuplicate() {
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon expectedBugemonDuplicate = TestUtilsBugemons.createDefaultBugemon("1");
        BugemonTeam team = new BugemonTeam();

        team.add(expectedBugemon);

        assertThrows(BugemonAlreadyExistsException.class,
                     () -> { team.add(expectedBugemonDuplicate); });
    }

    @Test
    public void testTeamAlreadyEmpty() {
        BugemonTeam team = new BugemonTeam();
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");

        assertThrows(TeamAlreadyEmptyException.class, () -> { team.remove(expectedBugemon); });
    }

    @Test
    public void testTeamSize() {
        BugemonTeam team = new BugemonTeam();
        assertTrue(team.isEmpty());

        BugemonTeam fullTeam = TestUtilsBugemons.createDefaultTeam(6);
        assertTrue(fullTeam.isFull());

        Bugemon extraBugemon = TestUtilsBugemons.createDefaultBugemon("7");
        assertThrows(TeamAlreadyFullException.class, () -> { fullTeam.add(extraBugemon); });
    }

    @Test
    public void testClearTeam() {
        Bugemon expectedBugemon1 = new BugemonBuilder().id("1").hp(100).build();
        Bugemon expectedBugemon2 = new BugemonBuilder().id("2").hp(100).build();

        BugemonTeam team = new BugemonTeam();
        team.add(expectedBugemon1);
        team.add(expectedBugemon2);

        team.clear();

        assertTrue(team.isEmpty());
    }
}
