package ulb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ulb.factories.BugemonFactory;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repositories.PlayerRepository;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.StaticBugemonDataDTO;
import ulb.repositories.dto.TeamDTO;
import ulb.repositories.dto.TeamMemberDTO;
import ulb.services.exceptions.NoActiveTeamException;
import ulb.services.exceptions.TeamEmptyException;
import ulb.services.exceptions.TeamNameAlreadyExistsException;
import ulb.services.exceptions.TeamNotFoundException;

public class PlayerService {
    // Unique identifier for the player.
    private final int playerId;

    // Player's active team
    private Optional<BugemonTeam> activeTeam = Optional.empty();

    // List of all teams owned by the player
    private List<TeamDTO> playerTeams;

    // Player Inventory
    private Inventory inventory;

    private final BugemonService bugemonService;
    private final PlayerRepository playerRepository;

    /** Retrieves or creates the player by playername, then loads their teams and starter inventory. */
    public PlayerService(BugemonService bugemonService, PlayerRepository playerRepository, String playername) {
        this.bugemonService = bugemonService;
        this.playerRepository = playerRepository;
        this.playerId = this.playerRepository.getPlayerIdByPlayername(playername)
                .orElseGet(() -> this.playerRepository.createPlayer(playername));
        this.playerTeams = this.playerRepository.getPlayerTeams(this.playerId);

        // TODO: probably connect to db
        this.inventory = InventoryService.addStarterItem(new Inventory());
    }

    public Optional<BugemonTeam> getActiveTeam() {
        return this.activeTeam;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void setActiveTeam(BugemonTeam team) {
        this.activeTeam = Optional.of(team);
    }

    public List<String> getTeamNames() {
        return this.playerTeams.stream().map(TeamDTO::teamName).toList();
    }

    /**
     * @throws TeamNotFoundException
     *             if oldName does not exist
     * @throws TeamNameAlreadyExistsException
     *             if newName is already taken
     */
    public void renameTeam(String oldName, String newName)
            throws TeamNotFoundException, TeamNameAlreadyExistsException, NoActiveTeamException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player does not have an active team.");
        }

        if (!this.teamNameExists(oldName)) {
            throw new TeamNotFoundException("No team saved with the name " + oldName);
        }

        if (this.teamNameExists(newName)) {
            throw new TeamNameAlreadyExistsException("A team is already saved with the name " + newName);
        }

