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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.exceptions.BugemonAlreadyPresentInTeamException;
import ulb.models.bugemon_team.exceptions.BugemonNotInTeamException;
import ulb.models.bugemon_team.exceptions.TeamAlreadyEmptyException;
import ulb.models.bugemon_team.exceptions.TeamAlreadyFullException;
import ulb.utils.test.TestUtilsBugemons;

public class TestBugemonTeam {
    @Test
    public void testGetBugemon() {
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");
        Team team = new Team();
        team.add(expectedBugemon);

        assertEquals(expectedBugemon, team.get("1").get());
    }

    @Test
    public void testAddBugemon() {
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");
        Team team = new Team();
        team.add(expectedBugemon);
        Bugemon bugemon = team.get("1").get();

        assertEquals(1, team.size());
        assertEquals(expectedBugemon, bugemon);
    }

    @Test
    public void testRemoveBugemon() {
        Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");

        Team team = new Team();
        team.add(expectedBugemon1);
        team.remove(expectedBugemon1);

        Bugemon expectedBugemon2 = TestUtilsBugemons.createDefaultBugemon("2");

        assertEquals(0, team.size());

        team.add(expectedBugemon1);
        assertThrows(BugemonNotInTeamException.class, () -> {
            team.remove(expectedBugemon2);
        });
    }

    @Test
    public void testAddDuplicate() {
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon expectedBugemonDuplicate = TestUtilsBugemons.createDefaultBugemon("1");
        Team team = new Team();

        team.add(expectedBugemon);

        assertThrows(BugemonAlreadyPresentInTeamException.class, () -> {
            team.add(expectedBugemonDuplicate);
        });
    }

    @Test
    public void testTeamAlreadyEmpty() {
        Team team = new Team();
        Bugemon expectedBugemon = TestUtilsBugemons.createDefaultBugemon("1");

        assertThrows(TeamAlreadyEmptyException.class, () -> {
            team.remove(expectedBugemon);
        });
    }

    @Test
    public void testTeamSize() {
        Team team = new Team();
        assertTrue(team.isEmpty());

        Team fullTeam = TestUtilsBugemons.createDefaultTeam(6);
        assertTrue(fullTeam.isFull());

        Bugemon extraBugemon = TestUtilsBugemons.createDefaultBugemon("7");
        assertThrows(TeamAlreadyFullException.class, () -> {
            fullTeam.add(extraBugemon);
        });
    }

    @Test
    public void testClearTeam() {
        Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon expectedBugemon2 = TestUtilsBugemons.createDefaultBugemon("2");

        Team team = new Team();
        team.add(expectedBugemon1);
        team.add(expectedBugemon2);

        team.clear();

        assertTrue(team.isEmpty());
    }

    @Test
    public void testAddAll() {
        Team team1 = new Team();
        for (int i = 1; i <= 3; i++) {
            team1.add(TestUtilsBugemons.createDefaultBugemon(String.valueOf(i)));
        }

        Team team2 = new Team();
        for (int i = 4; i <= 6; i++) {
            team2.add(TestUtilsBugemons.createDefaultBugemon(String.valueOf(i)));
        }

        team1.addAll(team2);
        assertEquals(6, team1.size());

        Bugemon extraBugemon = TestUtilsBugemons.createDefaultBugemon("9");
        assertThrows(TeamAlreadyFullException.class, () -> {
            team1.add(extraBugemon);
        });

        Team team3 = new Team();
        for (int i = 7; i <= 11; i++) {
            team3.add(TestUtilsBugemons.createDefaultBugemon(String.valueOf(i)));
        }
        assertThrows(TeamAlreadyFullException.class, () -> {
            team3.addAll(team2);
        });

        Bugemon pikachu = TestUtilsBugemons.createDefaultBugemon("Pikachu");
        Team team4 = new Team();
        team4.add(pikachu);
        Team team5 = new Team();
        team5.add(pikachu);
        assertThrows(BugemonAlreadyPresentInTeamException.class, () -> {
            team4.addAll(team5);
        });
    }
}
