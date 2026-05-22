package ulb.repositories;

import java.util.List;
import java.util.Optional;

import ulb.common.dto.persistence.TeamDTO;

public interface TeamRepository {

    List<TeamDTO> findAll(String playerName);

    Optional<TeamDTO> findByName(String playerName, String teamName);

    Optional<TeamDTO> getActiveTeam(String playerName);

    void setActiveTeam(String playerName, String teamName);

    void unSetActiveTeam(String playerName);

    void save(String playerName, TeamDTO team);

    void delete(String playerName, String teamName);

    void deleteAll(String playerName);
}
