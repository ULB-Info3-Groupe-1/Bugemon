package ulb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ulb.models.bugemon.Bugemon;
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

public class TeamService {
    private final int playerId;
    private final PlayerRepository playerRepository;

    private Optional<BugemonTeam> activeTeam;
    private List<BugemonTeam> playerTeams;

    public TeamService(PlayerRepository playerRepository, int playerId) throws PlayernameIsEmptyException {
        this.playerId = playerId;
        this.activeTeam = Optional.empty();
        this.playerRepository = playerRepository;
        this.playerTeams = this.playerRepository.loadTeams(this.playerId);
    }

    // --- Getters ---

    public Optional<BugemonTeam> getActiveTeam() {
        return this.activeTeam;
    }

    public List<String> getTeamNames() {
        return this.playerTeams.stream().map(BugemonTeam::getName).toList();
    }

    public int getPlayerId() {
        return this.playerId;
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
     * @throws TeamNotFoundException
     *             if the team does not exist
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
