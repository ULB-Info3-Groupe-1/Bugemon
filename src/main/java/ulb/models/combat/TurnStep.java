package ulb.models.combat;

import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Efficiency;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.effect.Effect;
import ulb.models.trainer.Trainer;

public sealed interface TurnStep {

    record AttackStep(Trainer attacker, Attack attack, Efficiency efficiency) implements TurnStep {
        public String getAttackName() {
            return this.attack.name();
        }

        public List<Effect> getAttackEffects() {
            return this.attack.effects();
        }
    }

    record SwitchStep(Trainer trainer, Bugemon bugemon) implements TurnStep {
        public Bugemon getBugemon() {
            return this.bugemon;
        }
    }

    record ItemStep(Trainer trainer, Item item) implements TurnStep {
        public String getItemName() {
            return this.item.name();
        }

        public String getItemdescription() {
            return this.item.description();
        }

        public Effect getItemEffect() {
            return this.item.effect();
        }
    }

    record BugemonKoStep(Trainer trainer) implements TurnStep {
    }

    record TrainerKoStep(Trainer trainerKo) implements TurnStep {
    }

    record ForfeitStep(Trainer trainer) implements TurnStep {
    }
}
