package ulb.repositories;

import java.util.List;
import java.util.Optional;

import ulb.repositories.dto.PlayerBugemonDTO;

public interface BugemonRepository {

    public List<PlayerBugemonDTO> findAll(String playername);

    public void removeAll(String playername);

    public Optional<PlayerBugemonDTO> findByName(String playername, String bugemonName);

    public void save(String playername, PlayerBugemonDTO playerBugemon);

    public void delete(String playername, String bugemonName);
}
