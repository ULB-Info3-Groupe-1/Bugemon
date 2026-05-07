package ulb.models.combat.factory;

import ulb.models.combat.Combat;
import ulb.models.combat.Combat.EndOfCombatAction;
import ulb.models.combat.CombatXpDistributor;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;

/** Factory for standalone combats (outside of the Tower). */
public final class StandaloneCombatFactory implements CombatFactory {
    private final BugemonService bugemonService;

    public StandaloneCombatFactory(BugemonService bugemonService) {
        this.bugemonService = bugemonService;
    }

    @Override
    public Combat create(Trainer playerTrainer, Trainer opponentTrainer) {
        CombatXpDistributor combatxpDistributor = new CombatXpDistributor(this.bugemonService);
        return new Combat(combatxpDistributor, playerTrainer, opponentTrainer, EndOfCombatAction.RESTORE_HP);
    }
}
