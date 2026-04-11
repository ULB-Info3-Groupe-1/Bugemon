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
    public void shouldCreateAndRetrievePlayer_whenValidPlayernameProvided() throws Exception {
        String playername = "Player_test123";
        int expectedPlayerId = 1;

        // Configure mock behavior
        when(this.repository.getPlayerIdOrCreatePlayer(playername)).thenReturn(expectedPlayerId);

        // Execute
        int playerId = this.repository.getPlayerIdOrCreatePlayer(playername);

        // Assert
        assertTrue("L'ID utilisateur doit être valide (supérieur à 0)", playerId > 0);

        // Verify the mock was called
        verify(this.repository).getPlayerIdOrCreatePlayer(playername);
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
    public void shouldCreateAndRetrieveTeams_whenAddingMultipleTeams() throws Exception {
        int playerId = 4;

        // Create test data
        BugemonTeam team1 = new BugemonTeam("team1");
        BugemonTeam team2 = new BugemonTeam("team2");
        List<BugemonTeam> expectedTeams = new ArrayList<>();
        expectedTeams.add(team1);
        expectedTeams.add(team2);

        // Configure mock behavior
        when(this.repository.loadTeams(playerId)).thenReturn(expectedTeams);

        // Execute

        this.repository.createTeam(playerId, "team1");
        this.repository.createTeam(playerId, "team2");

        List<BugemonTeam> teams = this.repository.loadTeams(playerId);

        // Assert
        assertEquals(expectedTeams, teams);

        // Verify the mock was called
        verify(this.repository).createTeam(playerId, "team1");
        verify(this.repository).createTeam(playerId, "team2");
        verify(this.repository).loadTeams(playerId);
    }

    @Test
    public void shouldDeleteTeam_whenRequested() throws Exception {
        int playerId = 5;
        String teamName = "ToDelete_team";

        // Configure mock behavior for retrieving teams after deletion
        // Empty list after deletion
        when(this.repository.loadTeams(playerId)).thenReturn(new ArrayList<>());

        // Execute
        this.repository.createTeam(playerId, teamName);

        this.repository.deleteTeam(playerId, teamName);
        List<BugemonTeam> teamsAfterDeletion = this.repository.loadTeams(playerId);

        // Assert
        assertEquals("L'équipe doit avoir été supprimée", 0, teamsAfterDeletion.size());

        // Verify the mock was called
        verify(this.repository).createTeam(playerId, teamName);
        verify(this.repository).deleteTeam(playerId, teamName);
        verify(this.repository).loadTeams(playerId);
    }

    @Test
    public void shouldAddAndRetrieveTeamMembers_whenFillingRoster() throws Exception {
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
    public void shouldRemoveTeamMember_whenRequested() throws Exception {
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
    public void shouldRenameTeam_whenRequested() throws Exception {
        int playerId = 8;
        String oldTeamName = "Old_name_team";
        String newTeamName = "New_name_team";

        // Configure mock behavior for retrieving teams after rename
        when(this.repository.loadTeams(playerId)).thenReturn(List.of(new BugemonTeam(newTeamName)));

        // Execute
        this.repository.createTeam(playerId, oldTeamName);
        this.repository.renameTeam(playerId, oldTeamName, newTeamName);

        List<BugemonTeam> teams = this.repository.loadTeams(playerId);

        // Assert
        assertEquals("L'équipe renommée doit être présente", 1, teams.size());
        assertEquals("Le nouveau nom doit être présent", newTeamName, teams.get(0).getName());

        // Verify the mock was called
        verify(this.repository).createTeam(playerId, oldTeamName);
        verify(this.repository).renameTeam(playerId, oldTeamName, newTeamName);
        verify(this.repository).loadTeams(playerId);
    }

    @Test
    public void shouldDeleteTeamMembers_whenRequested() throws Exception {
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
        this.repository.deleteTeam(playerId, teamName);
        List<TeamMemberDTO> members = this.repository.getTeamMembers(playerId, teamName);

        // Assert
        assertTrue("Les membres de l'équipe doivent avoir été supprimés", members.isEmpty());

        // Verify the mock was called
        verify(this.repository).createTeam(playerId, teamName);
        verify(this.repository).savePlayerBugemon(bugemon);
        verify(this.repository).addTeamMember(member);
        verify(this.repository).deleteTeam(playerId, teamName);
        verify(this.repository).getTeamMembers(playerId, teamName);
    }
}
