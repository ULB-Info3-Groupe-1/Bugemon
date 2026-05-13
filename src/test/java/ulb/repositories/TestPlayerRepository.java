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

import ulb.models.bugemon_team.Team;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.TeamMemberDTO;

@RunWith(MockitoJUnitRunner.class)
public class TestPlayerRepository {
    @Mock
    private BugemonRepository bugemonRepository;
    @Mock
    private TeamRepository teamRepository;

    @Test
    public void shouldSaveAndRetrievePlayerBugemons_whenValidDataProvided() {
        String playername = "test";
        String bugemonName = "1";

        PlayerBugemonDTO dto = new PlayerBugemonDTO(playername, bugemonName, 10, 20, 15, 100, 50, 5);
        List<PlayerBugemonDTO> expectedBugemons = new ArrayList<>();
        expectedBugemons.add(dto);

        when(this.bugemonRepository.getPlayerBugemons(playername)).thenReturn(expectedBugemons);

        this.bugemonRepository.savePlayerBugemon(dto);
        List<PlayerBugemonDTO> bugemons = this.bugemonRepository.getPlayerBugemons(playername);

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

        verify(this.bugemonRepository).savePlayerBugemon(dto);
        verify(this.bugemonRepository).getPlayerBugemons(playername);
    }

    @Test
    public void shouldUpdatePlayerBugemon_whenDataIsModified() {
        String playername = "test";
        String bugemonName = "2";

        PlayerBugemonDTO initial = new PlayerBugemonDTO(playername, bugemonName, 10, 10, 10, 50, 0, 1);
        PlayerBugemonDTO updated = new PlayerBugemonDTO(playername, bugemonName, 15, 25, 20, 80, 100, 3);

        List<PlayerBugemonDTO> updatedList = new ArrayList<>();
        updatedList.add(updated);

        when(this.bugemonRepository.getPlayerBugemons(playername)).thenReturn(updatedList);

        this.bugemonRepository.savePlayerBugemon(initial);
        this.bugemonRepository.updatePlayerBugemon(updated);
        List<PlayerBugemonDTO> bugemons = this.bugemonRepository.getPlayerBugemons(playername);

        assertEquals("La liste doit contenir un Bugémon", 1, bugemons.size());

        PlayerBugemonDTO retrieved = bugemons.get(0);
        assertEquals(15, retrieved.currentDefense());
        assertEquals(25, retrieved.currentAttackPower());
        assertEquals(20, retrieved.currentInitiative());
        assertEquals(80, retrieved.currentMaxHp());
        assertEquals(100, retrieved.currentXp());
        assertEquals(3, retrieved.currentLevel());

        verify(this.bugemonRepository).savePlayerBugemon(initial);
        verify(this.bugemonRepository).updatePlayerBugemon(updated);
        verify(this.bugemonRepository).getPlayerBugemons(playername);
    }

    @Test
    public void shouldCreateAndRetrieveTeams_whenAddingMultipleTeams() throws Exception {
        String playername = "name";

        Team team1 = new Team("team1");
        Team team2 = new Team("team2");
        List<Team> expectedTeams = new ArrayList<>();
        expectedTeams.add(team1);
        expectedTeams.add(team2);

        when(this.teamRepository.loadTeams(playername)).thenReturn(expectedTeams);

        this.teamRepository.createTeam(playername, "team1");
        this.teamRepository.createTeam(playername, "team2");

        List<Team> teams = this.teamRepository.loadTeams(playername);

        assertEquals(expectedTeams, teams);

        verify(this.teamRepository).createTeam(playername, "team1");
        verify(this.teamRepository).createTeam(playername, "team2");
        verify(this.teamRepository).loadTeams(playername);
    }

    @Test
    public void shouldDeleteTeam_whenRequested() throws Exception {
        String playername = "player";
        String teamName = "ToDelete_team";

        when(this.teamRepository.loadTeams(playername)).thenReturn(new ArrayList<>());

        this.teamRepository.createTeam(playername, teamName);
        this.teamRepository.deleteTeam(playername, teamName);
        List<Team> teamsAfterDeletion = this.teamRepository.loadTeams(playername);

        assertEquals("L'équipe doit avoir été supprimée", 0, teamsAfterDeletion.size());

        verify(this.teamRepository).createTeam(playername, teamName);
        verify(this.teamRepository).deleteTeam(playername, teamName);
        verify(this.teamRepository).loadTeams(playername);
    }

