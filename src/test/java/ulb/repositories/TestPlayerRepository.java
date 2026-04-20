package ulb.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ulb.models.bugemon_team.BugemonTeam;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.TeamMemberDTO;

@RunWith(MockitoJUnitRunner.class)
public class TestPlayerRepository {
    @Mock
    private PlayerRepository repository;

    @Test
    public void shouldSaveAndRetrievePlayerBugemons_whenValidDataProvided() {
        String playername = "test";
        String bugemonName = "1";

        // Create test data
        PlayerBugemonDTO dto = new PlayerBugemonDTO(playername, bugemonName, 10, 20, 15, 100, 50, 5);
        List<PlayerBugemonDTO> expectedBugemons = new ArrayList<>();
        expectedBugemons.add(dto);

        // Configure mock behavior
        when(this.repository.getPlayerBugemons(playername)).thenReturn(expectedBugemons);

        // Execute
        this.repository.savePlayerBugemon(dto);
        List<PlayerBugemonDTO> bugemons = this.repository.getPlayerBugemons(playername);

        // Assert
        assertEquals("L'utilisateur devrait avoir un Bugémon", 1, bugemons.size());

        PlayerBugemonDTO retrieved = bugemons.get(0);
        assertEquals(playername, retrieved.playername());
        assertEquals(bugemonName, retrieved.bugemonName());
        assertEquals(10, retrieved.currentDefense());
        assertEquals(20, retrieved.currentAttackPower());
        assertEquals(15, retrieved.currentInitiative());
        assertEquals(100, retrieved.currentMaxHp());
        assertEquals(50, retrieved.currentXp());
        assertEquals(5, retrieved.currentLevel());

        // Verify the mock was called
        verify(this.repository).savePlayerBugemon(dto);
        verify(this.repository).getPlayerBugemons(playername);
    }

    @Test
    public void shouldUpdatePlayerBugemon_whenDataIsModified() {
        String playername = "test";
        String bugemonName = "2";

        // Initial data
        PlayerBugemonDTO initial = new PlayerBugemonDTO(playername, bugemonName, 10, 10, 10, 50, 0, 1);
        PlayerBugemonDTO updated = new PlayerBugemonDTO(playername, bugemonName, 15, 25, 20, 80, 100, 3);

        List<PlayerBugemonDTO> updatedList = new ArrayList<>();
        updatedList.add(updated);

        // Configure mock behavior
        when(this.repository.getPlayerBugemons(playername)).thenReturn(updatedList);

        // Execute
        this.repository.savePlayerBugemon(initial);
        this.repository.updatePlayerBugemon(updated);
        List<PlayerBugemonDTO> bugemons = this.repository.getPlayerBugemons(playername);

        // Assert
        assertEquals("La liste doit contenir un Bugémon", 1, bugemons.size());

        PlayerBugemonDTO retrieved = bugemons.get(0);
        assertEquals(15, retrieved.currentDefense());
        assertEquals(25, retrieved.currentAttackPower());
        assertEquals(20, retrieved.currentInitiative());
        assertEquals(80, retrieved.currentMaxHp());
        assertEquals(100, retrieved.currentXp());
        assertEquals(3, retrieved.currentLevel());

        // Verify the mock was called
        verify(this.repository).savePlayerBugemon(initial);
        verify(this.repository).updatePlayerBugemon(updated);
        verify(this.repository).getPlayerBugemons(playername);
    }

    @Test
    public void shouldCreateAndRetrieveTeams_whenAddingMultipleTeams() throws Exception {
        String playername = "name";

        // Create test data
        BugemonTeam team1 = new BugemonTeam("team1");
        BugemonTeam team2 = new BugemonTeam("team2");
        List<BugemonTeam> expectedTeams = new ArrayList<>();
        expectedTeams.add(team1);
        expectedTeams.add(team2);

        // Configure mock behavior
        when(this.repository.loadTeams(playername)).thenReturn(expectedTeams);

        // Execute

        this.repository.createTeam(playername, "team1");
        this.repository.createTeam(playername, "team2");

        List<BugemonTeam> teams = this.repository.loadTeams(playername);

        // Assert
        assertEquals(expectedTeams, teams);

        // Verify the mock was called
        verify(this.repository).createTeam(playername, "team1");
        verify(this.repository).createTeam(playername, "team2");
        verify(this.repository).loadTeams(playername);
    }

    @Test
    public void shouldDeleteTeam_whenRequested() throws Exception {
        String playername = "player";
        String teamName = "ToDelete_team";

        // Configure mock behavior for retrieving teams after deletion
        // Empty list after deletion
        when(this.repository.loadTeams(playername)).thenReturn(new ArrayList<>());

        // Execute
        this.repository.createTeam(playername, teamName);

        this.repository.deleteTeam(playername, teamName);
        List<BugemonTeam> teamsAfterDeletion = this.repository.loadTeams(playername);

        // Assert
        assertEquals("L'équipe doit avoir été supprimée", 0, teamsAfterDeletion.size());

        // Verify the mock was called
        verify(this.repository).createTeam(playername, teamName);
        verify(this.repository).deleteTeam(playername, teamName);
        verify(this.repository).loadTeams(playername);
    }

