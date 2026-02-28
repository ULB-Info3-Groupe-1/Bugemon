package ulb.models.bugemon_team;

import static org.junit.Assert.*;

import org.junit.Test;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException;
import ulb.utils.TestUtilsBugemons;

public class TestBugemonTeam {

    @Test
    public void testGetBugemon() {
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");
        BugemonTeam team = new BugemonTeam();
        team.addBugemon(expectedBugemon);

        assertEquals(expectedBugemon, team.getBugemon(0));
    }

    @Test
    public void testIndexBugemon() {
        Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon expectedBugemon2 = TestUtilsBugemons.createDefaultBugemon("2");
        Bugemon expectedBugemon3 = TestUtilsBugemons.createDefaultBugemon("3");

        BugemonTeam team = new BugemonTeam();

        team.addBugemon(expectedBugemon1);
        team.addBugemon(expectedBugemon2);
        team.addBugemon(expectedBugemon3);

        assertEquals(expectedBugemon1, team.getBugemon(0));
        assertEquals(expectedBugemon2, team.getBugemon(1));
        assertEquals(expectedBugemon3, team.getBugemon(2));
    }

    @Test
    public void testIndexOutOfBound() {
        Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
        BugemonTeam team = new BugemonTeam();
        team.addBugemon(expectedBugemon1);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            team.removeBugemon(6);
        });

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            team.getBugemon(6);
        });
    }

    @Test
    public void testEmptySlot() {
        Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
        BugemonTeam team = new BugemonTeam();
        team.addBugemon(expectedBugemon1);

        assertThrows(IllegalArgumentException.class, () -> {
            team.removeBugemon(1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            team.getBugemon(1);
        });
    }

    @Test
    public void testAddBugemon() {
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");
        BugemonTeam team = new BugemonTeam();
        team.addBugemon(expectedBugemon);
        Bugemon bugemon = team.getBugemon(0);

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
        team.removeBugemon(0);

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
            team.removeBugemon(1);
        });
    }

    @Test
    public void testTeamSize() {
        Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon expectedBugemon2 = TestUtilsBugemons.createDefaultBugemon("2");
        Bugemon expectedBugemon3 = TestUtilsBugemons.createDefaultBugemon("3");
        Bugemon expectedBugemon4 = TestUtilsBugemons.createDefaultBugemon("4");
        Bugemon expectedBugemon5 = TestUtilsBugemons.createDefaultBugemon("5");
        Bugemon expectedBugemon6 = TestUtilsBugemons.createDefaultBugemon("6");
        Bugemon expectedBugemon7 = TestUtilsBugemons.createDefaultBugemon("7");

        BugemonTeam team = new BugemonTeam();

        assertTrue(team.isEmpty());

        team.addBugemon(expectedBugemon1);
        team.addBugemon(expectedBugemon2);
        team.addBugemon(expectedBugemon3);
        team.addBugemon(expectedBugemon4);
        team.addBugemon(expectedBugemon5);
        team.addBugemon(expectedBugemon6);

        assertTrue(team.isFull());

        assertThrows(IllegalStateException.class, () -> {
            team.addBugemon(expectedBugemon7);
        });
    }

    @Test
    public void testTeam() {
        Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon expectedBugemon2 = TestUtilsBugemons.createDefaultBugemon("2");
        Bugemon expectedBugemon3 = TestUtilsBugemons.createDefaultBugemon("3");

        BugemonTeam team = new BugemonTeam();

        team.addBugemon(expectedBugemon1);
        team.addBugemon(expectedBugemon2);
        team.addBugemon(expectedBugemon3);

        Bugemon[] expectedTeam = {
            expectedBugemon1,
            expectedBugemon2,
            expectedBugemon3,
        };
        Bugemon[] teamClone = team.getTeam();

        for (int i = 0; i < 3; i++) {
            assertEquals(expectedTeam[i].getId(), teamClone[i].getId());
        }
    }
}
