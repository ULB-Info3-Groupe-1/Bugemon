package ulb.repositories;

import java.util.Optional;

import ulb.common.dto.persistence.TowerDTO;

public interface TowerRepository {

    void save(String playerName, TowerDTO towerDTO);

    void delete(String playerName);

    Optional<TowerDTO> find(String playerName);
}
