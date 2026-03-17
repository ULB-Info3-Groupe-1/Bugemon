package ulb.repository;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import ulb.repository.dto.TeamDTO;
import ulb.repository.dto.TeamMemberDTO;
import ulb.repository.dto.UserBugemonDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TestRepository {

    private DatabaseRepository repository;
    private String uniqueId;

    @Before
    public void setUp() {
        // Test if the database is accessible if not ignore the tests
        try {
            Connection conn = DatabaseManager.getInstance().getConnection();
            if (conn == null || conn.isClosed()) {
                Assume.assumeTrue("Database is not connected, tests skipped", false);
            }
        } catch (Exception e) {
            Assume.assumeNoException("Database is not connected or you have an issue with your configuration, tests skipped", e);
        }
        
        repository = new DatabaseRepository();
        // ID unique pour éviter les collisions (violation de contraintes UNIQUE)
        uniqueId = UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Aide locale pour créer un Bugemon factice dans la DB de test, 
     * afin de respecter la Foreign Key "bugemon_id" dans "user_bugemons".
     */
    private void ensureDependenciesExist(String bugemonId) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Création d'une attaque factice
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO attacks (id, name, type, power) VALUES (?, 'Tackle', 'NORMAL', 10) ON CONFLICT DO NOTHING"
            )) {
                ps.setString(1, "atk_" + bugemonId);
                ps.executeUpdate();
            }
            // Création d'un bugemon factice lié
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO bugemons (id, name, base_max_hp, attack_1_id) VALUES (?, 'Testmon', 100, ?) ON CONFLICT DO NOTHING"
            )) {
                ps.setString(1, bugemonId);
                ps.setString(2, "atk_" + bugemonId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Avertissement: Impossible d'injecter la dépendance factice : " + e.getMessage());
        }
    }

    @Test
    public void shouldCreateAndRetrieveUser_whenValidUsernameProvided() {
        String username = "User_" + uniqueId;
        int userId = repository.createUser(username);

        assertTrue("L'ID utilisateur doit être valide (supérieur à 0)", userId > 0);

        Optional<Integer> retrievedId = repository.getUserIdByUsername(username);
        assertTrue("L'utilisateur devrait être trouvé dans la BD", retrievedId.isPresent());
        assertEquals("L'ID récupéré doit correspondre à celui créé", userId, retrievedId.get().intValue());
    }

    @Test
    public void shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<Integer> retrievedId = repository.getUserIdByUsername("Unknown_" + uniqueId);
        assertFalse("L'utilisateur ne devrait pas exister", retrievedId.isPresent());
    }

    @Test
    public void shouldSaveAndRetrieveUserBugemons_whenValidDataProvided() {
        String username = "Trainer_" + uniqueId;
        int userId = repository.createUser(username);
        String bugemonId = "bug_" + uniqueId;
        
        ensureDependenciesExist(bugemonId);

        UserBugemonDTO dto = new UserBugemonDTO(
            userId, bugemonId, 10, 20, 15, 100, 50, 5
        );
        repository.saveUserBugemon(dto);

        List<UserBugemonDTO> bugemons = repository.getUserBugemons(userId);
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
    }

    @Test
    public void shouldUpdateUserBugemon_whenDataIsModified() {
        String username = "Upgrader_" + uniqueId;
        int userId = repository.createUser(username);
        String bugemonId = "bugU_" + uniqueId;

        ensureDependenciesExist(bugemonId);

        // Sauvegarde initiale
        UserBugemonDTO initial = new UserBugemonDTO(
            userId, bugemonId, 10, 10, 10, 50, 0, 1
        );
        repository.saveUserBugemon(initial);

        // Modification
        UserBugemonDTO updated = new UserBugemonDTO(
            userId, bugemonId, 15, 25, 20, 80, 100, 3
        );
        repository.updateUserBugemon(updated);

        // Vérification
        List<UserBugemonDTO> bugemons = repository.getUserBugemons(userId);
        assertEquals(1, bugemons.size());

        UserBugemonDTO retrieved = bugemons.get(0);
        assertEquals(15, retrieved.currentDefense());
        assertEquals(25, retrieved.currentAttackPower());
        assertEquals(20, retrieved.currentInitiative());
        assertEquals(80, retrieved.currentMaxHp());
        assertEquals(100, retrieved.currentXp());
        assertEquals(3, retrieved.currentLevel());
    }

    @Test
    public void shouldCreateAndRetrieveTeams_whenAddingMultipleTeams() {
        String username = "TeamLeader_" + uniqueId;
        int userId = repository.createUser(username);
        String team1 = "Alpha_" + uniqueId;
        String team2 = "Beta_" + uniqueId;

        repository.createTeam(userId, team1);
        repository.createTeam(userId, team2);

        List<TeamDTO> teams = repository.getUserTeams(userId);
        assertEquals("Il devrait y avoir deux équipes", 2, teams.size());
        
        boolean foundTeam1 = teams.stream().anyMatch(t -> t.name().equals(team1));
        boolean foundTeam2 = teams.stream().anyMatch(t -> t.name().equals(team2));
        
        assertTrue("L'équipe Alpha doit être trouvée", foundTeam1);
        assertTrue("L'équipe Beta doit être trouvée", foundTeam2);
    }

    @Test
    public void shouldDeleteTeam_whenRequested() {
        String username = "DeleteTeam_" + uniqueId;
        int userId = repository.createUser(username);
        String teamName = "ToDelete_" + uniqueId;

        repository.createTeam(userId, teamName);
        assertEquals(1, repository.getUserTeams(userId).size());

        repository.deleteTeam(userId, teamName);
        List<TeamDTO> teamsAfterDeletion = repository.getUserTeams(userId);
        assertEquals("L'équipe doit avoir été supprimée", 0, teamsAfterDeletion.size());
    }

    @Test
    public void shouldAddAndRetrieveTeamMembers_whenFillingRoster() {
        String username = "Manager_" + uniqueId;
        int userId = repository.createUser(username);
        String teamName = "Roster_" + uniqueId;
        String bugemonId = "partner_" + uniqueId;

        ensureDependenciesExist(bugemonId);

        // Il faut d'abord posséder le bugémon et avoir l'équipe (si contraintes implémentées ainsi)
        repository.createTeam(userId, teamName);
        repository.saveUserBugemon(new UserBugemonDTO(userId, bugemonId, 5, 5, 5, 50, 0, 1));

        // Ajout au Roster (slot 1)
        TeamMemberDTO member = new TeamMemberDTO(userId, teamName, bugemonId, 1);
        repository.addTeamMember(member);

        List<TeamMemberDTO> members = repository.getTeamMembers(userId, teamName);
        assertEquals(1, members.size());
        assertEquals(bugemonId, members.get(0).bugemonId());
        assertEquals(1, members.get(0).slotPosition());
    }

    @Test
    public void shouldRemoveTeamMember_whenRequested() {
        String username = "Remover_" + uniqueId;
        int userId = repository.createUser(username);
        String teamName = "EmptyMe_" + uniqueId;
        String bugemonId = "leave_" + uniqueId;

        ensureDependenciesExist(bugemonId);
        repository.createTeam(userId, teamName);
        repository.saveUserBugemon(new UserBugemonDTO(userId, bugemonId, 5, 5, 5, 50, 0, 1));
        repository.addTeamMember(new TeamMemberDTO(userId, teamName, bugemonId, 1));

        repository.removeTeamMember(userId, teamName, bugemonId);
        
        List<TeamMemberDTO> members = repository.getTeamMembers(userId, teamName);
        assertTrue("La liste des membres devrait être vide après suppression", members.isEmpty());
    }
}



