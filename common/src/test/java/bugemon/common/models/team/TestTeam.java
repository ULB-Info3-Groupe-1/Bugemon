package bugemon.common.models.team;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import bugemon.common.models.player.PlayerBugemon;
import bugemon.common.models.team.exceptions.BugemonAlreadyPresentInTeamException;
import bugemon.common.models.team.exceptions.BugemonNotInTeamException;
import bugemon.common.models.team.exceptions.TeamAlreadyEmptyException;
import bugemon.common.models.team.exceptions.TeamAlreadyFullException;

public class TestTeam {

    private Team team;
    private PlayerBugemon mockBugemon1;
    private PlayerBugemon mockBugemon2;

    @Before
    public void setUp() {
        this.team = new Team();
        this.mockBugemon1 = mock(PlayerBugemon.class);
        this.mockBugemon2 = mock(PlayerBugemon.class);
        when(this.mockBugemon1.getName()).thenReturn("bugemon1");
        when(this.mockBugemon2.getName()).thenReturn("bugemon2");
    }

    @Test
    public void shouldInitializeEmptyTeam_whenDefaultConstructorIsCalled() {
        assertTrue(this.team.isEmpty());
        assertEquals(0, this.team.size());
    }

    @Test
    public void shouldCopyMembersAndName_whenCopyConstructorIsCalled() {
        this.team.add(this.mockBugemon1);
        this.team.setName("My Team");

        Team copiedTeam = new Team(this.team);

        assertEquals(1, copiedTeam.size());
        assertTrue(copiedTeam.contains(this.mockBugemon1));
        assertEquals("My Team", copiedTeam.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowException_whenConstructingWithDuplicateMembers() {
        List<PlayerBugemon> members = Arrays.asList(this.mockBugemon1, this.mockBugemon1);
        new Team(members);
    }

    @Test
    public void shouldReturnTrueForIsEmpty_whenTeamHasNoMembers() {
        assertTrue(this.team.isEmpty());
    }

    @Test
    public void shouldReturnFalseForIsEmpty_whenTeamHasMembers() {
        this.team.add(this.mockBugemon1);
        assertFalse(this.team.isEmpty());
    }

    @Test
    public void shouldAddBugemon_whenTeamIsNotFullAndBugemonNotPresent() {
        this.team.add(this.mockBugemon1);

        assertEquals(1, this.team.size());
        assertTrue(this.team.contains(this.mockBugemon1));
    }

    @Test(expected = BugemonAlreadyPresentInTeamException.class)
    public void shouldThrowBugemonAlreadyPresentInTeamException_whenAddingExistingBugemon() {
        this.team.add(this.mockBugemon1);
        this.team.add(this.mockBugemon1);
    }

    @Test(expected = TeamAlreadyFullException.class)
    public void shouldThrowTeamAlreadyFullException_whenAddingToFullTeam() {
        int count = 0;
        // Fill the team dynamically until it's full
        while (!this.team.isFull()) {
            PlayerBugemon mockB = mock(PlayerBugemon.class);
            when(mockB.getName()).thenReturn("b_" + count);
            this.team.add(mockB);
            count++;
        }

        // This addition should trigger the TeamAlreadyFullException
        this.team.add(this.mockBugemon1);
    }

    @Test
    public void shouldRemoveBugemon_whenBugemonIsInTeam() {
        this.team.add(this.mockBugemon1);
        this.team.remove(this.mockBugemon1);

        assertTrue(this.team.isEmpty());
        assertFalse(this.team.contains(this.mockBugemon1));
    }

    @Test(expected = TeamAlreadyEmptyException.class)
    public void shouldThrowTeamAlreadyEmptyException_whenRemovingFromEmptyTeam() {
        this.team.remove(this.mockBugemon1);
    }

    @Test(expected = BugemonNotInTeamException.class)
    public void shouldThrowBugemonNotInTeamException_whenRemovingNonExistentBugemon() {
        this.team.add(this.mockBugemon1);
        this.team.remove(this.mockBugemon2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldReturnUnmodifiableList_whenGetMembersIsCalled() {
        this.team.add(this.mockBugemon1);
        List<PlayerBugemon> members = this.team.getMembers();

        // Modifying the returned list should throw UnsupportedOperationException
        members.add(this.mockBugemon2);
    }

    @Test
    public void shouldClearMembersAndName_whenClearIsCalled() {
        this.team.add(this.mockBugemon1);
        this.team.setName("A Team");

        this.team.clear();

        assertTrue(this.team.isEmpty());
        assertNull(this.team.getName());
    }

    @Test
    public void shouldSetName_whenSetNameIsCalled() {
        this.team.setName("Champion Team");
        assertEquals("Champion Team", this.team.getName());
    }

    @Test
    public void shouldAddAllBugemons_whenAddAllIsCalledWithValidList() {
        List<PlayerBugemon> newMembers = Arrays.asList(this.mockBugemon1, this.mockBugemon2);

        this.team.addAll(newMembers);

        assertEquals(2, this.team.size());
        assertTrue(this.team.contains(this.mockBugemon1));
        assertTrue(this.team.contains(this.mockBugemon2));
    }

    @Test
    public void shouldReturnTrueForEquals_whenTeamsHaveSameMembersAndName() {
        this.team.add(this.mockBugemon1);
        this.team.setName("Team A");

        Team otherTeam = new Team();
        otherTeam.add(this.mockBugemon1);
        otherTeam.setName("Team A");

        assertEquals(this.team, otherTeam);
        assertEquals(this.team.hashCode(), otherTeam.hashCode());
    }

    @Test
    public void shouldReturnFalseForEquals_whenTeamsHaveDifferentMembersOrName() {
        this.team.add(this.mockBugemon1);
        this.team.setName("Team A");

        Team otherTeam = new Team();
        otherTeam.add(this.mockBugemon2);
        otherTeam.setName("Team A");

        assertNotEquals(this.team, otherTeam);
    }
}
