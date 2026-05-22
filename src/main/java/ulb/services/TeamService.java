package ulb.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import ulb.common.dto.persistence.TeamDTO;
import ulb.common.dto.persistence.TeamMemberDTO;
import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;
import ulb.repositories.BugemonRepository;
import ulb.repositories.TeamRepository;
import ulb.services.exceptions.TeamNameEmptyException;
import ulb.services.exceptions.TeamNotFoundException;

/**
 * Service responsible for team persistence and lifecycle management.
 *
 * <p>
 * Loads and saves {@link ulb.models.team.Team} instances for the current player, tracks the active team selection, and
 * exposes operations for creating, renaming, modifying, and deleting teams.
 */
public class TeamService {

    private final String playerName;
    private final TeamRepository teamRepository;
    private final BugemonRepository bugemonRepository;

    /**
     * Constructs a {@code TeamService} bound to the given player.
     *
     * @param teamRepository
     *            repository for team data
     * @param bugemonRepository
     *            repository used to resolve {@link ulb.models.player.PlayerBugemon} members when loading a team
     * @param playerName
     *            the name of the player whose teams this service manages
     */
    public TeamService(TeamRepository teamRepository, BugemonRepository bugemonRepository, String playerName) {
        this.playerName = playerName;
        this.teamRepository = teamRepository;
        this.bugemonRepository = bugemonRepository;
    }

    public Optional<Team> getTeam(String teamName) {
        return this.teamRepository.findByName(this.playerName, teamName).map(this::createTeam);
    }

    public List<String> getTeamNames() {
        return this.teamRepository.findAll(this.playerName).stream().map(TeamDTO::teamName).toList();
    }

    /**
     * Returns {@code true} if the repository contains a team whose name and members are identical to {@code team}.
     *
     * @param team
     *            the team to check
     * @return {@code true} if {@code team} is up-to-date in the repository
     */
    public boolean isTeamSaved(Team team) {
        if (team.getName() == null) {
            return false;
        }

        return this.teamRepository.findByName(this.playerName, team.getName())
                .map(teamDto -> teamDto.equals(this.teamToDTO(team))).orElse(false);
    }

    public boolean teamExists(String name) {
        return this.teamRepository.findByName(this.playerName, name).isPresent();
    }

    /**
     * Reconstructs a {@link ulb.models.team.Team} from a persisted {@link ulb.common.dto.persistence.TeamDTO}. Members
     * are sorted by their slot position and their {@link ulb.models.player.PlayerBugemon} data is fetched from the
     * repository.
     *
     * @param teamDTO
     *            the DTO to reconstruct from
     * @return the fully populated {@code Team}
     * @throws java.util.NoSuchElementException
     *             if a member's Bugemon data cannot be found
     */
    public Team createTeam(TeamDTO teamDTO) {
        List<PlayerBugemon> members = teamDTO.members().stream()
                .sorted(Comparator.comparingInt(TeamMemberDTO::slotPosition))
                .map(member -> this.bugemonRepository.findBase(member.bugemonName())
                        .flatMap(base -> this.bugemonRepository.findByName(this.playerName, member.bugemonName())
                                .map(dto -> PlayerBugemon.from(base, dto)))
                        .orElseThrow())
                .toList();
        Team team = new Team(members);
        team.setName(teamDTO.teamName());
        return team;
    }

    public Optional<Team> getActiveTeam() {
        return this.teamRepository.getActiveTeam(this.playerName).map(this::createTeam);
    }

    public void setActiveTeam(String teamName) {
        this.teamRepository.setActiveTeam(this.playerName, teamName);
    }

    public void unSetActiveTeam() {
        this.teamRepository.unSetActiveTeam(this.playerName);
    }

    /**
     * Deletes the named team and unsets the active team selection.
     *
     * @param teamName
     *            the name of the team to delete
     * @throws TeamNotFoundException
     *             if {@code teamName} is {@code null}, empty, or does not exist
     */
    public void deleteTeam(String teamName) throws TeamNotFoundException {
        if (teamName == null || teamName.isEmpty() || this.getTeam(teamName).isEmpty()) {
            throw new TeamNotFoundException("Team not found");
        }
        this.unSetActiveTeam();
        this.teamRepository.delete(this.playerName, teamName);
    }

    public void deleteTeams() {
        this.unSetActiveTeam();
        this.teamRepository.deleteAll(this.playerName);
    }

    /**
     * Renames {@code team} to {@code newName} by deleting the old record and saving under the new name.
     *
     * @param team
     *            the team to rename
     * @param newName
     *            the desired new name
     * @throws TeamNameEmptyException
     *             if {@code newName} is {@code null} or blank
     * @throws TeamNotFoundException
     *             if the team does not currently exist in the repository
     */
    public void renameTeam(Team team, String newName) throws TeamNameEmptyException, TeamNotFoundException {
        if (newName == null || newName.isEmpty()) {
            throw new TeamNameEmptyException("New name is empty");
        }

        this.deleteTeam(team.getName());
        team.setName(newName);
        this.save(team);
    }

    /**
     * Replaces the persisted team with the current in-memory state of {@code team}.
     *
     * @param team
     *            the team whose changes are to be persisted
     * @throws TeamNotFoundException
     *             if the team does not exist in the repository
     */
    public void modify(Team team) throws TeamNotFoundException {
        this.deleteTeam(team.getName());
        this.save(team);
    }

    public void save(Team team) {
        this.teamRepository.save(this.playerName, this.teamToDTO(team));
    }

    private TeamDTO teamToDTO(Team team) {
        List<TeamMemberDTO> members = team.getMembers().stream()
                .map(b -> new TeamMemberDTO(b.getName(), team.getMembers().indexOf(b))).toList();
        return new TeamDTO(this.playerName, team.getName(), members);
    }
}
