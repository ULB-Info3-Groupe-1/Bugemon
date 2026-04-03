package ulb.services;

import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repository.DatabaseRepository;
import ulb.repository.dto.TeamDTO;
import ulb.repository.dto.TeamMemberDTO;
import ulb.repository.dto.UserBugemonDTO;
import ulb.services.exceptions.TeamNameAlreadyExistsException;
import ulb.services.exceptions.TeamNotFoundException;

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

    // Player Inventory
    private Inventory inventory;

    /** Retrieves or creates the user by username, then loads their teams and starter inventory. */
    public PlayerService(String username) {
        this.activeTeam = new BugemonTeam();
        this.databaseRepository = new DatabaseRepository();
        this.userId = this.databaseRepository.getUserIdByUsername(username)
                .orElseGet(() -> this.databaseRepository.createUser(username));
        this.userTeams = this.databaseRepository.getUserTeams(this.userId);

        // TODO: probably connect to db
        this.inventory = InventoryService.addStarterItem(new Inventory());
    }

    public BugemonTeam getActiveTeam() {
        return this.activeTeam;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void setActiveTeam(BugemonTeam team) {
        this.activeTeam = team;
    }

    public List<String> getTeamNames() {
        return this.userTeams.stream().map(TeamDTO::name).toList();
    }

    /**
     * @throws TeamNotFoundException
     *             if oldName does not exist
     * @throws TeamNameAlreadyExistsException
     *             if newName is already taken
     */
    public void renameTeam(String oldName, String newName)
            throws TeamNotFoundException, TeamNameAlreadyExistsException {
        if (!this.teamNameExists(oldName)) {
            throw new TeamNotFoundException("No team saved with the name " + oldName);
        }

        if (this.teamNameExists(newName)) {
            throw new TeamNameAlreadyExistsException("A team is already saved with the name " + newName);
        }

        this.databaseRepository.renameTeam(this.userId, oldName, newName);
        this.userTeams = this.databaseRepository.getUserTeams(this.userId);

        if (this.activeTeam.getName().equals(oldName)) {
            this.activeTeam.setName(newName);
        }
    }

    /**
     * @throws TeamNotFoundException
     *             if teamName does not exist
     */
    public void deleteTeam(String teamName) throws TeamNotFoundException {
        if (!this.teamNameExists(teamName)) {
            throw new TeamNotFoundException("No team saved with the name " + teamName);
        }

        this.databaseRepository.deleteTeam(this.userId, teamName);
        this.userTeams.removeIf(t -> t.name().equals(teamName));

        if (this.activeTeam.getName().equals(teamName)) {
            this.activeTeam = new BugemonTeam();
        }
    }

    /** Cached after the first call. */
    public List<Bugemon> getAllDefaultBugemons() {
        if (this.allDefaultBugemonsCache == null) {
            this.allDefaultBugemonsCache = this.databaseRepository.getAllDefaultBugemons();
        }
        return this.allDefaultBugemonsCache;
    }

    /**
     * Replaces all members of the stored team, registering any Bugemon not yet owned by this user.
     *
     * @throws TeamNameAlreadyExistsException
     *             if teamName already exists
     */
    public void updateTeamMembers(String teamName, BugemonTeam team) {
        this.databaseRepository.deleteTeamMembers(this.userId, teamName);
        List<UserBugemonDTO> userBugemonDTOs = this.databaseRepository.getUserBugemons(this.userId);
        for (Bugemon bugemon : team) {
            if (userBugemonDTOs.stream().noneMatch(dto -> dto.bugemonId() == bugemon.getId())) {
                this.databaseRepository.saveUserBugemon(
                        new UserBugemonDTO(this.userId, bugemon.getId(), bugemon.getDefense(), bugemon.getAttack(),
                                bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));
            }
            TeamMemberDTO memberDTO = new TeamMemberDTO(this.userId, teamName, bugemon.getId(),
                    team.getSlotPosition(bugemon));
            this.databaseRepository.addTeamMember(memberDTO);
        }
    }

    /**
     * @throws TeamNameAlreadyExistsException
     *             if a team with teamName already exists
     */
    public void saveTeam(String teamName, BugemonTeam team) throws TeamNameAlreadyExistsException {
        if (this.teamNameExists(teamName)) {
            throw new TeamNameAlreadyExistsException("A team is already saved with the name " + teamName);
        }

        this.databaseRepository.createTeam(this.userId, teamName);
        List<UserBugemonDTO> userBugemonDTOs = this.databaseRepository.getUserBugemons(this.userId);
        for (Bugemon bugemon : team) {
            if (userBugemonDTOs.stream().noneMatch(dto -> dto.bugemonId() == bugemon.getId())) {
                this.databaseRepository.saveUserBugemon(
                        new UserBugemonDTO(this.userId, bugemon.getId(), bugemon.getDefense(), bugemon.getAttack(),
                                bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));
            }
            TeamMemberDTO memberDTO = new TeamMemberDTO(this.userId, teamName, bugemon.getId(),
                    team.getSlotPosition(bugemon));
            this.databaseRepository.addTeamMember(memberDTO);
        }
        this.userTeams.add(new TeamDTO(this.userId, teamName));
    }

    /**
     * @throws TeamNotFoundException
     *             if teamName does not exist
     */
    public void loadTeamAndSetActiveTeam(String teamName) throws TeamNotFoundException {
        if (!this.teamNameExists(teamName)) {
            throw new TeamNotFoundException("No team saved with the name " + teamName);
        }

        List<TeamMemberDTO> teamMembers = this.databaseRepository.getTeamMembers(this.userId, teamName);
        List<UserBugemonDTO> userBugemons = this.databaseRepository.getUserBugemons(this.userId);

        this.activeTeam = new BugemonTeam();
        this.activeTeam.setName(teamName);

        for (TeamMemberDTO member : teamMembers) {
            UserBugemonDTO userBugemon = userBugemons.stream().filter(b -> b.bugemonId() == member.bugemonId())
                    .findFirst().orElseThrow(() -> new RuntimeException(
                            "User Bugemon with ID " + member.bugemonId() + (" not found. Cannot load team.")));

            this.activeTeam.add(this.buildUserBugemon(userBugemon));
        }
    }

    public boolean teamNameExists(String teamName) {
        return this.userTeams.stream().anyMatch(team -> team.name().equals(teamName));
    }

    /**
     * @throws IllegalArgumentException
     *             if bugemon is not in the active team
     */
    public void saveBugemonState(Bugemon bugemon) {
        if (!this.activeTeam.contains(bugemon)) {
            throw new IllegalArgumentException("Cannot save state of a Bugemon that is not in the active team.");
        }

        this.databaseRepository.updateUserBugemon(new UserBugemonDTO(this.userId, bugemon.getId(), bugemon.getDefense(),
                bugemon.getAttack(), bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));
    }

    public void restoreHpActiveTeam() {
        this.activeTeam.restoreHp();
    }

    private Bugemon buildUserBugemon(UserBugemonDTO userBugemon) {
        Bugemon defaultBugemon = this.allDefaultBugemonsCache.stream().filter(b -> b.getId() == userBugemon.bugemonId())
                .findFirst().orElseThrow(() -> new RuntimeException(
                        "Default Bugemon with ID " + userBugemon.bugemonId() + " not found."));

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

    public boolean isActiveTeamEmpty() {
        return this.activeTeam.isEmpty();
    }
}
