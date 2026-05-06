package ulb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repositories.PlayerRepository;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.TeamMemberDTO;
import ulb.repositories.exceptions.PlayernameAlreadyExistsException;
import ulb.repositories.exceptions.TeamEmptyException;
import ulb.repositories.exceptions.TeamNameAlreadyExistsException;
import ulb.repositories.exceptions.TeamNameEmptyException;
import ulb.repositories.exceptions.TeamNotFoundException;
import ulb.services.exceptions.NoActiveTeamException;

/**
 * Service responsible for team persistence.
 */
public class TeamService {

    /**
     * The name of the player to use to interact with the database.
     */
    private final String playername;

    /**
     * The repository used to interact with the database.
     */
    private final PlayerRepository playerRepository;

    /**
     * The team that the player is currently modifying. It is used to keep track of the changes made to the team before
     * saving it to the database.
     */
    private BugemonTeam workingTeam;

    /**
     * The team that the player is currently using.
     */
    private Optional<BugemonTeam> activeTeam;

    /**
     * The list of teams that the player has.
     */
    private List<BugemonTeam> playerTeams;

    /**
     * Constructor for the TeamService.
     *
     * @param playerRepository
     *            the repository used to interact with the database
     * @param playername
     *            the name of the player to use to interact with the database
     */
    public TeamService(PlayerRepository playerRepository, String playername) {
        this.playername = playername;
        this.workingTeam = new BugemonTeam();
        this.playerRepository = playerRepository;
        this.activeTeam = Optional.empty();
        this.playerTeams = new ArrayList<>();

        try {
            this.playerRepository.createPlayer(this.playername);
        } catch (PlayernameAlreadyExistsException e) {
            // We do nothing because whitout client/server architecture, the database is local and we don't have a login
            // system, so the playername used is 'default_player' and is always the same.
        }
    }

    // --- Getters ---

    /**
     * Returns the active team of the player.
     *
     * @return the active team
     */
    public Optional<BugemonTeam> getActiveTeam() {
        return this.activeTeam;
    }

    /**
     * Returns the name of the active team of the player.
     *
     * @return the name of the active team
     */
    public Optional<String> getActiveTeamName() {
        return this.activeTeam.map(BugemonTeam::getName);
    }

    /**
     * Returns the working team of the player
     *
     * @return the working team
     */
    public BugemonTeam getWorkingTeam() {
        return this.workingTeam;
    }

    /**
     * Returns the names of the teams of the player
     *
     * @return the names of the teams
     */
    public List<String> getTeamNames() {
        return this.playerTeams.stream().map(BugemonTeam::getName).toList();
    }

    // --- Team Management ---

    /**
     * Loads the teams of the player from the database and sets the active team. If the player does not have an active
     * team, the active team is set to empty. It also clears the local list of teams before loading them from the
     * database to avoid duplicates in case this method is called multiple times.
     */
    public void loadTeamsAndActiveTeam() {
        this.playerTeams.clear();
        this.playerTeams.addAll(this.playerRepository.loadTeams(this.playername));
        this.activeTeam = this.playerRepository.loadCurrentTeam(this.playername);
    }

