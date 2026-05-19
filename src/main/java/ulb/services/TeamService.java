package ulb.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import ulb.factories.BugemonFactory;
import ulb.models.bugemon.Bugemon;
import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;
import ulb.models.team.factory.RandomTeamFactory;
import ulb.models.team.factory.TeamFactory;
import ulb.repositories.BugemonRepository;
import ulb.repositories.TeamRepository;
import ulb.repositories.dto.TeamDTO;
import ulb.repositories.dto.TeamMemberDTO;
import ulb.services.exceptions.TeamEmptyException;
import ulb.services.exceptions.TeamNameEmptyException;

/**
 * Service responsible for team persistence.
 */
public class TeamService {

    private final String playername;
    private final TeamRepository teamRepository;
    private final BugemonRepository bugemonRepository;

    public TeamService(TeamRepository teamRepository, BugemonRepository bugemonRepository, String playername) {
        this.playername = playername;
        this.teamRepository = teamRepository;
        this.bugemonRepository = bugemonRepository;
    }

    public List<String> getTeamNames() {
        return this.teamRepository.findAll(this.playername).stream().map(TeamDTO::teamName).toList();
    }

    public Team createTeam(TeamDTO teamDTO) {
        List<PlayerBugemon> members = teamDTO.members().stream()
                .sorted(Comparator.comparingInt(TeamMemberDTO::slotPosition))
                .map(member -> this.bugemonRepository.findBase(member.bugemonName())
                        .flatMap(base -> this.bugemonRepository.findByName(this.playername, member.bugemonName())
                                .map(dto -> BugemonFactory.createPlayerBugemon(base, dto)))
                        .orElseThrow())
                .toList();
        Team team = new Team(members);
        team.setName(teamDTO.teamName());
        return team;
    }

    public boolean isTeamSaved(Team team) {
        if (team.getName() == null || team.getName().isEmpty()) {
            return false;
        }
        return this.teamRepository.findByName(this.playername, team.getName())
                .map(teamDTO -> this.teamToDTO(this.playername, team).equals(teamDTO)).orElse(false);
    }

    public void deleteTeam(String teamName) throws TeamNameEmptyException {
        if (teamName.isEmpty()) {
            throw new TeamNameEmptyException(teamName);
        }
        this.teamRepository.delete(this.playername, teamName);
    }

    public void setActiveTeam(String teamName) {
        this.teamRepository.setCurrentTeamName(this.playername, teamName);
    }

    public void resetActiveTeam() {
        this.teamRepository.resetCurrentTeamName(this.playername);
    }

    public void deleteTeams() {
        this.teamRepository.deleteAll(this.playername);
    }

    public void save(Team team) throws TeamEmptyException, TeamNameEmptyException {
        if (team.isEmpty()) {
            throw new TeamEmptyException("Cannot save an empty team!");
        } else if (team.getName().isEmpty()) {
            throw new TeamNameEmptyException("Cannot save a team without a name!");
        }
        this.teamRepository.save(this.playername, this.teamToDTO(this.playername, team));
    }

    public Optional<String> getActiveTeamName() {
        return this.teamRepository.getCurrentTeamName(this.playername);
    }

    public Optional<Team> getActiveTeam() {
        return this.teamRepository.getCurrentTeamName(this.playername)
                .flatMap(teamName -> this.teamRepository.findByName(this.playername, teamName)).map(this::createTeam);
    }

    public TeamFactory createOpponentFactory(List<Bugemon> bugemons, Random random) {
        return new RandomTeamFactory(bugemons, random);
    }

    private TeamDTO teamToDTO(String playerName, Team team) {
        List<TeamMemberDTO> members = team.getMembers().stream()
                .map(b -> new TeamMemberDTO(b.getName(), team.getMembers().indexOf(b))).toList();
        return new TeamDTO(playerName, team.getName(), members);
    }
}
