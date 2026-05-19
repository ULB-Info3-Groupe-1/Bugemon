package ulb.repositories;

import java.util.List;
import java.util.Optional;

import ulb.repositories.dto.TeamDTO;
import ulb.repositories.exceptions.TeamEmptyException;

public interface TeamRepository {

    public List<TeamDTO> findAll(String playerName);

    public void removeAll(String playerName);

    public Optional<TeamDTO> loadTeam(String playerName, String teamName);

    public void saveTeam(String playerName, TeamDTO team) throws TeamEmptyException;

    public void deleteTeam(String playerName, String teamName);
}
