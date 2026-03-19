package ulb.repository;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import io.github.cdimascio.dotenv.Dotenv;
import ulb.repository.dto.TeamDTO;
import ulb.repository.dto.TeamMemberDTO;
import ulb.repository.dto.UserBugemonDTO;

public class TestRepository {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static final String TEST_DB_URL = dotenv.get("TEST_DB_URL");

    private DatabaseRepository repository;

    private String uniqueId;

    @Before
    public void setUp() {
        try {
            this.repository = new DatabaseRepository(TEST_DB_URL);

            if (!this.repository.isConnected()) {
                fail("La connexion à la base de données de test a échoué.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail("Le setup de la base de données a échoué : " + e.getMessage());
        }

        // Unique ID to avoid collisions (UNIQUE constraint violation)
        this.uniqueId = UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Local help to create a dummy Bugemon in the test database,
     * in order to respect the Foreign Key "bugemon_id" in "user_bugemons".
     */
    private void ensureDependenciesExist(String bugemonId) {
        String sqlAttack = "INSERT INTO attacks (id, name, type, power) VALUES ('test_atk', "
                           + "'Coup', 'AQUA', 10) ON CONFLICT DO NOTHING";
        String sqlBugemon =
                "INSERT INTO bugemons (id, name, type, attack_1_id, attack_2_id, attack_3_id) "
                + "VALUES (?, 'Template', 'AQUA', 'test_atk', 'test_atk', 'test_atk') ON "
                + "CONFLICT DO NOTHING";

        try (PreparedStatement ps = this.repository.getConnection().prepareStatement(sqlAttack)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Setup failed", e);
        }

        try (PreparedStatement ps = this.repository.getConnection().prepareStatement(sqlBugemon)) {
            ps.setString(1, bugemonId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Setup failed", e);
        }
    }

    @Test
    public void shouldCreateAndRetrieveUser_whenValidUsernameProvided() {
        String username = "User_" + this.uniqueId;
        int userId = this.repository.createUser(username);

        assertTrue("L'ID utilisateur doit être valide (supérieur à 0)", userId > 0);

        Optional<Integer> retrievedId = this.repository.getUserIdByUsername(username);
        assertTrue("L'utilisateur devrait être trouvé dans la BD", retrievedId.isPresent());
        assertEquals("L'ID récupéré doit correspondre à celui créé", userId,
                     retrievedId.get().intValue());
    }

    @Test
    public void shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<Integer> retrievedId = this.repository.getUserIdByUsername("Unknown_" + this.uniqueId);
        assertFalse("L'utilisateur ne devrait pas exister", retrievedId.isPresent());
    }

    @Test
    public void shouldSaveAndRetrieveUserBugemons_whenValidDataProvided() {
        String username = "Trainer_" + this.uniqueId;
        int userId = this.repository.createUser(username);
        String bugemonId = "bug_" + this.uniqueId;

        ensureDependenciesExist(bugemonId);

        UserBugemonDTO dto = new UserBugemonDTO(userId, bugemonId, 10, 20, 15, 100, 50, 5);
        this.repository.saveUserBugemon(dto);

        List<UserBugemonDTO> bugemons = this.repository.getUserBugemons(userId);
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
        String username = "Upgrader_" + this.uniqueId;
        int userId = this.repository.createUser(username);
        String bugemonId = "bugU_" + this.uniqueId;

        ensureDependenciesExist(bugemonId);

        // Initial save
        UserBugemonDTO initial = new UserBugemonDTO(userId, bugemonId, 10, 10, 10, 50, 0, 1);
        this.repository.saveUserBugemon(initial);

        // Modification
        UserBugemonDTO updated = new UserBugemonDTO(userId, bugemonId, 15, 25, 20, 80, 100, 3);
        this.repository.updateUserBugemon(updated);

        // Verification
        List<UserBugemonDTO> bugemons = this.repository.getUserBugemons(userId);
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
        String username = "TeamLeader_" + this.uniqueId;
        int userId = this.repository.createUser(username);
        String team1 = "Alpha_" + this.uniqueId;
        String team2 = "Beta_" + this.uniqueId;

        this.repository.createTeam(userId, team1);
        this.repository.createTeam(userId, team2);

        List<TeamDTO> teams = this.repository.getUserTeams(userId);
        assertEquals("Il devrait y avoir deux équipes", 2, teams.size());

        boolean foundTeam1 = teams.stream().anyMatch(t -> t.name().equals(team1));
        boolean foundTeam2 = teams.stream().anyMatch(t -> t.name().equals(team2));

        assertTrue("L'équipe Alpha doit être trouvée", foundTeam1);
        assertTrue("L'équipe Beta doit être trouvée", foundTeam2);
    }

    @Test
    public void shouldDeleteTeam_whenRequested() {
        String username = "DeleteTeam_" + this.uniqueId;
        int userId = this.repository.createUser(username);
        String teamName = "ToDelete_" + this.uniqueId;

        this.repository.createTeam(userId, teamName);
        assertEquals(1, this.repository.getUserTeams(userId).size());

        this.repository.deleteTeam(userId, teamName);
        List<TeamDTO> teamsAfterDeletion = this.repository.getUserTeams(userId);
        assertEquals("L'équipe doit avoir été supprimée", 0, teamsAfterDeletion.size());
    }

    @Test
    public void shouldAddAndRetrieveTeamMembers_whenFillingRoster() {
        String username = "Manager_" + this.uniqueId;
        int userId = this.repository.createUser(username);
        String teamName = "Roster_" + this.uniqueId;
        String bugemonId = "partner_" + this.uniqueId;

        ensureDependenciesExist(bugemonId);

        // First, we need to have the bugemon and the team (if constraints are implemented that way)
        this.repository.createTeam(userId, teamName);
        this.repository.saveUserBugemon(new UserBugemonDTO(userId, bugemonId, 5, 5, 5, 50, 0, 1));

        // Add to Roster (slot 1)
        TeamMemberDTO member = new TeamMemberDTO(userId, teamName, bugemonId, 1);
        this.repository.addTeamMember(member);

        List<TeamMemberDTO> members = this.repository.getTeamMembers(userId, teamName);
        assertEquals(1, members.size());
        assertEquals(bugemonId, members.get(0).bugemonId());
        assertEquals(1, members.get(0).slotPosition());
    }

    @Test
    public void shouldRemoveTeamMember_whenRequested() {
        String username = "Remover_" + this.uniqueId;
        int userId = this.repository.createUser(username);
        String teamName = "EmptyMe_" + this.uniqueId;
        String bugemonId = "leave_" + this.uniqueId;

        ensureDependenciesExist(bugemonId);
        this.repository.createTeam(userId, teamName);
        this.repository.saveUserBugemon(new UserBugemonDTO(userId, bugemonId, 5, 5, 5, 50, 0, 1));
        this.repository.addTeamMember(new TeamMemberDTO(userId, teamName, bugemonId, 1));

        this.repository.removeTeamMember(userId, teamName, bugemonId);

        List<TeamMemberDTO> members = this.repository.getTeamMembers(userId, teamName);
        assertTrue("La liste des membres devrait être vide après suppression", members.isEmpty());
    }
}
