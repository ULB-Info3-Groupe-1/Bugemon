package ulb.models.tower.utils;

import ulb.models.combat.Combat;
import ulb.models.combat.Combat.EndOfCombatAction;
import ulb.models.combat.CombatXpDistributor;
import ulb.models.combat.factory.CombatFactory;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;

/** Factory for Tower combats: does not restore HP at end of combat. */
public final class TowerCombatFactory implements CombatFactory {
    private final BugemonService bugemonService;

    public TowerCombatFactory(BugemonService bugemonService) {
        this.bugemonService = bugemonService;
    }

    @Override
    public Combat create(Trainer playerTrainer, Trainer opponentTrainer) {
        CombatXpDistributor combatxpDistributor = new CombatXpDistributor(this.bugemonService);
        return new Combat(combatxpDistributor, playerTrainer, opponentTrainer, EndOfCombatAction.NO_OP);
    }
}
