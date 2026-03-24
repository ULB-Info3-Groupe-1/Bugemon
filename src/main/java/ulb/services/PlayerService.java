package ulb.services;

import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repository.DatabaseRepository;
import ulb.repository.dto.TeamMemberDTO;
import ulb.repository.dto.UserBugemonDTO;

public class PlayerService {


    // Player's active team
    private static BugemonTeam activeTeam;

    // List of all user-created teams
    private static List<BugemonTeam> userTeams;

    private static Inventory inventory;

    // Repository for database interactions
    private static final DatabaseRepository databaseRepository = new DatabaseRepository();

    // Cache for all default Bugemons to avoid multiple database calls
    private static List<Bugemon> allDefaultBugemonsCache;

    // TODO: remove
    private static final String username = "admin";

    private PlayerService() {
       // Private constructor to prevent instantiation
    }

    /**
     * Returns the currently active team of Bugemons.
     * @return the active BugemonTeam
     */
    public static BugemonTeam getActiveTeam() {
        return activeTeam;
    }

    /**
     * Sets the active team to the given BugemonTeam.
     */
    public static void setActiveTeam(BugemonTeam team) {
        activeTeam = team;
    }

    /**
     * Clears the active team by removing all Bugemons from it. This method is useful for resetting
     * the player's team between sessions or when starting a new game.
     */
    public static void clearActiveTeam() {
        if (activeTeam != null) {
            activeTeam.clear();
        }
    }

    /**
     * Returns a list of all default Bugemons available in the game. This method caches the result
     * after the first call to minimize database access.
     * @return a list of all default Bugemons
     */
    public static List<Bugemon> getAllDefaultBugemons() {
        if (allDefaultBugemonsCache == null) {
            allDefaultBugemonsCache = databaseRepository.getAllDefaultBugemons();
        }
        return allDefaultBugemonsCache;
    }

    public static void saveTeam(String teamName, BugemonTeam team) {
        int userId = databaseRepository.getUserIdByUsername(username).orElseGet(() -> {
            databaseRepository.createUser(username);
            return databaseRepository.getUserIdByUsername(username).orElse(-1);
        });
        if (userId == -1) {
            throw new IllegalStateException("Failed to create or retrieve user ID for username: "
                                       + username + ". Cannot save team.");
        }
        databaseRepository.createTeam(userId, teamName);
        List<UserBugemonDTO> userBugemonDTOs = databaseRepository.getUserBugemons(userId);
        for (Bugemon bugemon : team) {
            if (userBugemonDTOs.stream().noneMatch(
                        dto -> dto.bugemonId().equals(bugemon.getId()))) {
                databaseRepository.saveUserBugemon(new UserBugemonDTO(
                        userId, bugemon.getId(), bugemon.getDefense(), bugemon.getAttack(),
                        bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(),
                        bugemon.getLevel()));
            }
            TeamMemberDTO memberDTO = new TeamMemberDTO(userId, teamName, bugemon.getId(),
                                                        team.getSlotPosition(bugemon));
            databaseRepository.addTeamMember(memberDTO);
        }
    }

    public static List<Bugemon> loadTeam(String teamName) {
        int userId = databaseRepository.getUserIdByUsername(username).orElse(-1);
        if (userId == -1) {
            throw new IllegalStateException("User not found for username: " + username
                                       + ". Cannot load team.");
        }
        List<TeamMemberDTO> teamMembers = databaseRepository.getTeamMembers(userId, teamName);
        BugemonTeam loadedTeam = new BugemonTeam();
        for (TeamMemberDTO member : teamMembers) {
            Bugemon bugemon =
                    getAllDefaultBugemons()
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
                throw new IllegalStateException(
                        "Failed to add Bugemon to loaded team: " + e.getMessage(), e);
            }
        }
        return loadedTeam.getAll();
    }
}
