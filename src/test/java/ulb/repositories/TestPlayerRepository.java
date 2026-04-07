package ulb.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.TeamDTO;
import ulb.repositories.dto.TeamMemberDTO;

@RunWith(MockitoJUnitRunner.class)
public class TestPlayerRepository {
    @Mock
    private PlayerRepository repository;

    @Test
    public void shouldCreateAndRetrievePlayer_whenValidPlayernameProvided() {
        String playername = "Player_test123";
        int expectedPlayerId = 1;

        // Configure mock behavior
        when(this.repository.createPlayer(playername)).thenReturn(expectedPlayerId);
        when(this.repository.getPlayerIdByPlayername(playername)).thenReturn(Optional.of(expectedPlayerId));

        // Execute
        int playerId = this.repository.createPlayer(playername);
        Optional<Integer> retrievedId = this.repository.getPlayerIdByPlayername(playername);

        // Assert
        assertTrue("L'ID utilisateur doit être valide (supérieur à 0)", playerId > 0);
        assertTrue("L'utilisateur devrait être trouvé dans la BD", retrievedId.isPresent());
        assertEquals("L'ID récupéré doit correspondre à celui créé", playerId, retrievedId.get().intValue());

        // Verify the mock was called
        verify(this.repository).createPlayer(playername);
        verify(this.repository).getPlayerIdByPlayername(playername);
    }

    @Test
    public void shouldReturnEmpty_whenPlayerDoesNotExist() {
        String unknownPlayername = "Unknown_test456";

        // Configure mock to return empty
        when(this.repository.getPlayerIdByPlayername(unknownPlayername)).thenReturn(Optional.empty());

        // Execute
        Optional<Integer> retrievedId = this.repository.getPlayerIdByPlayername(unknownPlayername);

        // Assert
        assertFalse("L'utilisateur ne devrait pas exister", retrievedId.isPresent());
        verify(this.repository).getPlayerIdByPlayername(unknownPlayername);
    }

    @Test
    public void shouldSaveAndRetrievePlayerBugemons_whenValidDataProvided() {
        int playerId = 2;
        String bugemonName = "1";

        // Create test data
        PlayerBugemonDTO dto = new PlayerBugemonDTO(playerId, bugemonName, 10, 20, 15, 100, 50, 5);
        List<PlayerBugemonDTO> expectedBugemons = new ArrayList<>();
        expectedBugemons.add(dto);

        // Configure mock behavior
        when(this.repository.getPlayerBugemons(playerId)).thenReturn(expectedBugemons);

        // Execute
        this.repository.savePlayerBugemon(dto);
        List<PlayerBugemonDTO> bugemons = this.repository.getPlayerBugemons(playerId);

        // Assert
        assertEquals("L'utilisateur devrait avoir un Bugémon", 1, bugemons.size());

        PlayerBugemonDTO retrieved = bugemons.get(0);
        assertEquals(playerId, retrieved.playerId());
        assertEquals(bugemonName, retrieved.bugemonName());
        assertEquals(10, retrieved.currentDefense());
        assertEquals(20, retrieved.currentAttackPower());
        assertEquals(15, retrieved.currentInitiative());
        assertEquals(100, retrieved.currentMaxHp());
        assertEquals(50, retrieved.currentXp());
        assertEquals(5, retrieved.currentLevel());

        // Verify the mock was called
        verify(this.repository).savePlayerBugemon(dto);
        verify(this.repository).getPlayerBugemons(playerId);
    }

    @Test
    public void shouldUpdatePlayerBugemon_whenDataIsModified() {
        int playerId = 3;
        String bugemonName = "2";

        // Initial data
        PlayerBugemonDTO initial = new PlayerBugemonDTO(playerId, bugemonName, 10, 10, 10, 50, 0, 1);
        PlayerBugemonDTO updated = new PlayerBugemonDTO(playerId, bugemonName, 15, 25, 20, 80, 100, 3);

        List<PlayerBugemonDTO> updatedList = new ArrayList<>();
        updatedList.add(updated);

        // Configure mock behavior
        when(this.repository.getPlayerBugemons(playerId)).thenReturn(updatedList);

        // Execute
        this.repository.savePlayerBugemon(initial);
        this.repository.updatePlayerBugemon(updated);
        List<PlayerBugemonDTO> bugemons = this.repository.getPlayerBugemons(playerId);

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
        verify(this.repository).getPlayerBugemons(playerId);
    }

