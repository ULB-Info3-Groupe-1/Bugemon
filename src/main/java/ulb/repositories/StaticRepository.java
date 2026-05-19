package ulb.repositories;

import java.util.List;
import java.util.Optional;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.repositories.dto.InventoryDTO;

public interface StaticRepository {

    List<Bugemon> findBugemons();

    Optional<Bugemon> findBugemonByName(String name);

    void saveBugemon(CreateBugemonDTO dto);

    List<Attack> findAttacks();

    InventoryDTO defaultInventory();
}
