package bugemon.server.bootstrap;

import bugemon.server.repositories.BugemonRepository;
import bugemon.server.repositories.InventoryRepository;
import bugemon.server.repositories.PlayerRepository;
import bugemon.server.repositories.SkillRepository;
import bugemon.server.repositories.StaticRepository;
import bugemon.server.repositories.TeamRepository;
import bugemon.server.repositories.TowerRepository;

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
