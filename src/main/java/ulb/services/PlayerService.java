package ulb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repositories.PlayerRepository;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.TeamMemberDTO;
import ulb.repositories.exceptions.PlayernameIsEmptyException;
import ulb.repositories.exceptions.TeamEmptyException;
import ulb.repositories.exceptions.TeamNameAlreadyExistsException;
import ulb.repositories.exceptions.TeamNameEmptyException;
import ulb.repositories.exceptions.TeamNotFoundException;
import ulb.services.exceptions.NoActiveTeamException;

/**
 * Manages the player's runtime state: active team, team list, and inventory.
 * All persistence is delegated to {@link ulb.repositories.PlayerRepository}.
 */
public class PlayerService {
    private final int playerId;
    private final PlayerRepository playerRepository;

    private Optional<BugemonTeam> activeTeam;
    private List<BugemonTeam> playerTeams;
    private Inventory inventory;

    public PlayerService(PlayerRepository playerRepository, String playername) throws PlayernameIsEmptyException {
        this.activeTeam = Optional.empty();
        this.playerRepository = playerRepository;
        this.playerId = this.playerRepository.getPlayerIdOrCreatePlayer(playername);
        // TODO: probably connect to db
        this.inventory = InventoryService.addStarterItems(new Inventory());
        this.playerTeams = this.playerRepository.loadTeams(this.playerId);
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

    public int getPlayerId() {
        return this.playerId;
    }

    // --- Team Management ---

    /**
     * Sets the active team by name.
     *
     * @throws TeamNotFoundException if no team with that name exists
     */
    public void setActiveTeam(String teamName) throws TeamNotFoundException {
        BugemonTeam team = this.playerTeams.stream().filter(t -> t.getName().equals(teamName)).findFirst()
                .orElseThrow(() -> new TeamNotFoundException("Team not found: " + teamName));
        this.activeTeam = Optional.of(new BugemonTeam(team));
    }

    /**
     * Persists the active team under the given name.
     *
     * @throws NoActiveTeamException if there is no active team
     * @throws TeamEmptyException if the active team has no members
     * @throws TeamNameAlreadyExistsException if the name is already taken
     */
    public void saveTeam(String teamName)
            throws NoActiveTeamException, TeamNameAlreadyExistsException, TeamEmptyException, TeamNameEmptyException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player does not have an active team.");
        }

        if (this.activeTeam.get().isEmpty()) {
            throw new TeamEmptyException("Active team is empty.");
        }

        this.activeTeam.get().setName(teamName);
        this.playerRepository.createTeam(this.playerId, teamName);
        this.persistActiveTeamMembers(this.activeTeam.get());
        this.playerTeams.add(new BugemonTeam(this.activeTeam.get()));
    }

    /**
     * Overwrites the active team's composition in the database.
     *
     * @throws NoActiveTeamException if there is no active team
     * @throws TeamEmptyException if the active team has no members
     * @throws TeamNotFoundException if the team no longer exists in the database
     */
    public void modifyActiveTeam() throws NoActiveTeamException, TeamEmptyException, TeamNotFoundException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player does not have an active team.");
        }

        List<TeamMemberDTO> members = new ArrayList<>();
        this.activeTeam.get().forEach(b -> members.add(new TeamMemberDTO(this.playerId, this.activeTeam.get().getName(),
                b.getName(), this.activeTeam.get().getSlotPosition(b))));

        this.persistActiveTeamMembers(this.activeTeam.get());
        this.playerRepository.modifyTeam(this.playerId, this.activeTeam.get().getName(), members);
        this.updateLocalTeams();
    }

    /**
     * Renames the active team from {@code oldName} to {@code newName}.
     *
     * @throws TeamNotFoundException if {@code oldName} does not exist
     * @throws TeamNameAlreadyExistsException if {@code newName} is already taken
     * @throws NoActiveTeamException if there is no active team
     */
    public void renameTeam(String oldName, String newName) throws TeamNotFoundException, TeamNameAlreadyExistsException,
            NoActiveTeamException, TeamNameEmptyException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player does not have an active team.");
        }

        this.playerRepository.renameTeam(this.playerId, oldName, newName);
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
     * Returns {@code true} if the active team matches a persisted team, or if there is no active team.
     *
     * @return {@code false} if the active team has unsaved changes
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
     * Deletes the active team from the database and clears it from the runtime state.
     *
     * @throws NoActiveTeamException if there is no active team to delete
     * @throws TeamNotFoundException if the team no longer exists in the database
     */
    public void deleteActiveTeam() throws NoActiveTeamException, TeamNotFoundException, TeamNameEmptyException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player does not have an active team to delete.");
        }

        this.playerRepository.deleteTeam(this.playerId, this.activeTeam.get().getName());
        this.playerTeams.removeIf(t -> t.equals(this.activeTeam.get()));
        this.activeTeam = Optional.empty();
    }

    // --- Bugemon State ---

    /**
     * Persists the current stats (HP, XP, level) of every Bugemon in the active team.
     *
     * @throws NoActiveTeamException if there is no active team
     */
    public void saveBugemonStateOfActiveTeam() throws NoActiveTeamException {
        BugemonTeam team = this.activeTeam.orElseThrow(() -> new NoActiveTeamException("No active team"));
        team.forEach(this::updateBugemonInDb);
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

    public void updateLocalTeams() {
        this.activeTeam.ifPresent(current -> {
            this.playerTeams.removeIf(t -> t.getName().equals(current.getName()));
            this.playerTeams.add(new BugemonTeam(current));
        });
    }
}
