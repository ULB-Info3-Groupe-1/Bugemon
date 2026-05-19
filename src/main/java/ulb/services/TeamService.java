package ulb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import ulb.models.bugemon.Bugemon;
import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;
import ulb.models.team.factory.RandomTeamFactory;
import ulb.models.team.factory.TeamFactory;
import ulb.repositories.PlayerBugemonRepository;
import ulb.repositories.TeamRepository;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.TeamMemberDTO;
import ulb.repositories.exceptions.TeamEmptyException;
import ulb.repositories.exceptions.TeamNameAlreadyExistsException;
import ulb.repositories.exceptions.TeamNameEmptyException;
import ulb.repositories.exceptions.TeamNotFoundException;

/**
 * Service responsible for team persistence.
 */
public class TeamService {

    private final String playername;
    private final TeamRepository teamRepository;
    private final PlayerBugemonRepository playerBugemonRepository;

    /**
     * Constructor.
     *
     * @param teamRepository
     *            needed for team persistence
     * @param playerBugemonRepository
     *            needed for bugemon persistence
     * @param playername
     *            the player's name to use for database operations
     */
    public TeamService(TeamRepository teamRepository, PlayerBugemonRepository playerBugemonRepository,
            String playername) {
        this.playername = playername;
        this.teamRepository = teamRepository;
        this.playerBugemonRepository = playerBugemonRepository;
    }

    // --- Getters ---

    public List<Team> getPlayerTeams() {
        return this.teamRepository.loadTeams(this.playername);
    }

    public Optional<Team> getActiveTeam() {
        return this.teamRepository.loadCurrentTeam(this.playername);
    }

    public Optional<String> getActiveTeamName() {
        return this.getActiveTeam().map(Team::getName);
    }

    public List<String> getTeamNames() {
        return this.getPlayerTeams().stream().map(Team::getName).toList();
    }

    // --- Team Management ---

    /**
     * Checks if the working team of the player has been saved to the database. If the working team is empty, it is
     * considered as saved because there is nothing to save (we cannot save an empty team).
     *
     * @return (boolean) true if the working team has been saved, false otherwise
     */
    public boolean isTeamSaved(Team team) {
        if (team.isEmpty()) {
            return true;
        }
        return this.getPlayerTeams().stream().anyMatch(pt -> pt.equals(team));
    }

    /**
     * Deletes all teams of the player and the active team from the database.
     */
    public void clearTeamsAndActiveTeam() {
        this.teamRepository.unsetPlayerCurrentTeam(this.playername);
        this.teamRepository.clearTeams(this.playername);
    }

    /**
     * Sets the active team for the player.
     *
     * @param teamName
     *            the name of the team to set
     */
    public void setActiveTeam(String teamName) {
        this.teamRepository.setPlayerCurrentTeam(this.playername, teamName);
    }

    /**
     * Saves the team to the database. The team must have a name before calling this method.
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
    public void saveTeam(Team team) throws TeamNameAlreadyExistsException, TeamEmptyException, TeamNameEmptyException {
        this.checkTeamIsNotEmpty(team);
        this.teamRepository.createTeam(this.playername, team.getName());
        this.persistTeamMembers(team);
    }

    /**
     * Modifies the active team in the database. The team must have a name before calling this method.
     *
     * @throws TeamEmptyException
     *             if the active team is empty
     * @throws TeamNotFoundException
     *             if the team does not exist
     */
    public void modifyTeam(Team team) throws TeamEmptyException, TeamNotFoundException {
        this.checkTeamIsNotEmpty(team);
        this.persistNewBugemons(team);
        List<TeamMemberDTO> members = new ArrayList<>();
        List<PlayerBugemon> teamMembers = team.getMembers();
        for (int i = 0; i < teamMembers.size(); i++) {
            PlayerBugemon b = teamMembers.get(i);
            members.add(new TeamMemberDTO(this.playername, team.getName(), b.getName(), i));
        }
        this.teamRepository.modifyTeam(this.playername, team.getName(), members);
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
    public void renameActiveTeam(String oldName, String newName)
            throws TeamNotFoundException, TeamNameAlreadyExistsException, TeamNameEmptyException {
        if (oldName == null || oldName.isBlank() || newName == null || newName.isBlank()) {
            throw new TeamNameEmptyException("Old team name or new team name is empty.");
        }

        this.teamRepository.unsetPlayerCurrentTeam(this.playername); // Unset the old team because it's a foreign key
        this.teamRepository.renameTeam(this.playername, oldName, newName);
        this.teamRepository.setPlayerCurrentTeam(this.playername, newName);
    }

    /**
     * Deletes the given team (with teamName) from the database.
     *
     * @param teamName
     *            the name of the team to be deleted
     * @throws TeamNotFoundException
     *             if the team does not exist
     * @throws TeamNameEmptyException
     *             if the team name is empty
     */
    public void deleteTeam(String teamName) throws TeamNotFoundException, TeamNameEmptyException {
        this.teamRepository.unsetPlayerCurrentTeam(this.playername);
        this.teamRepository.deleteTeam(this.playername, teamName);
    }

    // --- Private Helpers ---

    private void persistTeamMembers(Team team) {
        this.persistNewBugemons(team);
        this.saveTeamMembers(team.getMembers(), team.getName());

    }

    private void persistNewBugemons(Team team) {
        Set<String> ownedBugemonNames = this.playerBugemonRepository.getPlayerBugemons(this.playername).stream()
                .map(PlayerBugemonDTO::bugemonName).collect(Collectors.toSet());
        for (PlayerBugemon b : team.getMembers()) {
            if (!ownedBugemonNames.contains(b.getName())) {
                this.playerBugemonRepository.savePlayerBugemon(this.toDTO(b));
            }
        }
    }

    private PlayerBugemonDTO toDTO(PlayerBugemon b) {
        return new PlayerBugemonDTO(this.playername, b.getName(), b.getDefense(), b.getAttack(), b.getInitiative(),
                b.getMaxHp(), b.getXp(), b.getLevel());
    }

    private void checkTeamIsNotEmpty(Team team) throws TeamEmptyException {
        if (team.isEmpty()) {
            throw new TeamEmptyException("Working team is empty");
        }
    }

    public TeamFactory createOpponentFactory(List<Bugemon> bugemons, Random random) {
        return new RandomTeamFactory(bugemons, random);
    }

    private void saveTeamMembers(List<PlayerBugemon> bugemons, String teamName) {
        for (PlayerBugemon b : bugemons) {
            this.teamRepository
                    .addTeamMember(new TeamMemberDTO(this.playername, teamName, b.getName(), bugemons.indexOf(b)));
        }
    }
}
