package ulb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repositories.PlayerRepository;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.TeamMemberDTO;
import ulb.repositories.exceptions.TeamEmptyException;
import ulb.repositories.exceptions.TeamNameAlreadyExistsException;
import ulb.repositories.exceptions.TeamNameEmptyException;
import ulb.repositories.exceptions.TeamNotFoundException;
import ulb.services.exceptions.NoActiveTeamException;

public class TeamService {
    private final String playername;
    private final PlayerRepository playerRepository;
    /**
     * The team that the player is currently modifying. It is used to keep track of the changes made to the team before
     * saving it to the database.
     */
    private BugemonTeam workingTeam;

    private Optional<BugemonTeam> activeTeam;
    private List<BugemonTeam> playerTeams;

    public TeamService(PlayerRepository playerRepository, String playername) {
        this.playername = playername;
        this.playerRepository = playerRepository;

        this.playerRepository.createPlayer(this.playername);
        this.playerTeams = this.playerRepository.loadTeams(this.playername);
        this.activeTeam = this.playerRepository.loadCurrentTeam(playername);
    }

    // --- Getters ---

    public Optional<BugemonTeam> getActiveTeam() {
        return this.activeTeam;
    }

    public Optional<String> getActiveTeamName() {
        return this.activeTeam.map(BugemonTeam::getName);
    }

    public BugemonTeam getWorkingTeam() {
        return this.workingTeam;
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
        this.playerRepository.setPlayerCurrentTeam(this.playername, teamName);
    }

    /**
     * Saves the active team to the database.
     *
     * @param teamName
     *            the name of the team to be saved
     * @throws TeamNameAlreadyExistsException
     *             if the team name is already taken
     * @throws TeamEmptyException
     *             if the active team is empty
     * @throws TeamNameEmptyException
     *             if the team name is empty
     */
    public void saveTeam(String teamName)
            throws TeamNameAlreadyExistsException, TeamEmptyException, TeamNameEmptyException {
        if (this.workingTeam.isEmpty()) {
            throw new TeamEmptyException("Active team is empty.");
        }

        this.activeTeam.get().setName(teamName);
        this.playerRepository.createTeam(this.playername, teamName);
        this.persistActiveTeamMembers(this.activeTeam.get());
        this.playerTeams.add(new BugemonTeam(this.activeTeam.get()));
    }

    /**
     * Modifies the active team in the database.
     *
     * @param teamName
     *            the name of the team to be modified
     * @throws TeamEmptyException
     *             if the active team is empty
     * @throws TeamNotFoundException
     *             if the team does not exist
     */
    public void modifyTeam(String teamName) throws TeamEmptyException, TeamNotFoundException {
        List<TeamMemberDTO> members = new ArrayList<>();
        this.activeTeam.get().forEach(b -> members.add(new TeamMemberDTO(this.playername,
                this.activeTeam.get().getName(), b.getName(), this.activeTeam.get().getSlotPosition(b))));

        this.persistActiveTeamMembers(this.activeTeam.get());
        this.playerRepository.modifyTeam(this.playername, this.activeTeam.get().getName(), members);
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
     * @throws TeamNameEmptyException
     *             if the team name is empty
     */
    public void renameTeam(String oldName, String newName) throws TeamNotFoundException, TeamNameAlreadyExistsException,
            NoActiveTeamException, TeamNameEmptyException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player does not have an active team.");
        }

        this.playerRepository.unsetPlayerCurrentTeam(oldName); // Unset the old team because it's a foreign key
        this.playerRepository.renameTeam(this.playername, oldName, newName);
        this.playerRepository.setPlayerCurrentTeam(this.playername, newName);
        this.playerTeams.stream().filter(t -> t.getName().equals(oldName)).forEach(t -> t.setName(newName));
        this.activeTeam.get().setName(newName);
    }

    public boolean isActiveTeamEmpty() {
        return this.activeTeam.isEmpty() || this.activeTeam.get().isEmpty();
    }

    public void clearActiveTeam() {
        this.activeTeam = Optional.empty();
        this.playerRepository.unsetPlayerCurrentTeam(this.playername);
    }

    /**
     * Checks if the working team of the player has been saved to the database. If the working team is empty, it is
     * considered as saved because there is nothing to save (we cannot save an empty team).
     *
     * @return (boolean) true if the working team has been saved, false otherwise
     */
    public boolean isWorkingTeamSaved() {
        if (this.workingTeam.isEmpty()) {
            return true;
        }
        return this.playerTeams.stream().anyMatch(pt -> pt.equals(this.workingTeam));
    }

    public void restoreHpActiveTeam() throws NoActiveTeamException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player does not have an active team.");
        }
        this.activeTeam.get().restoreHp();
    }

    /**
     * Sets the working team of the player to be equal (the same team (by a copy)) to the active team.
     */
    public void setWorkingTeamEqualsActiveTeam() {
        this.activeTeam.ifPresent(team -> this.workingTeam = new BugemonTeam(team));
    }

    /**
     * Adds a bugemon to the working team if it is not already in the team, otherwise removes it from the team.
     *
     * @param bugemon
     *            the bugemon to be added or removed from the working team
     */
    public void addOrRemoveBugemon(Bugemon bugemon) {
        this.workingTeam.addOrRemoveBugemon(bugemon);
    }

    /**
     * Deletes the active team from the database and clears the active team.
     *
     * @param teamName
     *            the name of the team to be deleted
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     * @throws TeamNotFoundException
     *             if the team does not exist
     */
    public void deleteActiveTeam() throws NoActiveTeamException, TeamNotFoundException, TeamNameEmptyException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player does not have an active team to delete.");
        }

        this.playerRepository.deleteTeam(this.playername, this.activeTeam.get().getName());
        this.playerTeams.removeIf(t -> t.equals(this.activeTeam.get()));
        this.workingTeam.clear();
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
        return new PlayerBugemonDTO(this.playername, b.getName(), b.getDefense(), b.getAttack(), b.getInitiative(),
                b.getMaxHp(), b.getXp(), b.getLevel());
    }

    // --- Private Helpers ---

    private void persistActiveTeamMembers(BugemonTeam team) {
        List<PlayerBugemonDTO> owned = this.playerRepository.getPlayerBugemons(this.playername);
        for (Bugemon b : team) {
            if (owned.stream().noneMatch(dto -> dto.bugemonName().equals(b.getName()))) {
                this.playerRepository.savePlayerBugemon(this.toDTO(b));
            }
            this.playerRepository.addTeamMember(
                    new TeamMemberDTO(this.playername, team.getName(), b.getName(), team.getSlotPosition(b)));
        }
    }

    private void updateLocalTeams() {
        this.activeTeam.ifPresent(current -> {
            this.playerTeams.removeIf(t -> t.getName().equals(current.getName()));
            this.playerTeams.add(new BugemonTeam(current));
        });
    }
}
