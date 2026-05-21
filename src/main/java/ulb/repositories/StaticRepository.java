package ulb.repositories;

import java.util.List;
import java.util.Optional;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.skills.SkillTree;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.repositories.dto.DefaultInventoryDTO;

public interface StaticRepository {

    List<Bugemon> bugemons();

    Optional<Bugemon> findBugemonByName(String name);

    void saveBugemon(CreateBugemonDTO dto);

    List<Attack> attacks();

    DefaultInventoryDTO defaultInventory();

    SkillTree skillTree();
}
