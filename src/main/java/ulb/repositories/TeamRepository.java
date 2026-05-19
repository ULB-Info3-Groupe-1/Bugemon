package ulb.repositories;

import java.util.List;
import java.util.Optional;

import ulb.repositories.dto.TeamDTO;

public interface TeamRepository {

    public List<TeamDTO> findAll(String playerName);

    public Optional<TeamDTO> findByName(String playerName, String teamName);

    public void save(String playerName, TeamDTO team);

    public void delete(String playerName, String teamName);

    public void removeAll(String playerName);
}
