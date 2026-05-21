package ulb.bootstrap;

import ulb.services.BugemonService;
import ulb.services.CombatService;
import ulb.services.InventoryService;
import ulb.services.LevelUpService;
import ulb.services.MusicService;
import ulb.services.SaveService;
import ulb.services.SkillService;
import ulb.services.TeamService;
import ulb.services.TowerService;

public class ServiceRegistry {
    public final BugemonService bugemon;
    public final TeamService team;
    public final InventoryService inventory;
    public final SkillService skill;
    public final TowerService tower;
    public final CombatService combat;
    public final SaveService save;
    public final LevelUpService levelUp;
    public final MusicService music;

    public ServiceRegistry(BugemonService bugemon, TeamService team, InventoryService inventory, SkillService skill,
            TowerService tower, CombatService combat, SaveService save, LevelUpService levelUp, MusicService music) {
        this.bugemon = bugemon;
        this.team = team;
        this.inventory = inventory;
        this.skill = skill;
        this.tower = tower;
        this.combat = combat;
        this.save = save;
        this.levelUp = levelUp;
        this.music = music;
    }
}