    @Test
    public void shouldCreateAndRetrieveTeams_whenAddingMultipleTeams() {
        int playerId = 4;
        String team1 = "Alpha_team";
        String team2 = "Beta_team";

        // Create test data
        TeamDTO dto1 = new TeamDTO(playerId, team1);
        TeamDTO dto2 = new TeamDTO(playerId, team2);
        List<TeamDTO> expectedTeams = new ArrayList<>();
        expectedTeams.add(dto1);
        expectedTeams.add(dto2);

        // Configure mock behavior
        when(this.repository.getPlayerTeams(playerId)).thenReturn(expectedTeams);

        // Execute
        this.repository.createTeam(playerId, team1);
        this.repository.createTeam(playerId, team2);
        List<TeamDTO> teams = this.repository.getPlayerTeams(playerId);

        // Assert
        assertEquals("Il devrait y avoir deux équipes", 2, teams.size());

        boolean foundTeam1 = teams.stream().anyMatch(t -> t.name().equals(team1));
        boolean foundTeam2 = teams.stream().anyMatch(t -> t.name().equals(team2));

        assertTrue("L'équipe Alpha doit être trouvée", foundTeam1);
        assertTrue("L'équipe Beta doit être trouvée", foundTeam2);

        // Verify the mock was called
        verify(this.repository).createTeam(playerId, team1);
        verify(this.repository).createTeam(playerId, team2);
        verify(this.repository).getPlayerTeams(playerId);
    }

    @Test
    public void shouldDeleteTeam_whenRequested() {
        int playerId = 5;
        String teamName = "ToDelete_team";

        // Configure mock behavior for retrieving teams after deletion
        // Empty list after deletion
        when(this.repository.getPlayerTeams(playerId)).thenReturn(new ArrayList<>());

        // Execute
        this.repository.createTeam(playerId, teamName);
        this.repository.deleteTeam(playerId, teamName);
        List<TeamDTO> teamsAfterDeletion = this.repository.getPlayerTeams(playerId);

        // Assert
        assertEquals("L'équipe doit avoir été supprimée", 0, teamsAfterDeletion.size());

        // Verify the mock was called
        verify(this.repository).createTeam(playerId, teamName);
        verify(this.repository).deleteTeam(playerId, teamName);
        verify(this.repository).getPlayerTeams(playerId);
    }

    @Test
    public void shouldAddAndRetrieveTeamMembers_whenFillingRoster() {
        int plyerId = 6;
        String teamName = "Roster_team";
        String bugemonName = "1";

        // Create test data
        TeamMemberDTO member = new TeamMemberDTO(plyerId, teamName, bugemonName, 1);
        List<TeamMemberDTO> expectedMembers = new ArrayList<>();
        expectedMembers.add(member);

        // Configure mock behavior
        when(this.repository.getTeamMembers(plyerId, teamName)).thenReturn(expectedMembers);

        // Execute
        this.repository.createTeam(plyerId, teamName);
        PlayerBugemonDTO bugemon = new PlayerBugemonDTO(plyerId, bugemonName, 5, 5, 5, 50, 0, 1);
        this.repository.savePlayerBugemon(bugemon);
        this.repository.addTeamMember(member);
        List<TeamMemberDTO> members = this.repository.getTeamMembers(plyerId, teamName);

        // Assert
        assertEquals("La liste des membres doit contenir 1 élément", 1, members.size());
        assertEquals(bugemonName, members.get(0).bugemonName());
        assertEquals(1, members.get(0).slotPosition());

        // Verify the mock was called
        verify(this.repository).createTeam(plyerId, teamName);
        verify(this.repository).savePlayerBugemon(bugemon);
        verify(this.repository).addTeamMember(member);
        verify(this.repository).getTeamMembers(plyerId, teamName);
    }

