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
 * Service responsible for team persistence.
 */
public class TeamService {

    private final String playerName;
    private final TeamRepository teamRepository;
    private final BugemonRepository bugemonRepository;

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

    public void renameTeam(Team team, String newName) throws TeamNameEmptyException, TeamNotFoundException {
        if (newName == null || newName.isEmpty()) {
            throw new TeamNameEmptyException("New name is empty");
        }

        this.deleteTeam(team.getName());
        team.setName(newName);
        this.save(team);
    }

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
