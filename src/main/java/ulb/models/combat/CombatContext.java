package ulb.models.combat;

import ulb.models.trainer.Trainer;

/** Carries the outcome of a completed combat to post-combat actions such as XP distribution. */
public record CombatContext(Trainer winner, Trainer loser) {
}
