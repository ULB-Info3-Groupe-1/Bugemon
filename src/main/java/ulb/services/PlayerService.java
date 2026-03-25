package ulb.services;

import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repository.DatabaseRepository;
import ulb.repository.dto.TeamDTO;
import ulb.repository.dto.TeamMemberDTO;
import ulb.repository.dto.UserBugemonDTO;

public class PlayerService {
    // Unique identifier for the user/player.
    private final int userId;

    // Player's active team
    private BugemonTeam activeTeam;

    // List of all teams owned by the user
    private List<TeamDTO> userTeams;

    // Repository for database interactions
    private final DatabaseRepository databaseRepository;

    // Cache for all default Bugemons to avoid multiple database calls
    private List<Bugemon> allDefaultBugemonsCache;

    /**
     * Constructor for PlayerService. Initializes the service by retrieving the user ID based on the
     * provided username, loading the user's teams, and setting up the database repository for
     * future interactions.
     * @param username the username of the player, used to retrieve or create a user ID in the
     *         database
     */
    public PlayerService(String username) {
        this.databaseRepository = new DatabaseRepository();
        this.userId = this.databaseRepository.getUserIdByUsername(username).orElseGet(
                () -> this.databaseRepository.createUser(username));
        this.userTeams = this.databaseRepository.getUserTeams(this.userId);
    }

    /**
     * Returns the currently active team of Bugemons.
     * @return the active BugemonTeam
     */
    public BugemonTeam getActiveTeam() {
        return this.activeTeam;
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
        if (this.allDefaultBugemonsCache == null) {
            this.allDefaultBugemonsCache = this.databaseRepository.getAllDefaultBugemons();
        }
        return this.allDefaultBugemonsCache;
    }

    public void saveTeam(String teamName, BugemonTeam team) {
        this.databaseRepository.createTeam(this.userId, teamName);
        List<UserBugemonDTO> userBugemonDTOs = this.databaseRepository.getUserBugemons(this.userId);
        for (Bugemon bugemon : team) {
            if (userBugemonDTOs.stream().noneMatch(
                        dto -> dto.bugemonId().equals(bugemon.getId()))) {
                this.databaseRepository.saveUserBugemon(new UserBugemonDTO(
                        userId, bugemon.getId(), bugemon.getDefense(), bugemon.getAttack(),
                        bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(),
                        bugemon.getLevel()));
            }
            TeamMemberDTO memberDTO = new TeamMemberDTO(this.userId, teamName, bugemon.getId(),
                                                        team.getSlotPosition(bugemon));
            this.databaseRepository.addTeamMember(memberDTO);
        }
        this.userTeams.add(new TeamDTO(this.userId, teamName));
    }

    /**
     * Loads the team with the given name from the database and sets it as the active team. This
     * method assumes that the team with the given name exists and belongs to the user. It retrieves
     * the team members from the database, constructs a BugemonTeam object, and populates it with
     * the corresponding Bugemons based on their IDs. If any Bugemon in the team cannot be found in
     * the default Bugemons cache, an exception is thrown.
     * @param teamName the name of the team to load and set as active
     */
    public void loadTeamAndSetActiveTeam(String teamName) {
        List<TeamMemberDTO> teamMembers =
                this.databaseRepository.getTeamMembers(this.userId, teamName);
        BugemonTeam loadedTeam = new BugemonTeam(teamName);
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
        this.activeTeam = loadedTeam;
    }

    /**
     * Checks if the user already has a team with the given name. This is used to prevent duplicate
     * team names when saving a new team.
     * @param teamName the name of the team to check for existence
     * @return true if a team with the given name already exists for the user, false otherwise
     */
    public boolean teamNameExists(String teamName) {
        return this.userTeams.stream().anyMatch(team -> team.name().equals(teamName));
    }
}
