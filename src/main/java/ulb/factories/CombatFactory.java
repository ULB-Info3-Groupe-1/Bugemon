package ulb.factories;

import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.combat.Combat;
import ulb.models.skills.Skill;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;
import ulb.services.InventoryService;

public class CombatFactory {

    private CombatFactory() {
        // Private constructor to prevent instantiation
    }

    public static Combat create(List<Bugemon> allBugemons, BugemonTeam playerTeam, List<Skill> skills, boolean isBoss) {
        // Later will handle the floor difficulty and boss ?
        AutoTrainer opponentTrainer = new AutoTrainer(
                isBoss ? TeamFactory.createRandomBossTeam(allBugemons, playerTeam.size())
                        : TeamFactory.createRandomTeam(allBugemons, playerTeam.size()));

        ManualTrainer playerTrainer = new ManualTrainer(playerTeam, InventoryService.getInstance().loadInventory());

        return new Combat(playerTrainer, opponentTrainer, skills);
    }

}
