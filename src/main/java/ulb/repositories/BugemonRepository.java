package ulb.repositories;

import java.util.List;
import java.util.Optional;

import ulb.common.dto.persistence.PlayerBugemonDTO;
import ulb.models.bugemon.Bugemon;

public interface BugemonRepository {

    List<PlayerBugemonDTO> findAll(String playerName);

    void removeAll(String playerName);

    Optional<PlayerBugemonDTO> findByName(String playerName, String bugemonName);

    Optional<Bugemon> findBase(String bugemonName);

    void save(PlayerBugemonDTO playerBugemon);

    void delete(String playerName, String bugemonName);
}
