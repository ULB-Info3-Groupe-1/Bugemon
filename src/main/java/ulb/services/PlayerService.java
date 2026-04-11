package ulb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ulb.factories.BugemonFactory;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repositories.PlayerRepository;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.StaticBugemonDataDTO;
import ulb.repositories.dto.TeamDTO;
import ulb.repositories.dto.TeamMemberDTO;
import ulb.repositories.exceptions.TeamNameAlreadyExistsException;
import ulb.services.exceptions.NoActiveTeamException;
import ulb.services.exceptions.TeamEmptyException;
import ulb.services.exceptions.TeamNotFoundException;

public class PlayerService {
    private final int playerId;
    private final BugemonService bugemonService;
    private final PlayerRepository playerRepository;

    private Optional<BugemonTeam> activeTeam;
    private List<BugemonTeam> playerTeams;
    private Inventory inventory;

    public PlayerService(BugemonService bugemonService, PlayerRepository playerRepository, String playername) {
        this.activeTeam = Optional.empty();
        this.bugemonService = bugemonService;
        this.playerRepository = playerRepository;
        this.playerId = this.playerRepository.getPlayerIdByPlayername(playername)
                .orElseGet(() -> this.playerRepository.createPlayer(playername));
        // TODO: probably connect to db
        this.inventory = InventoryService.addStarterItem(new Inventory());
        this.loadTeams(this.playerRepository.getPlayerTeams(this.playerId));
    }

    // --- Getters ---

    public Optional<BugemonTeam> getActiveTeam() {
        return this.activeTeam;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public List<String> getTeamNames() {
        return this.playerTeams.stream().map(BugemonTeam::getName).toList();
    }

    // --- Team Management ---

    /**
     * Sets the active team for the player.
     *
     * @param teamName
     *            the name of the team to be set
     * @throws TeamNotFoundException
     *             if the team does not exist
     */
    public void setActiveTeam(String teamName) throws TeamNotFoundException {
        BugemonTeam team = this.playerTeams.stream().filter(t -> t.getName().equals(teamName)).findFirst()
                .orElseThrow(() -> new TeamNotFoundException("Team not found: " + teamName));
        this.activeTeam = Optional.of(new BugemonTeam(team));
    }

    /**
     * Saves the active team to the database.
     *
     * @param teamName
     *            the name of the team to be saved
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     * @throws TeamNameAlreadyExistsException
     *             if the team name is already taken
     * @throws TeamEmptyException
     *             if the active team is empty
     */
    public void saveTeam(String teamName)
            throws NoActiveTeamException, TeamNameAlreadyExistsException, TeamEmptyException {
        BugemonTeam current = this.ensureActiveAndNotEmpty();

        if (this.teamNameExists(teamName)) {
            throw new TeamNameAlreadyExistsException("Name taken: " + teamName);
        }

        current.setName(teamName);
        this.playerRepository.createTeam(this.playerId, teamName);
        this.persistActiveTeamMembers(current);

        this.playerTeams.add(new BugemonTeam(current));
    }

    /**
     * Modifies the active team in the database.
     *
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     * @throws TeamEmptyException
     *             if the active team is empty
     */
    public void modifyActiveTeam() throws NoActiveTeamException, TeamEmptyException {
        BugemonTeam current = this.ensureActiveAndNotEmpty();
        List<TeamMemberDTO> members = new ArrayList<>();
        current.forEach(b -> members
                .add(new TeamMemberDTO(this.playerId, current.getName(), b.getName(), current.getSlotPosition(b))));

        this.persistActiveTeamMembers(current); // Assure que les bugemons existent en base
        this.playerRepository.modifyTeam(this.playerId, current.getName(), members);
        this.updateLocalTeams();
    }

    /**
     * Renames a team
     *
     * @param oldName
     *            the old team name
     * @param newName
     *            the new team name
     * @throws TeamNotFoundException
     *             if the team does not exist
     * @throws TeamNameAlreadyExistsException
     *             if the team name is already taken
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     */
    public void renameTeam(String oldName, String newName)
            throws TeamNotFoundException, TeamNameAlreadyExistsException, NoActiveTeamException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player does not have an active team.");
        }

        if (!this.teamNameExists(oldName)) {
            throw new TeamNotFoundException("No team saved with the name " + oldName);
        }

        try {
            this.playerRepository.renameTeam(this.playerId, oldName, newName);
        } catch (TeamNameAlreadyExistsException e) {
            throw new TeamNameAlreadyExistsException("Team name already exists: " + newName);
        }

