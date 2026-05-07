package ulb.models.tower.utils;

import ulb.models.combat.Combat;
import ulb.models.combat.Combat.EndOfCombatAction;
import ulb.models.combat.CombatXpDistributor;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;
import ulb.services.CombatService;

public class CombatFactory {
    private final BugemonService bugemonService;

    public CombatFactory(BugemonService bugemonService) {
        this.bugemonService = bugemonService;
    }

    public Combat create(Trainer playerTrainer, boolean isBoss) {
        // Later will handle the floor difficulty and boss ?
        AutoTrainer opponentTrainer = new AutoTrainer(isBoss
                ? CombatService.createRandomBossTeam(this.bugemonService.getAllDefaultBugemons(),
                        playerTrainer.getTeamSize())
                : CombatService.createRandomTeam(this.bugemonService.getAllDefaultBugemons(),
                        playerTrainer.getTeamSize()));
        CombatXpDistributor combatxpDistributor = new CombatXpDistributor(this.bugemonService);
        return new Combat(combatxpDistributor, playerTrainer, opponentTrainer, EndOfCombatAction.RESTORE_HP);
    }
}
