package ulb.models.combat.factory;

import ulb.models.combat.Combat;
import ulb.models.trainer.Trainer;

/**
 * Creates a Combat instance for a given (player, opponent) pair.
 *
 * Implementations define end-of-combat rules (e.g. whether HP is restored).
 */
public interface CombatFactory {
    Combat create(Trainer playerTrainer, Trainer opponentTrainer);
}
