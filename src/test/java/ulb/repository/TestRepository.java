package ulb.repository;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ulb.repository.dto.TeamDTO;
import ulb.repository.dto.TeamMemberDTO;
import ulb.repository.dto.UserBugemonDTO;

@RunWith(MockitoJUnitRunner.class)
public class TestRepository {
    @Mock private DatabaseRepository repository;

    @Test
    public void shouldCreateAndRetrieveUser_whenValidUsernameProvided() {
        String username = "User_test123";
        int expectedUserId = 1;

        // Configure mock behavior
        when(this.repository.createUser(username)).thenReturn(expectedUserId);
        when(this.repository.getUserIdByUsername(username)).thenReturn(Optional.of(expectedUserId));

        // Execute
        int userId = this.repository.createUser(username);
        Optional<Integer> retrievedId = this.repository.getUserIdByUsername(username);

        // Assert
        assertTrue("L'ID utilisateur doit être valide (supérieur à 0)", userId > 0);
        assertTrue("L'utilisateur devrait être trouvé dans la BD", retrievedId.isPresent());
        assertEquals("L'ID récupéré doit correspondre à celui créé", userId,
                     retrievedId.get().intValue());

        // Verify the mock was called
        verify(this.repository).createUser(username);
        verify(this.repository).getUserIdByUsername(username);
    }

    @Test
    public void shouldReturnEmpty_whenUserDoesNotExist() {
        String unknownUsername = "Unknown_test456";

        // Configure mock to return empty
        when(this.repository.getUserIdByUsername(unknownUsername)).thenReturn(Optional.empty());

        // Execute
        Optional<Integer> retrievedId = this.repository.getUserIdByUsername(unknownUsername);

        // Assert
        assertFalse("L'utilisateur ne devrait pas exister", retrievedId.isPresent());
        verify(this.repository).getUserIdByUsername(unknownUsername);
    }

    @Test
    public void shouldSaveAndRetrieveUserBugemons_whenValidDataProvided() {
        int userId = 2;
        String bugemonId = "bug_test001";

        // Create test data
        UserBugemonDTO dto = new UserBugemonDTO(userId, bugemonId, 10, 20, 15, 100, 50, 5);
        List<UserBugemonDTO> expectedBugemons = new ArrayList<>();
        expectedBugemons.add(dto);

        // Configure mock behavior
        when(this.repository.getUserBugemons(userId)).thenReturn(expectedBugemons);

        // Execute
        this.repository.saveUserBugemon(dto);
        List<UserBugemonDTO> bugemons = this.repository.getUserBugemons(userId);

        // Assert
        assertEquals("L'utilisateur devrait avoir un Bugémon", 1, bugemons.size());

        UserBugemonDTO retrieved = bugemons.get(0);
        assertEquals(userId, retrieved.userId());
        assertEquals(bugemonId, retrieved.bugemonId());
        assertEquals(10, retrieved.currentDefense());
        assertEquals(20, retrieved.currentAttackPower());
        assertEquals(15, retrieved.currentInitiative());
        assertEquals(100, retrieved.currentMaxHp());
        assertEquals(50, retrieved.currentXp());
        assertEquals(5, retrieved.currentLevel());

        // Verify the mock was called
        verify(this.repository).saveUserBugemon(dto);
        verify(this.repository).getUserBugemons(userId);
    }