        this.playerRepository.renameTeam(this.playerId, oldName, newName);
        this.playerTeams.removeIf(t -> t.teamName().equals(oldName));
        this.playerTeams.add(new TeamDTO(this.playerId, newName));
        this.activeTeam.get().setName(newName);
    }

    /**
     * @throws TeamNotFoundException
     *             if teamName does not exist
     */
    public void deleteTeam(String teamName) throws TeamNotFoundException {
        if (!this.teamNameExists(teamName)) {
            throw new TeamNotFoundException("No team saved with the name " + teamName);
        }

        this.playerRepository.deleteTeam(this.playerId, teamName);
        this.playerTeams.removeIf(t -> t.teamName().equals(teamName));

        if (this.activeTeam.isPresent() && this.activeTeam.get().getName().equals(teamName)) {
            this.activeTeam = Optional.empty();
        }
    }

    /**
     * Replaces all members of the stored team, registering any Bugemon not yet owned by this player.
     *
     * @throws TeamNameAlreadyExistsException
     *             if teamName already exists
     */
    public void updateTeamMembers(String teamName, BugemonTeam team) {
        this.playerRepository.deleteTeamMembers(this.playerId, teamName);
        List<PlayerBugemonDTO> playerBugemonDTOs = this.playerRepository.getPlayerBugemons(this.playerId);
        for (Bugemon bugemon : team) {
            if (playerBugemonDTOs.stream().noneMatch(dto -> dto.bugemonName().equals(bugemon.getName()))) {
                this.playerRepository.savePlayerBugemon(new PlayerBugemonDTO(this.playerId, bugemon.getName(),
                        bugemon.getDefense(), bugemon.getAttack(), bugemon.getInitiative(), bugemon.getMaxHp(),
                        bugemon.getXp(), bugemon.getLevel()));
            }
            TeamMemberDTO memberDTO = new TeamMemberDTO(this.playerId, teamName, bugemon.getName(),
                    team.getSlotPosition(bugemon));
            this.playerRepository.addTeamMember(memberDTO);
        }
    }

    /**
     * Saves the active team to the database.
     *
     * @param teamName
     *            the name of the team to be saved
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     * @throws TeamNameAlreadyExistsException
     *             if a team with the same name already exists
     * @throws TeamEmptyException
     *             if the active team is empty
     */
    public void saveTeam(String teamName)
            throws NoActiveTeamException, TeamNameAlreadyExistsException, TeamEmptyException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player does not have an active team.");
        }

        if (this.activeTeam.get().isEmpty()) {
            throw new TeamEmptyException("Team is empty!");
        }

        if (this.teamNameExists(teamName)) {
            throw new TeamNameAlreadyExistsException("A team is already saved with the name " + teamName);
        }

        this.activeTeam.get().setName(teamName);
        this.playerRepository.createTeam(this.playerId, teamName);
        List<PlayerBugemonDTO> playerBugemonDTOs = this.playerRepository.getPlayerBugemons(this.playerId);
        for (Bugemon bugemon : this.activeTeam.get()) {
            if (playerBugemonDTOs.stream().noneMatch(dto -> dto.bugemonName().equals(bugemon.getName()))) {
                this.playerRepository.savePlayerBugemon(new PlayerBugemonDTO(this.playerId, bugemon.getName(),
                        bugemon.getDefense(), bugemon.getAttack(), bugemon.getInitiative(), bugemon.getMaxHp(),
                        bugemon.getXp(), bugemon.getLevel()));
            }
            TeamMemberDTO memberDTO = new TeamMemberDTO(this.playerId, teamName, bugemon.getName(),
                    this.activeTeam.get().getSlotPosition(bugemon));
            this.playerRepository.addTeamMember(memberDTO);
        }
        this.playerTeams.add(new TeamDTO(this.playerId, teamName));
    }

    /**
     * Loads a team and sets it as the active team.
     *
     * @param teamName
     *            the name of the team to load
     * @throws TeamNotFoundException
     *             if teamName does not exist
     */
    public void loadTeamAndSetActiveTeam(String teamName) throws TeamNotFoundException {
        if (!this.teamNameExists(teamName)) {
            throw new TeamNotFoundException("No team saved with the name " + teamName);
        }

        List<TeamMemberDTO> teamMembers = this.playerRepository.getTeamMembers(this.playerId, teamName);
        List<PlayerBugemonDTO> playerBugemons = this.playerRepository.getPlayerBugemons(this.playerId);

        BugemonTeam loadTeam = new BugemonTeam();
        loadTeam.setName(teamName);

        for (TeamMemberDTO member : teamMembers) {
            PlayerBugemonDTO playerBugemon = playerBugemons.stream()
                    .filter(b -> b.bugemonName().equals(member.bugemonName())).findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Player Bugemon with name " + member.bugemonName() + (" not found. Cannot load team.")));

            loadTeam.add(this.buildPlayerBugemon(playerBugemon));
        }

        this.activeTeam = Optional.of(loadTeam);
    }

    private boolean teamNameExists(String teamName) {
        return this.playerTeams.stream().anyMatch(team -> team.teamName().equals(teamName));
    }

    /**
     * @throws IllegalArgumentException
     *             if bugemon is not in the active team
     */
    public void saveBugemonState(Bugemon bugemon) {
        this.playerRepository.updatePlayerBugemon(
                new PlayerBugemonDTO(this.playerId, bugemon.getName(), bugemon.getDefense(), bugemon.getAttack(),
                        bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));
    }

    /** Persists the current state (XP, level, stats) of every Bugemon in the active team. */
    public void saveActiveTeamState() throws NoActiveTeamException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player does not have an active team.");
        }
        for (Bugemon bugemon : this.activeTeam.get()) {
            this.playerRepository.updatePlayerBugemon(
                    new PlayerBugemonDTO(this.playerId, bugemon.getName(), bugemon.getDefense(), bugemon.getAttack(),
                            bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));
        }
    }

    public void restoreHpActiveTeam() throws NoActiveTeamException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player does not have an active team.");
        }
        this.activeTeam.get().restoreHp();
    }

    private Bugemon buildPlayerBugemon(PlayerBugemonDTO playerBugemon) {
        StaticBugemonDataDTO defaultBugemon = BugemonFactory
                .createStaticBugemonData(this.bugemonService.getBugemonByName(playerBugemon.bugemonName()));

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
        return this.activeTeam.isEmpty() || this.activeTeam.get().isEmpty();
    }

    /**
     * Modifies the active team whose has been modified by the player.
     *
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     * @throws TeamEmptyException
     *             if the active team is empty
     */
    public void modifyActiveTeam() throws NoActiveTeamException, TeamEmptyException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player does not have an active team.");
        }

        if (this.activeTeam.get().isEmpty()) {
            throw new TeamEmptyException("Team is empty!");
        }

        List<TeamMemberDTO> teamMembers = new ArrayList<>();
        List<PlayerBugemonDTO> playerBugemonDTOs = this.playerRepository.getPlayerBugemons(this.playerId);
        for (Bugemon bugemon : this.activeTeam.get()) {
            if (playerBugemonDTOs.stream().noneMatch(dto -> dto.bugemonName().equals(bugemon.getName()))) {
                this.playerRepository.savePlayerBugemon(new PlayerBugemonDTO(this.playerId, bugemon.getName(),
                        bugemon.getDefense(), bugemon.getAttack(), bugemon.getInitiative(), bugemon.getMaxHp(),
                        bugemon.getXp(), bugemon.getLevel()));
            }
            teamMembers.add(new TeamMemberDTO(this.playerId, this.activeTeam.get().getName(), bugemon.getName(),
                    this.activeTeam.get().getSlotPosition(bugemon)));
        }
        this.playerRepository.modifyTeam(this.playerId, this.activeTeam.get().getName(), teamMembers);
    }

    public void clearActiveTeam() {
        this.activeTeam = Optional.empty();
    }

    /**
     * Adds or removes a bugemon from the active team depending on whether it is already in the team. Creates a new team
     * if the player does not have an active team.
     *
     * @param bugemon
     *            the bugemon to add or remove
     */
    public void addOrRemoveBugemonOfActiveTeam(Bugemon bugemon) {
        if (this.activeTeam.isEmpty()) {
            this.activeTeam = Optional.of(new BugemonTeam());
        }

        if (this.activeTeam.get().contains(bugemon)) {
            this.activeTeam.get().remove(bugemon);
        } else if (!this.activeTeam.get().isFull()) {
            this.activeTeam.get().add(new Bugemon(bugemon));
        }
    }

    /**
     * Checks if the active team of the player has been saved to the database.
     *
     * @return (boolean) true if the active team has been saved, false otherwise
     */
    public boolean isActiveTeamSaved() {
        return this.activeTeam
                .map(team -> this.playerTeams.stream()
                        .anyMatch(pt -> pt.teamName().equals(team.getName()) && pt.playerId() == this.playerId))
                .orElse(false);
    }
}
