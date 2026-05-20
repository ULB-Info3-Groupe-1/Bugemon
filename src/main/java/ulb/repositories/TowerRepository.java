package ulb.repositories;

import ulb.repositories.dto.TowerDTO;

public interface TowerRepository {
    
    void save(String playerName, TowerDTO towerDTO);

    void delete(String playerName);

    TowerDTO find(String playerName);
}
