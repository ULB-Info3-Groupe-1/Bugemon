package ulb.repositories;

import java.util.List;

import ulb.repositories.dto.TeamDTO;
import ulb.repositories.exceptions.TeamEmptyException;

public interface TeamRepository {

    public List<TeamDTO> findAll(String playerName);

    public TeamDTO loadTeam(String playerName, String teamName);

    public void saveTeam(String playerName, TeamDTO team) throws TeamEmptyException;

    public void deleteTeam(String playerName, String teamName);
}
