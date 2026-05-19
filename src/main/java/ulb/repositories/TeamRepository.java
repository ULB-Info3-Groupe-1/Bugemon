package ulb.repositories;

import java.util.List;
import java.util.Optional;

import ulb.repositories.dto.TeamDTO;

public interface TeamRepository {

    List<TeamDTO> findAll(String playerName);

    Optional<TeamDTO> findByName(String playerName, String teamName);

    void save(String playerName, TeamDTO team);

    void delete(String playerName, String teamName);

    void deleteAll(String playerName);

    Optional<String> getCurrentTeamName(String playername);

    void setCurrentTeamName(String playername, String teamName);

    void resetCurrentTeamName(String playername);
}
