package ulb.services;

import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repository.DatabaseRepository;
import ulb.repository.dto.PlayerBugemonDTO;
import ulb.repository.dto.StaticBugemonDataDTO;
import ulb.repository.dto.TeamDTO;
import ulb.repository.dto.TeamMemberDTO;
import ulb.services.exceptions.TeamEmptyException;
import ulb.services.exceptions.TeamNameAlreadyExistsException;
import ulb.services.exceptions.TeamNotFoundException;

public class PlayerService {
    // Unique identifier for the player.
    private final int playerId;

    // Player's active team
    private BugemonTeam activeTeam;

    // List of all teams owned by the player
    private List<TeamDTO> playerTeams;

    // Player Inventory
    private Inventory inventory;

    private final DatabaseRepository dbRepository;

    /** Retrieves or creates the player by playername, then loads their teams and starter inventory. */
    public PlayerService(String playername) {
        this.dbRepository = DatabaseRepository.getInstance();
        this.activeTeam = new BugemonTeam();
        this.playerId = this.dbRepository.getPlayerIdByPlayername(playername)
                .orElseGet(() -> this.dbRepository.createPlayer(playername));
        this.playerTeams = this.dbRepository.getPlayerTeams(this.playerId);

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
        return this.playerTeams.stream().map(TeamDTO::name).toList();
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

        this.dbRepository.renameTeam(this.playerId, oldName, newName);
        this.playerTeams = this.dbRepository.getPlayerTeams(this.playerId);

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

        this.dbRepository.deleteTeam(this.playerId, teamName);
        this.playerTeams.removeIf(t -> t.name().equals(teamName));

        if (this.activeTeam.getName().equals(teamName)) {
            this.activeTeam = new BugemonTeam();
        }
    }

    /**
     * Replaces all members of the stored team, registering any Bugemon not yet owned by this player.
     *
     * @throws TeamNameAlreadyExistsException
     *             if teamName already exists
     */
    public void updateTeamMembers(String teamName, BugemonTeam team) {
        this.dbRepository.deleteTeamMembers(this.playerId, teamName);
        List<PlayerBugemonDTO> playerBugemonDTOs = this.dbRepository.getPlayerBugemons(this.playerId);
        for (Bugemon bugemon : team) {
            if (playerBugemonDTOs.stream().noneMatch(dto -> dto.bugemonName().equals(bugemon.getName()))) {
                this.dbRepository.savePlayerBugemon(new PlayerBugemonDTO(this.playerId, bugemon.getName(),
                        bugemon.getDefense(), bugemon.getAttack(), bugemon.getInitiative(), bugemon.getMaxHp(),
                        bugemon.getXp(), bugemon.getLevel()));
            }
            TeamMemberDTO memberDTO = new TeamMemberDTO(this.playerId, teamName, bugemon.getName(),
                    team.getSlotPosition(bugemon));
            this.dbRepository.addTeamMember(memberDTO);
        }
    }

    /**
     * @throws TeamNameAlreadyExistsException
     *             if a team with teamName already exists
     */
    public void saveTeam(String teamName, BugemonTeam team) throws TeamNameAlreadyExistsException, TeamEmptyException {
        if (this.teamNameExists(teamName)) {
            throw new TeamNameAlreadyExistsException("A team is already saved with the name " + teamName);
        }

        if (team.isEmpty()) {
            throw new TeamEmptyException("Team is empty!");
        }

        this.dbRepository.createTeam(this.playerId, teamName);
        List<PlayerBugemonDTO> playerBugemonDTOs = this.dbRepository.getPlayerBugemons(this.playerId);
        for (Bugemon bugemon : team) {
            if (playerBugemonDTOs.stream().noneMatch(dto -> dto.bugemonName().equals(bugemon.getName()))) {
                this.dbRepository.savePlayerBugemon(new PlayerBugemonDTO(this.playerId, bugemon.getName(),
                        bugemon.getDefense(), bugemon.getAttack(), bugemon.getInitiative(), bugemon.getMaxHp(),
                        bugemon.getXp(), bugemon.getLevel()));
            }
            TeamMemberDTO memberDTO = new TeamMemberDTO(this.playerId, teamName, bugemon.getName(),
                    team.getSlotPosition(bugemon));
            this.dbRepository.addTeamMember(memberDTO);
        }
        this.playerTeams.add(new TeamDTO(this.playerId, teamName));
    }

    /**
     * @throws TeamNotFoundException
     *             if teamName does not exist
     */
    public void loadTeamAndSetActiveTeam(String teamName) throws TeamNotFoundException {
        if (!this.teamNameExists(teamName)) {
            throw new TeamNotFoundException("No team saved with the name " + teamName);
        }

        List<TeamMemberDTO> teamMembers = this.dbRepository.getTeamMembers(this.playerId, teamName);
        List<PlayerBugemonDTO> playerBugemons = this.dbRepository.getPlayerBugemons(this.playerId);

        this.activeTeam = new BugemonTeam();
        this.activeTeam.setName(teamName);

        for (TeamMemberDTO member : teamMembers) {
            PlayerBugemonDTO playerBugemon = playerBugemons.stream()
                    .filter(b -> b.bugemonName().equals(member.bugemonName())).findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Player Bugemon with name " + member.bugemonName() + (" not found. Cannot load team.")));

            this.activeTeam.add(this.buildPlayerBugemon(playerBugemon));
        }
    }

    private boolean teamNameExists(String teamName) {
        return this.playerTeams.stream().anyMatch(team -> team.name().equals(teamName));
    }

    /**
     * @throws IllegalArgumentException
     *             if bugemon is not in the active team
     */
    public void saveBugemonState(Bugemon bugemon) {
        if (!this.activeTeam.contains(bugemon)) {
            throw new IllegalArgumentException("Cannot save state of a Bugemon that is not in the active team.");
        }

        this.dbRepository.updatePlayerBugemon(
                new PlayerBugemonDTO(this.playerId, bugemon.getName(), bugemon.getDefense(), bugemon.getAttack(),
                        bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));
    }

    /** Persists the current state (XP, level, stats) of every Bugemon in the active team. */
    public void saveActiveTeamState() {
        for (Bugemon bugemon : this.activeTeam) {
            this.dbRepository.updatePlayerBugemon(
                    new PlayerBugemonDTO(this.playerId, bugemon.getName(), bugemon.getDefense(), bugemon.getAttack(),
                            bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));
        }
    }

    public void restoreHpActiveTeam() {
        this.activeTeam.restoreHp();
    }

    private Bugemon buildPlayerBugemon(PlayerBugemonDTO playerBugemon) {
        StaticBugemonDataDTO defaultBugemon = this.dbRepository.getBugemonByName(playerBugemon.bugemonName());

        BugemonBuilder builder = new BugemonBuilder();
        builder.name(defaultBugemon.name());
        builder.type(BugemonType.valueOf(defaultBugemon.type()));
        builder.sprite(defaultBugemon.spriteUrl());
        builder.hp(playerBugemon.currentMaxHp());
        builder.attack(playerBugemon.currentAttackPower());
        builder.defense(playerBugemon.currentDefense());
        builder.initiative(playerBugemon.currentInitiative());
        builder.xp(playerBugemon.currentXp());
        builder.level(playerBugemon.currentLevel());
        builder.attackList(defaultBugemon.attackList());
        builder.isStarter(defaultBugemon.isStarter());

        return builder.build();
    }

    public boolean isActiveTeamEmpty() {
        return this.activeTeam.isEmpty();
    }
}