    /**
     * Deletes all teams of the player from the database and clears the active team.
     */
    public void clearTeamsAndActiveTeam() {
        this.clearActiveTeam();
        this.playerRepository.clearTeams(this.playername);
        this.playerTeams.clear();
    }

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
     * Saves the working team to the database. It needs the name to give to the working team to save it in db.
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
        this.checkWorkingTeamIsNotEmpty();
        this.workingTeam.setName(teamName);
        this.playerRepository.createTeam(this.playername, teamName);
        this.persistTeamMembers();
        this.playerTeams.add(new BugemonTeam(this.workingTeam));
        this.workingTeam.clear(); // Clear because the working team is saved so by clearing it we can create a new team
    }

    /**
     * Modifies the active team in the database.
     *
     * @throws TeamEmptyException
     *             if the active team is empty
     * @throws TeamNotFoundException
     *             if the team does not exist
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     */
    public void modifyActiveTeam() throws TeamEmptyException, TeamNotFoundException, NoActiveTeamException {
        this.checkWorkingTeamIsNotEmpty();
        this.checkActiveTeamIsPresent();

        // Update the active team with the working team that has the modifications
        this.workingTeam.setName(this.activeTeam.get().getName());
        this.activeTeam = Optional.of(new BugemonTeam(this.workingTeam));

        List<TeamMemberDTO> members = new ArrayList<>();
        this.workingTeam.forEach(b -> members.add(new TeamMemberDTO(this.playername, this.workingTeam.getName(),
                b.getName(), this.workingTeam.getSlotPosition(b))));

        Set<String> ownedBugemonNames = this.playerRepository.getPlayerBugemons(this.playername).stream()
                .map(PlayerBugemonDTO::bugemonName).collect(Collectors.toSet());
        for (Bugemon b : this.workingTeam) {
            if (!ownedBugemonNames.contains(b.getName())) {
                this.playerRepository.savePlayerBugemon(this.toDTO(b));
            }
        }

        this.playerRepository.modifyTeam(this.playername, this.workingTeam.getName(), members);
        this.playerTeams.removeIf(t -> t.getName().equals(this.workingTeam.getName()));
        this.playerTeams.add(new BugemonTeam(this.workingTeam));
    }

    /**
     * Renames a team
     *
     * @param newName
     *            the new team name
     * @throws TeamNotFoundException
     *             if the team does not exist
     * @throws TeamNameAlreadyExistsException
     *             if the team name is already taken
     * @throws TeamNameEmptyException
     *             if the team name is empty
     */
    public void renameActiveTeam(String newName) throws TeamNotFoundException, TeamNameAlreadyExistsException,
            TeamNameEmptyException, NoActiveTeamException {
        this.checkActiveTeamIsPresent();
        this.playerRepository.unsetPlayerCurrentTeam(this.playername); // Unset the old team because it's a foreign key
        this.playerRepository.renameTeam(this.playername, this.activeTeam.get().getName(), newName);
        this.playerRepository.setPlayerCurrentTeam(this.playername, newName);
        this.playerTeams.stream().filter(t -> t.getName().equals(this.activeTeam.get().getName()))
                .forEach(t -> t.setName(newName));
        this.workingTeam.setName(newName);
        this.activeTeam.get().setName(newName);
    }

    /**
     * Deletes the active team from the database and clears the active team.
     *
     * @param teamName
     *            the name of the team to be deleted
     * @throws TeamNotFoundException
     *             if the team does not exist
     * @throws TeamNameEmptyException
     *             if the team name is empty
     */
    public void deleteTeam(String teamName)
            throws TeamNotFoundException, TeamNameEmptyException, NoActiveTeamException {
        this.checkActiveTeamIsPresent();
        this.playerTeams.removeIf(t -> t.equals(this.activeTeam.get()));
        this.playerRepository.unsetPlayerCurrentTeam(this.playername);
        this.playerRepository.deleteTeam(this.playername, teamName);
        this.workingTeam.clear();
        this.activeTeam = Optional.empty();

    }

    /**
     * Checks if the active team is empty.
     *
     * @return (boolean) true if the active team is empty, false otherwise
     */
    public boolean isActiveTeamEmpty() {
        return this.activeTeam.isEmpty() || this.activeTeam.get().isEmpty();
    }

    /**
     * Clears the active team.
     */
    public void clearActiveTeam() {
        this.activeTeam = Optional.empty();
        this.playerRepository.unsetPlayerCurrentTeam(this.playername);
    }

    /**
     * Clears the working team.
     */
    public void clearWorkingTeam() {
        this.workingTeam.clear();
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

    /**
     * Restores the HP of all the bugemons of the active team to their maximum HP.
     *
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     */
    public void restoreHpActiveTeam() throws NoActiveTeamException {
        this.checkActiveTeamIsPresent();
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

    // --- Bugemon State ---

    /**
     * Saves the state of the bugemons of the active team to the database.
     *
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     */
    public void saveBugemonStateOfActiveTeam() throws NoActiveTeamException {
        this.checkActiveTeamIsPresent();
        this.activeTeam.get().forEach(this::updateBugemonInDb);
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

    /**
     * Saves the members of the working team to the database. If a bugemon of the working team is not owned by the
     * player, it is first saved to the database before being added as a team member.
     */
    private void persistTeamMembers() {
        List<PlayerBugemonDTO> owned = this.playerRepository.getPlayerBugemons(this.playername);
        for (Bugemon b : this.workingTeam) {
            if (owned.stream().noneMatch(dto -> dto.bugemonName().equals(b.getName()))) {
                this.playerRepository.savePlayerBugemon(this.toDTO(b));
            }
            this.playerRepository.addTeamMember(new TeamMemberDTO(this.playername, this.workingTeam.getName(),
                    b.getName(), this.workingTeam.getSlotPosition(b)));
        }
    }

    private void updateLocalTeams() {
        this.activeTeam.ifPresent(current -> {
            this.playerTeams.removeIf(t -> t.getName().equals(current.getName()));
            this.playerTeams.add(new BugemonTeam(current));
        });
    }

    private void checkActiveTeamIsPresent() throws NoActiveTeamException {
        if (this.activeTeam.isEmpty()) {
            throw new NoActiveTeamException("Player doesn't have an active Team");
        }
    }

    private void checkWorkingTeamIsNotEmpty() throws TeamEmptyException {
        if (this.workingTeam.isEmpty()) {
            throw new TeamEmptyException("Working team is empty");
        }
    }
}
