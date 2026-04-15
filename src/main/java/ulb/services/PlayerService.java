package ulb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.player.Player;
import ulb.models.player.exceptions.NoActiveTeamException;
import ulb.repositories.PlayerRepository;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.TeamMemberDTO;
import ulb.repositories.exceptions.PlayernameIsEmptyException;
import ulb.repositories.exceptions.TeamEmptyException;
import ulb.repositories.exceptions.TeamNameAlreadyExistsException;
import ulb.repositories.exceptions.TeamNameEmptyException;
import ulb.repositories.exceptions.TeamNotFoundException;

public class PlayerService {

    private final Player player;
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository, String playername) throws PlayernameIsEmptyException {
        this.playerRepository = playerRepository;

        int playerId = this.playerRepository.getPlayerIdOrCreatePlayer(playername);
        // TODO: probably connect to db the inventory
        this.player = new Player(this.playerRepository.getPlayerIdOrCreatePlayer(playername),
                this.playerRepository.loadTeams(playerId), InventoryService.addStarterItems(new Inventory()));
    }

    // --- Getters ---

    public Optional<BugemonTeam> getActiveTeam() {
        return this.player.getActiveTeam();
    }

    public Inventory getInventory() {
        return this.player.getInventory();
    }

    public List<String> getTeamNames() {
        return this.player.getTeams().stream().map(BugemonTeam::getName).toList();
    }

    public int getPlayerId() {
        return this.player.getId();
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
        BugemonTeam team = this.player.getTeams().stream().filter(t -> t.getName().equals(teamName)).findFirst()
                .orElseThrow(() -> new TeamNotFoundException("Team not found: " + teamName));
        this.player.setActiveTeam(team);
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
            throws NoActiveTeamException, TeamNameAlreadyExistsException, TeamEmptyException, TeamNameEmptyException {

        if (this.player.getActiveTeam().isEmpty()) {
            throw new NoActiveTeamException("Player has no active team.");
        }

        if (this.player.isActiveTeamEmpty()) {
            throw new TeamEmptyException("Active team is empty.");
        }

        this.player.setActiveTeamName(teamName);
        this.playerRepository.createTeam(this.player.getId(), teamName);
        this.persistActiveTeamMembers(this.player.getActiveTeam().orElseThrow(() -> new NoActiveTeamException(
                "The player team to save doesn't exist. The active team should exist now ")));
        this.player.addActiveTeamToCache();
    }

    /**
     * Modifies the active team in the database.
     *
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     * @throws TeamEmptyException
     *             if the active team is empty
     * @throws TeamNotFoundException
     *             if the team does not exist
     */
    public void modifyActiveTeam() throws NoActiveTeamException, TeamEmptyException, TeamNotFoundException {
        List<TeamMemberDTO> members = new ArrayList<>();
        BugemonTeam team = this.player.getActiveTeam().orElseThrow(this.player.noActiveTeamException());
        team.forEach(b -> members.add(new TeamMemberDTO(this.player.getId(), this.player.getActiveTeamName(),
                b.getName(), team.getSlotPosition(b))));
        this.persistActiveTeamMembers(team);
        this.playerRepository.modifyTeam(this.player.getId(), this.player.getActiveTeamName(), members);
        this.updatePlayerCacheWithActiveTeam();
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
    public void renameTeam(String oldName, String newName) throws TeamNotFoundException, TeamNameAlreadyExistsException,
            NoActiveTeamException, TeamNameEmptyException {
        this.playerRepository.renameTeam(this.player.getId(), oldName, newName);
        this.player.renameTeamInCache(oldName, newName);
        this.player.setActiveTeamName(newName);
    }

    public boolean isActiveTeamEmpty() {
        return this.player.isActiveTeamEmpty();
    }

    public void clearActiveTeam() {
        this.player.clearActiveTeam();
    }

    public boolean isActiveTeamSaved() {
        return this.player.isActiveTeamSaved();
    }

    public void restoreHpActiveTeam() throws NoActiveTeamException {
        this.player.restoreHp();
    }

    public void addOrRemoveBugemonOfActiveTeam(Bugemon bugemon) {
        if (this.player.isActiveTeamEmpty()) {
            this.player.createNewTeam();
        }
        this.player.addOrRemoveBugemonOfActiveTeam(bugemon);
    }

    /**
     * Deletes the active team from the database and clears the active team.
     *
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     * @throws TeamNotFoundException
     *             if the team does not exist
     */
    public void deleteActiveTeam() throws NoActiveTeamException, TeamNotFoundException, TeamNameEmptyException {
        this.playerRepository.deleteTeam(this.player.getId(), this.player.getActiveTeamName());
        this.player.deleteActiveTeamFromCache();
        this.player.clearActiveTeam();
    }

    /**
     * Updates the player cache with the active team when the active team has changed so it's different from the cache.
     *
     * @throws NoActiveTeamException
     */
    public void updatePlayerCacheWithActiveTeam() throws NoActiveTeamException {
        this.player.updateCacheWithActiveTeam();
    }

    // --- Private Helpers ---

    private void persistActiveTeamMembers(BugemonTeam team) {
        List<PlayerBugemonDTO> owned = this.playerRepository.getPlayerBugemons(this.player.getId());
        for (Bugemon b : team) {
            if (owned.stream().noneMatch(dto -> dto.bugemonName().equals(b.getName()))) {
                this.playerRepository.savePlayerBugemon(this.toDTO(b));
            }
            this.playerRepository.addTeamMember(
                    new TeamMemberDTO(this.player.getId(), team.getName(), b.getName(), team.getSlotPosition(b)));
        }
    }

    private PlayerBugemonDTO toDTO(Bugemon b) {
        return new PlayerBugemonDTO(this.player.getId(), b.getName(), b.getDefense(), b.getAttack(), b.getInitiative(),
                b.getMaxHp(), b.getXp(), b.getLevel());
    }
}