    @Test
    public void shouldRemoveTeamMember_whenRequested() {
        int plyerId = 7;
        String teamName = "EmptyMe_team";
        String bugemonName = "1";

        // Configure mock behavior to return empty list after removal
        // Empty list after removal
        when(this.repository.getTeamMembers(plyerId, teamName)).thenReturn(new ArrayList<>());

        // Execute
        this.repository.createTeam(plyerId, teamName);
        PlayerBugemonDTO bugemon = new PlayerBugemonDTO(plyerId, bugemonName, 5, 5, 5, 50, 0, 1);
        this.repository.savePlayerBugemon(bugemon);
        TeamMemberDTO member = new TeamMemberDTO(plyerId, teamName, bugemonName, 1);
        this.repository.addTeamMember(member);
        this.repository.removeTeamMember(plyerId, teamName, bugemonName);
        List<TeamMemberDTO> members = this.repository.getTeamMembers(plyerId, teamName);

        // Assert
        assertTrue("La liste des membres devrait être vide après suppression", members.isEmpty());

        // Verify the mock was called
        verify(this.repository).createTeam(plyerId, teamName);
        verify(this.repository).savePlayerBugemon(bugemon);
        verify(this.repository).addTeamMember(member);
        verify(this.repository).removeTeamMember(plyerId, teamName, bugemonName);
        verify(this.repository).getTeamMembers(plyerId, teamName);
    }

    @Test
    public void shouldRenameTeam_whenRequested() {
        int playerId = 8;
        String oldTeamName = "Old_name_team";
        String newTeamName = "New_name_team";

        // Configure mock behavior for retrieving teams after rename
        when(this.repository.getPlayerTeams(playerId)).thenReturn(List.of(new TeamDTO(playerId, newTeamName)));

        // Execute
        this.repository.createTeam(playerId, oldTeamName);
        this.repository.renameTeam(playerId, oldTeamName, newTeamName);
        List<TeamDTO> teams = this.repository.getPlayerTeams(playerId);

        // Assert
        assertEquals("L'équipe renommée doit être présente", 1, teams.size());
        assertEquals("Le nouveau nom doit être présent", newTeamName, teams.get(0).name());

        // Verify the mock was called
        verify(this.repository).createTeam(playerId, oldTeamName);
        verify(this.repository).renameTeam(playerId, oldTeamName, newTeamName);
        verify(this.repository).getPlayerTeams(playerId);
    }

    @Test
    public void shouldDeleteTeamMembers_whenRequested() {
        int playerId = 9;
        String teamName = "ClearMembers_team";
        String bugemonName = "1";

        // Configure mock behavior to return empty list after member deletion
        when(this.repository.getTeamMembers(playerId, teamName)).thenReturn(new ArrayList<>());

        // Execute
        this.repository.createTeam(playerId, teamName);
        PlayerBugemonDTO bugemon = new PlayerBugemonDTO(playerId, bugemonName, 5, 5, 5, 50, 0, 1);
        this.repository.savePlayerBugemon(bugemon);
        TeamMemberDTO member = new TeamMemberDTO(playerId, teamName, bugemonName, 1);
        this.repository.addTeamMember(member);
        this.repository.deleteTeamMembers(playerId, teamName);
        List<TeamMemberDTO> members = this.repository.getTeamMembers(playerId, teamName);

        // Assert
        assertTrue("Les membres de l'équipe doivent avoir été supprimés", members.isEmpty());

        // Verify the mock was called
        verify(this.repository).createTeam(playerId, teamName);
        verify(this.repository).savePlayerBugemon(bugemon);
        verify(this.repository).addTeamMember(member);
        verify(this.repository).deleteTeamMembers(playerId, teamName);
        verify(this.repository).getTeamMembers(playerId, teamName);
    }
}