    @Test
    public void shouldAddAndRetrieveTeamMembers_whenFillingRoster() throws Exception {
        String playername = "test";
        String teamName = "Roster_team";
        String bugemonName = "1";

        TeamMemberDTO member = new TeamMemberDTO(playername, teamName, bugemonName, 1);
        List<TeamMemberDTO> expectedMembers = new ArrayList<>();
        expectedMembers.add(member);

        when(this.teamRepository.getTeamMembers(playername, teamName)).thenReturn(expectedMembers);

        this.teamRepository.createTeam(playername, teamName);

        PlayerBugemonDTO bugemon = new PlayerBugemonDTO(playername, bugemonName, 5, 5, 5, 50, 0, 1);
        this.bugemonRepository.savePlayerBugemon(bugemon);
        this.teamRepository.addTeamMember(member);
        List<TeamMemberDTO> members = this.teamRepository.getTeamMembers(playername, teamName);

        assertEquals("La liste des membres doit contenir 1 élément", 1, members.size());
        assertEquals(bugemonName, members.get(0).bugemonName());
        assertEquals(1, members.get(0).slotPosition());

        verify(this.teamRepository).createTeam(playername, teamName);
        verify(this.bugemonRepository).savePlayerBugemon(bugemon);
        verify(this.teamRepository).addTeamMember(member);
        verify(this.teamRepository).getTeamMembers(playername, teamName);
    }

    @Test
    public void shouldRemoveTeamMember_whenRequested() throws Exception {
        String playername = "name";
        String teamName = "EmptyMe_team";
        String bugemonName = "1";

        when(this.teamRepository.getTeamMembers(playername, teamName)).thenReturn(new ArrayList<>());

        this.teamRepository.createTeam(playername, teamName);
        PlayerBugemonDTO bugemon = new PlayerBugemonDTO(playername, bugemonName, 5, 5, 5, 50, 0, 1);
        this.bugemonRepository.savePlayerBugemon(bugemon);
        TeamMemberDTO member = new TeamMemberDTO(playername, teamName, bugemonName, 1);
        this.teamRepository.addTeamMember(member);
        this.teamRepository.removeTeamMember(playername, teamName, bugemonName);
        List<TeamMemberDTO> members = this.teamRepository.getTeamMembers(playername, teamName);

        assertTrue("La liste des membres devrait être vide après suppression", members.isEmpty());

        verify(this.teamRepository).createTeam(playername, teamName);
        verify(this.bugemonRepository).savePlayerBugemon(bugemon);
        verify(this.teamRepository).addTeamMember(member);
        verify(this.teamRepository).removeTeamMember(playername, teamName, bugemonName);
        verify(this.teamRepository).getTeamMembers(playername, teamName);
    }

    @Test
    public void shouldRenameTeam_whenRequested() throws Exception {
        String playername = "test";
        String oldTeamName = "Old_name_team";
        String newTeamName = "New_name_team";

        when(this.teamRepository.loadTeams(playername)).thenReturn(List.of(new Team(newTeamName)));

        this.teamRepository.createTeam(playername, oldTeamName);
        this.teamRepository.renameTeam(playername, oldTeamName, newTeamName);

        List<Team> teams = this.teamRepository.loadTeams(playername);

        assertEquals("L'équipe renommée doit être présente", 1, teams.size());
        assertEquals("Le nouveau nom doit être présent", newTeamName, teams.get(0).getName());

        verify(this.teamRepository).createTeam(playername, oldTeamName);
        verify(this.teamRepository).renameTeam(playername, oldTeamName, newTeamName);
        verify(this.teamRepository).loadTeams(playername);
    }

    @Test
    public void shouldDeleteTeamMembers_whenRequested() throws Exception {
        String playername = "name";
        String teamName = "ClearMembers_team";
        String bugemonName = "1";

        when(this.teamRepository.getTeamMembers(playername, teamName)).thenReturn(new ArrayList<>());

        this.teamRepository.createTeam(playername, teamName);

        PlayerBugemonDTO bugemon = new PlayerBugemonDTO(playername, bugemonName, 5, 5, 5, 50, 0, 1);
        this.bugemonRepository.savePlayerBugemon(bugemon);
        TeamMemberDTO member = new TeamMemberDTO(playername, teamName, bugemonName, 1);
        this.teamRepository.addTeamMember(member);
        this.teamRepository.deleteTeam(playername, teamName);
        List<TeamMemberDTO> members = this.teamRepository.getTeamMembers(playername, teamName);

        assertTrue("Les membres de l'équipe doivent avoir été supprimés", members.isEmpty());

        verify(this.teamRepository).createTeam(playername, teamName);
        verify(this.bugemonRepository).savePlayerBugemon(bugemon);
        verify(this.teamRepository).addTeamMember(member);
        verify(this.teamRepository).deleteTeam(playername, teamName);
        verify(this.teamRepository).getTeamMembers(playername, teamName);
    }
}
