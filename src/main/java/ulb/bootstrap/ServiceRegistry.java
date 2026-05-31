package ulb.bootstrap;

import ulb.services.game.BugemonService;
import ulb.services.game.InventoryService;
import ulb.services.game.LevelUpService;
import ulb.services.game.RewardService;
import ulb.services.game.SaveService;
import ulb.services.game.SkillService;
import ulb.services.game.TeamService;
import ulb.services.game.TowerService;

/**
 * Immutable value object that groups every application service into a single, conveniently accessible container.
 *
 * <p>
 * Controllers receive a {@code ServiceRegistry} instance from {@link GameBootstrapper} and access the individual
 * services through its public final fields. All fields are set once in the constructor and never reassigned.
 */
public class ServiceRegistry {
    public final BugemonService bugemon;
    public final TeamService team;
    public final InventoryService inventory;
    public final SkillService skill;
    public final TowerService tower;
    public final SaveService save;
    public final LevelUpService levelUp;
    public final RewardService reward;

    @SuppressWarnings("checkstyle:ParameterNumber")
    public ServiceRegistry(BugemonService bugemon, TeamService team, InventoryService inventory, SkillService skill,
            TowerService tower, SaveService save, LevelUpService levelUp, RewardService reward) {
        this.bugemon = bugemon;
        this.team = team;
        this.inventory = inventory;
        this.skill = skill;
        this.tower = tower;
        this.save = save;
        this.levelUp = levelUp;
        this.reward = reward;
    }
}
