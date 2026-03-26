package ulb.services;

import java.util.ArrayList;
import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repository.DatabaseRepository;
import ulb.repository.dto.TeamDTO;
import ulb.repository.dto.TeamMemberDTO;
import ulb.repository.dto.UserBugemonDTO;
import ulb.utils.Parser;

public class PlayerService {
    // Unique identifier for the user/player.
    private int userId;

    // Player's active team
    private BugemonTeam activeTeam;

    // List of all teams owned by the user
    private List<TeamDTO> userTeams;

    // Repository for database interactions
    private DatabaseRepository databaseRepository;

    // Cache for all default Bugemons to avoid multiple database calls
    private List<Bugemon> allDefaultBugemonsCache;

    // Player Inventory
    private Inventory inventory;
    private boolean databaseAvailable;

    /**
     * Constructor for PlayerService. Initializes the service by retrieving the user ID based on the
     * provided username, loading the user's teams, and setting up the database repository for
     * future interactions.
     * @param username the username of the player, used to retrieve or create a user ID in the
     *         database
     */
    public PlayerService(String username) {
        this.inventory = InventoryService.addStarterItem(new Inventory());

        try {
            this.databaseRepository = new DatabaseRepository();
            this.userId = this.databaseRepository.getUserIdByUsername(username).orElseGet(
                    () -> this.databaseRepository.createUser(username));
            this.userTeams = this.databaseRepository.getUserTeams(this.userId);
            this.databaseAvailable = true;
        } catch (IllegalStateException e) {
            // Local fallback: allow the game to start even if remote DB is unreachable.
            this.databaseRepository = null;
            this.userId = -1;
            this.userTeams = new ArrayList<>();
            this.databaseAvailable = false;

            Parser parser = new Parser();
            parser.parse();
            this.allDefaultBugemonsCache = parser.getBugemons();
        }
    }

    /**
     * Returns the currently active team of Bugemons.
     * @return the active BugemonTeam
     */
    public BugemonTeam getActiveTeam() {
        return this.activeTeam;
    }

    public Inventory getInventory() {
        return this.inventory;
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
            if (!this.databaseAvailable || this.databaseRepository == null) {
                Parser parser = new Parser();
                parser.parse();
                this.allDefaultBugemonsCache = parser.getBugemons();
                return this.allDefaultBugemonsCache;
            }
            this.allDefaultBugemonsCache = this.databaseRepository.getAllDefaultBugemons();
        }
        return this.allDefaultBugemonsCache;
    }

    public void saveTeam(String teamName, BugemonTeam team) {
        if (!this.databaseAvailable || this.databaseRepository == null) {
            this.setActiveTeam(team);
            return;
        }

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
     * the corresponding user Bugemons based on their IDs. If any Bugemon in the team cannot be
     * found, an exception is thrown.
     * @param teamName the name of the team to load and set as active
     */
    public void loadTeamAndSetActiveTeam(String teamName) {
        if (!this.databaseAvailable || this.databaseRepository == null) {
            throw new IllegalStateException(
                    "Database unavailable: loading a saved team is disabled in offline mode.");
        }

        List<TeamMemberDTO> teamMembers =
                this.databaseRepository.getTeamMembers(this.userId, teamName);
        List<UserBugemonDTO> userBugemons = this.databaseRepository.getUserBugemons(this.userId);

        this.activeTeam = new BugemonTeam(teamName);

        for (TeamMemberDTO member : teamMembers) {
            UserBugemonDTO userBugemon =
                    userBugemons.stream()
                            .filter(b -> b.bugemonId().equals(member.bugemonId()))
                            .findFirst()
                            .orElseThrow(()
                                                 -> new RuntimeException(
                                                         "User Bugemon with ID "
                                                         + member.bugemonId()
                                                         + (" not found. Cannot load team.")));
            try {
                this.activeTeam.add(this.buildUserBugemon(userBugemon));
            } catch (Exception e) {
                throw new IllegalStateException(
                        "Failed to add Bugemon to loaded team: " + e.getMessage(), e);
            }
        }
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

    /**
     * Saves the current state of the given Bugemon to the database. This method updates the
     * Bugemon's attributes such as HP, attack, defense, initiative, XP, and level in the database
     * to reflect any changes that occurred during gameplay. It first checks if the Bugemon is part
     * of the active team to ensure that only Bugemons currently in use are saved. If the Bugemon is
     * not in the active team, an exception is thrown to prevent saving invalid data. If the Bugemon
     * is valid, its current state is printed to the console for debugging purposes, and then the
     * database repository is called to update the Bugemon's information in the database using a
     * UserBugemonDTO object that encapsulates the necessary data for the update operation.
     * @param bugemon the Bugemon whose state is to be saved to the database. Must be part of the
     *         active team.
     */
    public void saveBugemonState(Bugemon bugemon) {
        if (!this.databaseAvailable || this.databaseRepository == null) {
            return;
        }

        if (!this.activeTeam.contains(bugemon)) {
            throw new IllegalArgumentException(
                    "Cannot save state of a Bugemon that is not in the active team.");
        }

        this.databaseRepository.updateUserBugemon(new UserBugemonDTO(
                userId, bugemon.getId(), bugemon.getDefense(), bugemon.getAttack(),
                bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));
    }

    /**
     * Restores the HP of all Bugemons in the active team to their maximum HP. This method is
     * typically called after a combat session to ensure that all Bugemons are fully healed before
     * the next encounter. It iterates through each Bugemon in the active team and calls their
     * restoreHp() method, which sets their current HP back to their maximum HP value. This allows
     * players to start the next combat with their Bugemons at full health, providing a fair and
     * consistent gameplay experience.
     */
    public void restoreHpActiveTeam() {
        if (this.activeTeam == null) {
            return;
        }
        this.activeTeam.forEach(Bugemon::restoreHp);
    }

    /**
     * Builds a Bugemon object based on the provided UserBugemonDTO. This method retrieves the
     * default Bugemon information from the cache using the bugemon ID from the UserBugemonDTO, and
     * then constructs a new Bugemon object using the attributes from both the default Bugemon and
     * the UserBugemonDTO. The resulting Bugemon object reflects the current state of the user's
     * Bugemon, including its current HP, attack, defense, initiative, XP, and level, while
     * retaining the base attributes such as name, type, and sprite from the default Bugemon.
     * @param userBugemon the UserBugemonDTO containing the data needed to build the Bugemon object,
     *         including its current state and attributes
     * @return the constructed Bugemon object
     */
    private Bugemon buildUserBugemon(UserBugemonDTO userBugemon) {
        Bugemon defaultBugemon =
                this.allDefaultBugemonsCache.stream()
                        .filter(b -> b.getId().equals(userBugemon.bugemonId()))
                        .findFirst()
                        .orElseThrow(()
                                             -> new RuntimeException("Default Bugemon with ID "
                                                                     + userBugemon.bugemonId()
                                                                     + " not found."));

        BugemonBuilder builder = new BugemonBuilder();
        builder.id(userBugemon.bugemonId());
        builder.name(defaultBugemon.getName());
        builder.type(defaultBugemon.getType());
        builder.sprite(defaultBugemon.getSpriteURL());
        builder.hp(userBugemon.currentMaxHp());
        builder.attack(userBugemon.currentAttackPower());
        builder.defense(userBugemon.currentDefense());
        builder.initiative(userBugemon.currentInitiative());
        builder.xp(userBugemon.currentXp());
        builder.level(userBugemon.currentLevel());
        builder.attackList(defaultBugemon.getAttackList());
        builder.isStarter(defaultBugemon.isStarter());

        return builder.build();
    }
}
