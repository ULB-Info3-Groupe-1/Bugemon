package ulb.services;

import java.util.Iterator;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repository.DatabaseConnection;
import ulb.repository.DatabaseRepository;
import ulb.repository.dto.TeamMemberDTO;
import ulb.repository.dto.UserBugemonDTO;

public class PlayerService {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static final String PRODUCTION_URL = dotenv.get("PRODUCTION_DB_URL");

    // Player's active team
    private BugemonTeam activeTeam;
    // List of all user-created teams
    private List<BugemonTeam> userTeams;

    private Inventory inventory;

    // Repository for database interactions
    private DatabaseRepository databaseRepository;

    // Cache for all default Bugemons to avoid multiple database calls
    private List<Bugemon> allDefaultBugemonsCache;

    private String username = "admin";

    /**
     * Contructor for PlayerService.
     */
    public PlayerService() {
        this.databaseRepository = new DatabaseRepository(new DatabaseConnection(PRODUCTION_URL));
    }

    /**
     * Returns the currently active team of Bugemons.
     * @return the active BugemonTeam
     */
    public BugemonTeam getActiveTeam() {
        return activeTeam;
    }

    /**
     * Sets the active team to the given BugemonTeam.
     */
    public void setActiveTeam(BugemonTeam team) {
        this.activeTeam = team;
    }

    /**
     * Clears the active team by removing all Bugemons from it. This method is useful for resetting
     * the player's team between sessions or when starting a new game.
     */
    public void clearActiveTeam() {
        if (this.activeTeam != null) {
            this.activeTeam.clear();
        }
    }

    /**
     * Returns a list of all default Bugemons available in the game. This method caches the result
     * after the first call to minimize database access.
     * @return a list of all default Bugemons
     */
    public List<Bugemon> getAllDefaultBugemons() {
        if (allDefaultBugemonsCache == null) {
            allDefaultBugemonsCache = databaseRepository.getAllDefaultBugemons();
        }
        return allDefaultBugemonsCache;
    }

    public void saveTeam(String teamName, BugemonTeam team) {
        int userId = this.databaseRepository.getUserIdByUsername(this.username).orElseGet(() -> {
            this.databaseRepository.createUser(this.username);
            return this.databaseRepository.getUserIdByUsername(this.username).orElse(-1);
        });
        if (userId == -1) {
            throw new RuntimeException("Failed to create or retrieve user ID for username: "
                                       + this.username + ". Cannot save team.");
        }
        this.databaseRepository.createTeam(userId, teamName);
        List<UserBugemonDTO> userBugemonDTOs = this.databaseRepository.getUserBugemons(userId);
        for (Bugemon bugemon : team) {
            if (userBugemonDTOs.stream().noneMatch(
                        dto -> dto.bugemonId().equals(bugemon.getId()))) {
                this.databaseRepository.saveUserBugemon(new UserBugemonDTO(
                        userId, bugemon.getId(), bugemon.getDefense(), bugemon.getAttack(),
                        bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(),
                        bugemon.getLevel()));
            }
            TeamMemberDTO memberDTO = new TeamMemberDTO(userId, teamName, bugemon.getId(),
                                                        team.getSlotPosition(bugemon));
            this.databaseRepository.addTeamMember(memberDTO);
        }
    }

    public List<Bugemon> loadTeam(String teamName) {
        int userId = this.databaseRepository.getUserIdByUsername(this.username).orElse(-1);
        if (userId == -1) {
            throw new RuntimeException("User not found for username: " + this.username
                                       + ". Cannot load team.");
        }
        List<TeamMemberDTO> teamMembers = this.databaseRepository.getTeamMembers(userId, teamName);
        BugemonTeam loadedTeam = new BugemonTeam();
        for (TeamMemberDTO member : teamMembers) {
            Bugemon bugemon =
                    this.getAllDefaultBugemons()
                            .stream()
                            .filter(b -> b.getId().equals(member.bugemonId()))
                            .findFirst()
                            .orElseThrow(()
                                                 -> new RuntimeException(
                                                         "Bugemon with ID " + member.bugemonId()
                                                         + (" not found in default Bugemons cache. "
                                                            + "Cannot load team.")));
            try {
                loadedTeam.add(bugemon);
            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to add Bugemon to loaded team: " + e.getMessage(), e);
            }
        }
        return loadedTeam.getAll();
    }
}
