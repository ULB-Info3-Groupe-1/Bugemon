package ulb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;
import ulb.repositories.PlayerBugemonRepository;
import ulb.repositories.PlayerRepository;
import ulb.repositories.TeamRepository;
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

    private final TeamRepository teamRepository;

    private final PlayerBugemonRepository playerBugemonRepository;

    /**
     * The team that the player is currently modifying. It is used to keep track of the changes made to the team before
     * saving it to the database.
     */
    private final Team workingTeam;

    /**
     * The team that the player is currently using.
     */
    private Team activeTeam;

    /**
     * The list of teams that the player has.
     */
    private List<Team> playerTeams;

    /**
     * Constructor for the TeamService.
     *
     * @param playerRepository
     *            the repository used to interact with the database
     * @param playername
     *            the name of the player to use to interact with the database
     */
    public TeamService(PlayerRepository playerRepository, TeamRepository teamRepository,
            PlayerBugemonRepository playerBugemonRepository, String playername) {
        this.playername = playername;
        this.workingTeam = new Team();
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.playerBugemonRepository = playerBugemonRepository;
        this.playerTeams = new ArrayList<>();

        try {
            this.playerRepository.createPlayer(this.playername);
        } catch (PlayernameAlreadyExistsException e) {
            // We do nothing because whitout client/server architecture, the database is
            // local and we don't have a login
            // system, so the playername used is 'default_player' and is always the same.
        }
    }

    // --- Getters ---

    /**
     * Retrieves the active team.
     *
     * @return the active team of the player
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     */
    public Team getRequiredActiveTeam() throws NoActiveTeamException {
        this.checkActiveTeamIsPresent();
        return this.activeTeam;
    }

    public Optional<String> getActiveTeamName() {
        return Optional.ofNullable(this.activeTeam).map(Team::getName);
    }

    public Team getWorkingTeam() {
        return this.workingTeam;
    }

    public List<String> getTeamNames() {
        return this.playerTeams.stream().map(Team::getName).toList();
    }

    // --- Team Management ---

    /**
     * Loads the teams of the player from the database and sets the active team. If the player does not have an active
     * team, the active team is set to empty. It also clears the local list of teams before loading them from the
     * database to avoid duplicates in case this method is called multiple times.
     */
    public void loadTeamsAndActiveTeam() {
        this.playerTeams.clear();
        this.playerTeams.addAll(this.teamRepository.loadTeams(this.playername));
        this.teamRepository.loadCurrentTeam(this.playername).ifPresent(t -> this.activeTeam = new Team(t));
    }

    /**
     * Deletes all teams of the player from the database and clears the active team.
     */
    public void clearTeamsAndActiveTeam() {
        this.clearActiveTeam();
        this.teamRepository.clearTeams(this.playername);
        this.playerTeams.clear();
    }

    /**
     * Sets the active team for the player.
     *
     * @param teamName
     *            the name of the team to set
     * @throws TeamNotFoundException
     *             if the team does not exist
     */
    public void setActiveTeam(String teamName) throws TeamNotFoundException {
        Team team = this.playerTeams.stream().filter(t -> t.getName().equals(teamName)).findFirst()
                .orElseThrow(() -> new TeamNotFoundException("Team not found: " + teamName));
        this.activeTeam = new Team(team);
        this.teamRepository.setPlayerCurrentTeam(this.playername, teamName);
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
        this.teamRepository.createTeam(this.playername, teamName);
        this.persistTeamMembers();
        this.playerTeams.add(new Team(this.workingTeam));
        // Clear because the working team is saved so by clearing it we can create a new
        // team
        this.workingTeam.clear();
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
        this.workingTeam.setName(this.activeTeam.getName());
        this.activeTeam = new Team(this.workingTeam);

        List<TeamMemberDTO> members = new ArrayList<>();
        this.workingTeam.getMembers().forEach(b -> members.add(new TeamMemberDTO(this.playername, this.workingTeam.getName(),
                b.getName(), this.workingTeam.getSlotPosition(b))));
        this.persistNewBugemons();

        this.teamRepository.modifyTeam(this.playername, this.workingTeam.getName(), members);
        this.playerTeams.removeIf(t -> t.getName().equals(this.workingTeam.getName()));
        this.playerTeams.add(new Team(this.workingTeam));
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
        this.teamRepository.unsetPlayerCurrentTeam(this.playername); // Unset the old team because it's a foreign key
        this.teamRepository.renameTeam(this.playername, this.activeTeam.getName(), newName);
        this.teamRepository.setPlayerCurrentTeam(this.playername, newName);
        this.playerTeams.stream().filter(t -> t.getName().equals(this.activeTeam.getName()))
                .forEach(t -> t.setName(newName));
        this.workingTeam.setName(newName);
        this.activeTeam.setName(newName);
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
        this.playerTeams.removeIf(t -> t.equals(this.activeTeam));
        this.teamRepository.unsetPlayerCurrentTeam(this.playername);
        this.teamRepository.deleteTeam(this.playername, teamName);
        this.workingTeam.clear();
        this.activeTeam = null;

    }

    public boolean isActiveTeamEmpty() {
        return this.activeTeam == null || this.activeTeam.isEmpty();
    }

    public void clearActiveTeam() {
        this.activeTeam = null;
        this.teamRepository.unsetPlayerCurrentTeam(this.playername);
    }

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
     * Adds a bugemon to the working team if it is not already in the team, otherwise removes it from the team.
     *
     * @param bugemon
     *            the bugemon to be added or removed from the working team
     */
    public void addOrRemoveBugemon(PlayerBugemon bugemon) {
        if (this.workingTeam.contains(bugemon)) {
            this.workingTeam.remove(bugemon);
        } else if (!this.workingTeam.isFull()) {
            this.workingTeam.add(bugemon);
        }
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
        this.activeTeam.getMembers().forEach(this::updateBugemonInDb);
        this.updateLocalTeams();
    }

    private void updateBugemonInDb(PlayerBugemon b) {
        this.playerBugemonRepository.updatePlayerBugemon(this.toDTO(b));
    }

    private PlayerBugemonDTO toDTO(PlayerBugemon b) {
        return new PlayerBugemonDTO(this.playername, b.getName(), b.getDefense(), b.getAttack(), b.getInitiative(),
                b.getMaxHp(), b.getXp(), b.getLevel());
    }

    // -- Public Helpers --

    /**
     * Sets the working team as the active team (by copying the members). If the player does not have an active team,
     * nothing is done.
     */
    public void setWorkingTeamAsActiveTeam() {
        if (this.activeTeam != null) {
            this.workingTeam.clear();
            this.workingTeam.addAll(this.activeTeam.getMembers());
            this.workingTeam.setName(this.activeTeam.getName());
        }
    }

    // --- Private Helpers ---

    /**
     * Saves the members of the working team to the database. If a bugemon of the working team is not owned by the
     * player, it is first saved to the database before being added as a team member.
     */
    private void persistTeamMembers() {
        this.persistNewBugemons();
        for (PlayerBugemon b : this.workingTeam.getMembers()) {
            this.teamRepository.addTeamMember(new TeamMemberDTO(this.playername, this.workingTeam.getName(),
                    b.getName(), this.workingTeam.getSlotPosition(b)));
        }
    }

    private void persistNewBugemons() {
        Set<String> ownedBugemonNames = this.playerBugemonRepository.getPlayerBugemons(this.playername).stream()
                .map(PlayerBugemonDTO::bugemonName).collect(Collectors.toSet());
        for (PlayerBugemon b : this.workingTeam.getMembers()) {
            if (!ownedBugemonNames.contains(b.getName())) {
                this.playerBugemonRepository.savePlayerBugemon(this.toDTO(b));
            }
        }
    }

    private void updateLocalTeams() throws NoActiveTeamException {
        this.checkActiveTeamIsPresent();
        this.playerTeams.removeIf(t -> t.getName().equals(this.activeTeam.getName()));
        this.playerTeams.add(new Team(this.activeTeam));
    }

    private void checkActiveTeamIsPresent() throws NoActiveTeamException {
        if (this.activeTeam == null) {
            throw new NoActiveTeamException("Player doesn't have an active Team");
        }
    }

    private void checkWorkingTeamIsNotEmpty() throws TeamEmptyException {
        if (this.workingTeam.isEmpty()) {
            throw new TeamEmptyException("Working team is empty");
        }
    }
}
