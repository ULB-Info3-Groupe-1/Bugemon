package ulb.repositories;

import java.util.List;
import java.util.Optional;

import ulb.models.bugemon.Bugemon;
import ulb.repositories.dto.PlayerBugemonDTO;

public interface BugemonRepository {

    List<PlayerBugemonDTO> findAll(String playername);

    void removeAll(String playername);

    Optional<PlayerBugemonDTO> findByName(String playername, String bugemonName);

    Optional<Bugemon> findBase(String bugemonName);

    void save(String playername, PlayerBugemonDTO playerBugemon);

    void delete(String playername, String bugemonName);

    void addAllPlayerBugemons(String playerName);
}
