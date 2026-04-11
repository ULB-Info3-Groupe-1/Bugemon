package ulb.models.combat;

import ulb.models.trainer.Trainer;

public record CombatContext(Trainer winner, Trainer loser) {
}