    @Test
    public void shouldAddAndRetrieveTeamMembers_whenFillingRoster() throws Exception {
        String playername = "test";
        String teamName = "Roster_team";
        String bugemonName = "1";

        // Create test data
        TeamMemberDTO member = new TeamMemberDTO(playername, teamName, bugemonName, 1);
        List<TeamMemberDTO> expectedMembers = new ArrayList<>();
        expectedMembers.add(member);

        // Configure mock behavior
        when(this.repository.getTeamMembers(playername, teamName)).thenReturn(expectedMembers);

        // Execute
        this.repository.createTeam(playername, teamName);

        PlayerBugemonDTO bugemon = new PlayerBugemonDTO(playername, bugemonName, 5, 5, 5, 50, 0, 1);
        this.repository.savePlayerBugemon(bugemon);
        this.repository.addTeamMember(member);
        List<TeamMemberDTO> members = this.repository.getTeamMembers(playername, teamName);

        // Assert
        assertEquals("La liste des membres doit contenir 1 élément", 1, members.size());
        assertEquals(bugemonName, members.get(0).bugemonName());
        assertEquals(1, members.get(0).slotPosition());

        // Verify the mock was called
        verify(this.repository).createTeam(playername, teamName);
        verify(this.repository).savePlayerBugemon(bugemon);
        verify(this.repository).addTeamMember(member);
        verify(this.repository).getTeamMembers(playername, teamName);
    }

    @Test
    public void shouldRemoveTeamMember_whenRequested() throws Exception {
        String playername = "name";
        String teamName = "EmptyMe_team";
        String bugemonName = "1";

        // Configure mock behavior to return empty list after removal
        // Empty list after removal
        when(this.repository.getTeamMembers(playername, teamName)).thenReturn(new ArrayList<>());

        // Execute
        this.repository.createTeam(playername, teamName);
        PlayerBugemonDTO bugemon = new PlayerBugemonDTO(playername, bugemonName, 5, 5, 5, 50, 0, 1);
        this.repository.savePlayerBugemon(bugemon);
        TeamMemberDTO member = new TeamMemberDTO(playername, teamName, bugemonName, 1);
        this.repository.addTeamMember(member);
        this.repository.removeTeamMember(playername, teamName, bugemonName);
        List<TeamMemberDTO> members = this.repository.getTeamMembers(playername, teamName);

        // Assert
        assertTrue("La liste des membres devrait être vide après suppression", members.isEmpty());

        // Verify the mock was called
        verify(this.repository).createTeam(playername, teamName);
        verify(this.repository).savePlayerBugemon(bugemon);
        verify(this.repository).addTeamMember(member);
        verify(this.repository).removeTeamMember(playername, teamName, bugemonName);
        verify(this.repository).getTeamMembers(playername, teamName);
    }

    @Test
    public void shouldRenameTeam_whenRequested() throws Exception {
        String playername = "test";
        String oldTeamName = "Old_name_team";
        String newTeamName = "New_name_team";

        // Configure mock behavior for retrieving teams after rename
        when(this.repository.loadTeams(playername)).thenReturn(List.of(new BugemonTeam(newTeamName)));

        // Execute
        this.repository.createTeam(playername, oldTeamName);
        this.repository.renameTeam(playername, oldTeamName, newTeamName);

        List<BugemonTeam> teams = this.repository.loadTeams(playername);

        // Assert
        assertEquals("L'équipe renommée doit être présente", 1, teams.size());
        assertEquals("Le nouveau nom doit être présent", newTeamName, teams.get(0).getName());

        // Verify the mock was called
        verify(this.repository).createTeam(playername, oldTeamName);
        verify(this.repository).renameTeam(playername, oldTeamName, newTeamName);
        verify(this.repository).loadTeams(playername);
    }

    @Test
    public void shouldDeleteTeamMembers_whenRequested() throws Exception {
        String playername = "name";
        String teamName = "ClearMembers_team";
        String bugemonName = "1";

        // Configure mock behavior to return empty list after member deletion
        when(this.repository.getTeamMembers(playername, teamName)).thenReturn(new ArrayList<>());

        // Execute
        this.repository.createTeam(playername, teamName);

        PlayerBugemonDTO bugemon = new PlayerBugemonDTO(playername, bugemonName, 5, 5, 5, 50, 0, 1);
        this.repository.savePlayerBugemon(bugemon);
        TeamMemberDTO member = new TeamMemberDTO(playername, teamName, bugemonName, 1);
        this.repository.addTeamMember(member);
        this.repository.deleteTeam(playername, teamName);
        List<TeamMemberDTO> members = this.repository.getTeamMembers(playername, teamName);

        // Assert
        assertTrue("Les membres de l'équipe doivent avoir été supprimés", members.isEmpty());

        // Verify the mock was called
        verify(this.repository).createTeam(playername, teamName);
        verify(this.repository).savePlayerBugemon(bugemon);
        verify(this.repository).addTeamMember(member);
        verify(this.repository).deleteTeam(playername, teamName);
        verify(this.repository).getTeamMembers(playername, teamName);
    }
}
