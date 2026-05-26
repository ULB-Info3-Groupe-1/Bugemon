package ulb.bootstrap;

import ulb.repositories.BugemonRepository;
import ulb.repositories.InventoryRepository;
import ulb.repositories.PlayerRepository;
import ulb.repositories.SkillRepository;
import ulb.repositories.StaticRepository;
import ulb.repositories.TeamRepository;
import ulb.repositories.TowerRepository;

public class RepositoryRegistry {

    public final StaticRepository staticDataRepository;
    public final InventoryRepository inventoryRepository;
    public final PlayerRepository playerRepository;
    public final SkillRepository skillRepository;
    public final BugemonRepository bugemonRepository;
    public final TeamRepository teamRepository;
    public final TowerRepository towerRepository;

    public RepositoryRegistry(StaticRepository staticDataRepository, InventoryRepository inventoryRepository,
            PlayerRepository playerRepository, SkillRepository skillRepository, BugemonRepository bugemonRepository,
            TeamRepository teamRepository, TowerRepository towerRepository) {
        this.staticDataRepository = staticDataRepository;
        this.inventoryRepository = inventoryRepository;
        this.playerRepository = playerRepository;
        this.skillRepository = skillRepository;
        this.bugemonRepository = bugemonRepository;
        this.teamRepository = teamRepository;
        this.towerRepository = towerRepository;
    }
}