        this.playerTeams.stream().filter(t -> t.getName().equals(oldName)).forEach(t -> t.setName(newName));
        this.activeTeam.get().setName(newName);
    }

    public boolean isActiveTeamEmpty() {
        return this.activeTeam.isEmpty() || this.activeTeam.get().isEmpty();
    }

    public void clearActiveTeam() {
        this.activeTeam = Optional.empty();
    }

    /**
     * Checks if the active team of the player has been saved to the database. If the active team is empty, it is
     * considered as saved because there is nothing to save (we cannot s).
     *
     * @return (boolean) true if the active team has been saved, false otherwise
     */
    public boolean isActiveTeamSaved() {
        if (this.activeTeam.isEmpty() || this.activeTeam.get().isEmpty()) {
            return true;
        }
        return this.activeTeam.map(team -> this.playerTeams.stream().anyMatch(pt -> pt.equals(team))).orElse(false);
    }

    public void restoreHpActiveTeam() throws NoActiveTeamException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player does not have an active team.");
        }
        this.activeTeam.get().restoreHp();
    }

    public void addOrRemoveBugemonOfActiveTeam(Bugemon bugemon) {
        if (this.activeTeam.isEmpty()) {
            this.activeTeam = Optional.of(new BugemonTeam());
        }
        this.activeTeam.get().addOrRemoveBugemon(bugemon);
    }

    /**
     * Deletes the active team from the database and clears the active team.
     *
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     */
    public void deleteActiveTeam() throws NoActiveTeamException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player does not have an active team to delete.");
        }

        this.playerRepository.deleteTeam(this.playerId, this.activeTeam.get().getName());
        this.playerTeams.removeIf(t -> t.equals(this.activeTeam.get()));
        this.activeTeam = Optional.empty();
    }

    // --- Bugemon State ---

    /**
     * Saves the state of the bugemons of the active team to the database.
     *
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     */
    public void saveBugemonStateOfActiveTeam() throws NoActiveTeamException {
        BugemonTeam team = this.activeTeam.orElseThrow(() -> new NoActiveTeamException("No active team"));
        team.forEach(this::updateBugemonInDb);
        this.updateLocalTeams();
    }

    /**
     * Saves the state of a single bugemon to the database.
     *
     * @param bugemon
     *            the bugemon to save
     */
    public void saveBugemonState(Bugemon bugemon) {
        this.playerRepository.updatePlayerBugemon(
                new PlayerBugemonDTO(this.playerId, bugemon.getName(), bugemon.getDefense(), bugemon.getAttack(),
                        bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));

        this.updateLocalTeams();
    }

    private void updateBugemonInDb(Bugemon b) {
        this.playerRepository.updatePlayerBugemon(this.toDTO(b));
    }

    private PlayerBugemonDTO toDTO(Bugemon b) {
        return new PlayerBugemonDTO(this.playerId, b.getName(), b.getDefense(), b.getAttack(), b.getInitiative(),
                b.getMaxHp(), b.getXp(), b.getLevel());
    }

    // --- Private Helpers ---

    private void persistActiveTeamMembers(BugemonTeam team) {
        List<PlayerBugemonDTO> owned = this.playerRepository.getPlayerBugemons(this.playerId);
        for (Bugemon b : team) {
            if (owned.stream().noneMatch(dto -> dto.bugemonName().equals(b.getName()))) {
                this.playerRepository.savePlayerBugemon(this.toDTO(b));
            }
            this.playerRepository.addTeamMember(
                    new TeamMemberDTO(this.playerId, team.getName(), b.getName(), team.getSlotPosition(b)));
        }
    }

    private BugemonTeam ensureActiveAndNotEmpty() throws NoActiveTeamException, TeamEmptyException {
        BugemonTeam team = this.activeTeam.orElseThrow(() -> new NoActiveTeamException("No active team"));
        if (team.isEmpty()) {
            throw new TeamEmptyException("Team is empty");
        }
        return team;
    }

    private void updateLocalTeams() {
        this.activeTeam.ifPresent(current -> {
            this.playerTeams.removeIf(t -> t.getName().equals(current.getName()));
            this.playerTeams.add(new BugemonTeam(current));
        });
    }

    private boolean teamNameExists(String teamName) {
        return this.playerTeams.stream().anyMatch(team -> team.getName().equals(teamName));
    }

    /**
     * Loads the teams of the player from the database.
     *
     * @param teams
     *            (List<TeamDTO>) the teams of the player to be loaded
     */
    private void loadTeams(List<TeamDTO> teams) {
        this.playerTeams = new ArrayList<>();
        List<PlayerBugemonDTO> allBugemons = this.playerRepository.getPlayerBugemons(this.playerId);

        for (TeamDTO dto : teams) {
            BugemonTeam team = new BugemonTeam();
            team.setName(dto.teamName());
            this.playerRepository.getTeamMembers(this.playerId, dto.teamName())
                    .forEach(m -> allBugemons.stream().filter(b -> b.bugemonName().equals(m.bugemonName())).findFirst()
                            .ifPresent(pb -> team.add(BugemonFactory.createBugemon(this.getDefaultBugemon(pb), pb))));
            this.playerTeams.add(team);
        }
    }

    private StaticBugemonDataDTO getDefaultBugemon(PlayerBugemonDTO playerBugemon) {
        return BugemonFactory
                .createStaticBugemonData(this.bugemonService.getBugemonByName(playerBugemon.bugemonName()));
    }
}
