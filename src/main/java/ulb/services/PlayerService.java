package ulb.services;

import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repository.DatabaseRepository;
import ulb.repository.dto.StaticBugemonDataDTO;
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

    // Player Inventory
    private Inventory inventory;

    private final DatabaseRepository dbRepository;

    /** Retrieves or creates the user by username, then loads their teams and starter inventory. */
    public PlayerService(String username) {
        this.dbRepository = DatabaseRepository.getInstance();
        this.activeTeam = new BugemonTeam();
        this.userId = this.dbRepository.getUserIdByUsername(username)
                .orElseGet(() -> this.dbRepository.createUser(username));
        this.userTeams = this.dbRepository.getUserTeams(this.userId);

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

        this.dbRepository.renameTeam(this.userId, oldName, newName);
        this.userTeams = this.dbRepository.getUserTeams(this.userId);

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

        this.dbRepository.deleteTeam(this.userId, teamName);
        this.userTeams.removeIf(t -> t.name().equals(teamName));

        if (this.activeTeam.getName().equals(teamName)) {
            this.activeTeam = new BugemonTeam();
        }
    }

    /**
     * Replaces all members of the stored team, registering any Bugemon not yet owned by this user.
     *
     * @throws TeamNameAlreadyExistsException
     *             if teamName already exists
     */
    public void updateTeamMembers(String teamName, BugemonTeam team) {
        this.dbRepository.deleteTeamMembers(this.userId, teamName);
        List<UserBugemonDTO> userBugemonDTOs = this.dbRepository.getUserBugemons(this.userId);
        for (Bugemon bugemon : team) {
            if (userBugemonDTOs.stream().noneMatch(dto -> dto.bugemonName().equals(bugemon.getName()))) {
                this.dbRepository.saveUserBugemon(
                        new UserBugemonDTO(this.userId, bugemon.getName(), bugemon.getDefense(), bugemon.getAttack(),
                                bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));
            }
            TeamMemberDTO memberDTO = new TeamMemberDTO(this.userId, teamName, bugemon.getName(),
                    team.getSlotPosition(bugemon));
            this.dbRepository.addTeamMember(memberDTO);
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

        this.dbRepository.createTeam(this.userId, teamName);
        List<UserBugemonDTO> userBugemonDTOs = this.dbRepository.getUserBugemons(this.userId);
        for (Bugemon bugemon : team) {
            if (userBugemonDTOs.stream().noneMatch(dto -> dto.bugemonName().equals(bugemon.getName()))) {
                this.dbRepository.saveUserBugemon(
                        new UserBugemonDTO(this.userId, bugemon.getName(), bugemon.getDefense(), bugemon.getAttack(),
                                bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));
            }
            TeamMemberDTO memberDTO = new TeamMemberDTO(this.userId, teamName, bugemon.getName(),
                    team.getSlotPosition(bugemon));
            this.dbRepository.addTeamMember(memberDTO);
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

        List<TeamMemberDTO> teamMembers = this.dbRepository.getTeamMembers(this.userId, teamName);
        List<UserBugemonDTO> userBugemons = this.dbRepository.getUserBugemons(this.userId);

        this.activeTeam = new BugemonTeam();
        this.activeTeam.setName(teamName);

        for (TeamMemberDTO member : teamMembers) {
            UserBugemonDTO userBugemon = userBugemons.stream().filter(b -> b.bugemonName().equals(member.bugemonName()))
                    .findFirst().orElseThrow(() -> new RuntimeException(
                            "User Bugemon with name " + member.bugemonName() + (" not found. Cannot load team.")));

            this.activeTeam.add(this.buildUserBugemon(userBugemon));
        }
    }

    private boolean teamNameExists(String teamName) {
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

        this.dbRepository.updateUserBugemon(new UserBugemonDTO(this.userId, bugemon.getName(), bugemon.getDefense(),
                bugemon.getAttack(), bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));
    }

    /** Persists the current state (XP, level, stats) of every Bugemon in the active team. */
    public void saveActiveTeamState() {
        for (Bugemon bugemon : this.activeTeam) {
            this.databaseRepository.updateUserBugemon(
                    new UserBugemonDTO(this.userId, bugemon.getId(), bugemon.getDefense(), bugemon.getAttack(),
                            bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));
        }
    }

    public void restoreHpActiveTeam() {
        this.activeTeam.restoreHp();
    }

    private Bugemon buildUserBugemon(UserBugemonDTO userBugemon) {
        StaticBugemonDataDTO defaultBugemon = this.dbRepository.getBugemonByName(userBugemon.bugemonName());

        BugemonBuilder builder = new BugemonBuilder();
        builder.name(defaultBugemon.name());
        builder.type(BugemonType.valueOf(defaultBugemon.type()));
        builder.sprite(defaultBugemon.spriteUrl());
        builder.hp(userBugemon.currentMaxHp());
        builder.attack(userBugemon.currentAttackPower());
        builder.defense(userBugemon.currentDefense());
        builder.initiative(userBugemon.currentInitiative());
        builder.xp(userBugemon.currentXp());
        builder.level(userBugemon.currentLevel());
        builder.attackList(defaultBugemon.attackList());
        builder.isStarter(defaultBugemon.isStarter());

        return builder.build();
    }

    public boolean isActiveTeamEmpty() {
        return this.activeTeam.isEmpty();
    }
}
