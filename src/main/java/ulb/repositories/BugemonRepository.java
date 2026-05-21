package ulb.repositories;

import java.util.List;
import java.util.Optional;

import ulb.common.dto.persistence.PlayerBugemonDTO;
import ulb.models.bugemon.Bugemon;

public interface BugemonRepository {

    List<PlayerBugemonDTO> findAll(String playername);

    void removeAll(String playername);

    Optional<PlayerBugemonDTO> findByName(String playername, String bugemonName);

    Optional<Bugemon> findBase(String bugemonName);

    void save(PlayerBugemonDTO playerBugemon);

    void delete(String playername, String bugemonName);
}
