package ulb.models.tower.utils;

import ulb.models.combat.Combat;
import ulb.models.combat.Combat.EndOfCombatAction;
import ulb.models.combat.CombatXpDistributor;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;
import ulb.services.CombatService;
import ulb.models.bugemon.Inventory;
import ulb.models.trainer.MiniMax;

public class CombatFactory {
    private final BugemonService bugemonService;

    public CombatFactory(BugemonService bugemonService) {
        this.bugemonService = bugemonService;
    }

    public Combat create(Trainer playerTrainer, int floorNumber, boolean isBoss) {
        // Later will handle the floor difficulty
        AutoTrainer opponentTrainer = new AutoTrainer(CombatService
                .createRandomTeam(this.bugemonService.getAllDefaultBugemons(), playerTrainer.getTeamSize()), new MiniMax(3), new Inventory());

        CombatXpDistributor combatxpDistributor = new CombatXpDistributor(this.bugemonService);
        return new Combat(combatxpDistributor, playerTrainer, opponentTrainer, EndOfCombatAction.RESTORE_HP);
    }
}