    @Test
    public void shouldUpdateUserBugemon_whenDataIsModified() {
        int userId = 3;
        String bugemonId = "bugU_test002";

        // Initial data
        UserBugemonDTO initial = new UserBugemonDTO(userId, bugemonId, 10, 10, 10, 50, 0, 1);
        UserBugemonDTO updated = new UserBugemonDTO(userId, bugemonId, 15, 25, 20, 80, 100, 3);

        List<UserBugemonDTO> updatedList = new ArrayList<>();
        updatedList.add(updated);

        // Configure mock behavior
        when(this.repository.getUserBugemons(userId)).thenReturn(updatedList);

        // Execute
        this.repository.saveUserBugemon(initial);
        this.repository.updateUserBugemon(updated);
        List<UserBugemonDTO> bugemons = this.repository.getUserBugemons(userId);

        // Assert
        assertEquals("La liste doit contenir un Bugémon", 1, bugemons.size());

        UserBugemonDTO retrieved = bugemons.get(0);
        assertEquals(15, retrieved.currentDefense());
        assertEquals(25, retrieved.currentAttackPower());
        assertEquals(20, retrieved.currentInitiative());
        assertEquals(80, retrieved.currentMaxHp());
        assertEquals(100, retrieved.currentXp());
        assertEquals(3, retrieved.currentLevel());

        // Verify the mock was called
        verify(this.repository).saveUserBugemon(initial);
        verify(this.repository).updateUserBugemon(updated);
        verify(this.repository).getUserBugemons(userId);
    }

    @Test
    public void shouldCreateAndRetrieveTeams_whenAddingMultipleTeams() {
        int userId = 4;
        String team1 = "Alpha_team";
        String team2 = "Beta_team";

        // Create test data
        TeamDTO dto1 = new TeamDTO(userId, team1);
        TeamDTO dto2 = new TeamDTO(userId, team2);
        List<TeamDTO> expectedTeams = new ArrayList<>();
        expectedTeams.add(dto1);
        expectedTeams.add(dto2);

        // Configure mock behavior
        when(this.repository.getUserTeams(userId)).thenReturn(expectedTeams);

        // Execute
        this.repository.createTeam(userId, team1);
        this.repository.createTeam(userId, team2);
        List<TeamDTO> teams = this.repository.getUserTeams(userId);

        // Assert
        assertEquals("Il devrait y avoir deux équipes", 2, teams.size());

        boolean foundTeam1 = teams.stream().anyMatch(t -> t.name().equals(team1));
        boolean foundTeam2 = teams.stream().anyMatch(t -> t.name().equals(team2));

        assertTrue("L'équipe Alpha doit être trouvée", foundTeam1);
        assertTrue("L'équipe Beta doit être trouvée", foundTeam2);

        // Verify the mock was called
        verify(this.repository).createTeam(userId, team1);
        verify(this.repository).createTeam(userId, team2);
        verify(this.repository).getUserTeams(userId);
    }

    @Test
    public void shouldDeleteTeam_whenRequested() {
        int userId = 5;
        String teamName = "ToDelete_team";

        // Configure mock behavior for retrieving teams after deletion
        when(this.repository.getUserTeams(userId))
                .thenReturn(new ArrayList<>()); // Empty list after deletion

        // Execute
        this.repository.createTeam(userId, teamName);
        this.repository.deleteTeam(userId, teamName);
        List<TeamDTO> teamsAfterDeletion = this.repository.getUserTeams(userId);

        // Assert
        assertEquals("L'équipe doit avoir été supprimée", 0, teamsAfterDeletion.size());

        // Verify the mock was called
        verify(this.repository).createTeam(userId, teamName);
        verify(this.repository).deleteTeam(userId, teamName);
        verify(this.repository).getUserTeams(userId);
    }

    @Test
    public void shouldAddAndRetrieveTeamMembers_whenFillingRoster() {
        int userId = 6;
        String teamName = "Roster_team";
        String bugemonId = "partner_001";

        // Create test data
        TeamMemberDTO member = new TeamMemberDTO(userId, teamName, bugemonId, 1);
        List<TeamMemberDTO> expectedMembers = new ArrayList<>();
        expectedMembers.add(member);

        // Configure mock behavior
        when(this.repository.getTeamMembers(userId, teamName)).thenReturn(expectedMembers);

        // Execute
        this.repository.createTeam(userId, teamName);
        UserBugemonDTO bugemon = new UserBugemonDTO(userId, bugemonId, 5, 5, 5, 50, 0, 1);
        this.repository.saveUserBugemon(bugemon);
        this.repository.addTeamMember(member);
        List<TeamMemberDTO> members = this.repository.getTeamMembers(userId, teamName);

        // Assert
        assertEquals("La liste des membres doit contenir 1 élément", 1, members.size());
        assertEquals(bugemonId, members.get(0).bugemonId());
        assertEquals(1, members.get(0).slotPosition());

        // Verify the mock was called
        verify(this.repository).createTeam(userId, teamName);
        verify(this.repository).saveUserBugemon(bugemon);
        verify(this.repository).addTeamMember(member);
        verify(this.repository).getTeamMembers(userId, teamName);
    }

    @Test
    public void shouldRemoveTeamMember_whenRequested() {
        int userId = 7;
        String teamName = "EmptyMe_team";
        String bugemonId = "leave_001";

        // Configure mock behavior to return empty list after removal
        when(this.repository.getTeamMembers(userId, teamName))
                .thenReturn(new ArrayList<>()); // Empty list after removal

        // Execute
        this.repository.createTeam(userId, teamName);
        UserBugemonDTO bugemon = new UserBugemonDTO(userId, bugemonId, 5, 5, 5, 50, 0, 1);
        this.repository.saveUserBugemon(bugemon);
        TeamMemberDTO member = new TeamMemberDTO(userId, teamName, bugemonId, 1);
        this.repository.addTeamMember(member);
        this.repository.removeTeamMember(userId, teamName, bugemonId);
        List<TeamMemberDTO> members = this.repository.getTeamMembers(userId, teamName);

        // Assert
        assertTrue("La liste des membres devrait être vide après suppression", members.isEmpty());

        // Verify the mock was called
        verify(this.repository).createTeam(userId, teamName);
        verify(this.repository).saveUserBugemon(bugemon);
        verify(this.repository).addTeamMember(member);
        verify(this.repository).removeTeamMember(userId, teamName, bugemonId);
        verify(this.repository).getTeamMembers(userId, teamName);
    }

    @Test
    public void shouldRenameTeam_whenRequested() {
        int userId = 8;
        String oldTeamName = "Old_name_team";
        String newTeamName = "New_name_team";

        // Configure mock behavior for retrieving teams after rename
        when(this.repository.getUserTeams(userId))
                .thenReturn(List.of(new TeamDTO(userId, newTeamName)));

        // Execute
        this.repository.createTeam(userId, oldTeamName);
        this.repository.renameTeam(userId, oldTeamName, newTeamName);
        List<TeamDTO> teams = this.repository.getUserTeams(userId);

        // Assert
        assertEquals("L'équipe renommée doit être présente", 1, teams.size());
        assertEquals("Le nouveau nom doit être présent", newTeamName, teams.get(0).name());

        // Verify the mock was called
        verify(this.repository).createTeam(userId, oldTeamName);
        verify(this.repository).renameTeam(userId, oldTeamName, newTeamName);
        verify(this.repository).getUserTeams(userId);
    }

    @Test
    public void shouldDeleteTeamMembers_whenRequested() {
        int userId = 9;
        String teamName = "ClearMembers_team";
        String bugemonId = "clear_001";

        // Configure mock behavior to return empty list after member deletion
        when(this.repository.getTeamMembers(userId, teamName)).thenReturn(new ArrayList<>());

        // Execute
        this.repository.createTeam(userId, teamName);
        UserBugemonDTO bugemon = new UserBugemonDTO(userId, bugemonId, 5, 5, 5, 50, 0, 1);
        this.repository.saveUserBugemon(bugemon);
        TeamMemberDTO member = new TeamMemberDTO(userId, teamName, bugemonId, 1);
        this.repository.addTeamMember(member);
        this.repository.deleteTeamMembers(userId, teamName);
        List<TeamMemberDTO> members = this.repository.getTeamMembers(userId, teamName);

        // Assert
        assertTrue("Les membres de l'équipe doivent avoir été supprimés", members.isEmpty());

        // Verify the mock was called
        verify(this.repository).createTeam(userId, teamName);
        verify(this.repository).saveUserBugemon(bugemon);
        verify(this.repository).addTeamMember(member);
        verify(this.repository).deleteTeamMembers(userId, teamName);
        verify(this.repository).getTeamMembers(userId, teamName);
    }
}
