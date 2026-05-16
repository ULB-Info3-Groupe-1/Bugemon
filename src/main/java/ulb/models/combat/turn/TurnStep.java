package ulb.models.combat.turn;

import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.combat.CombatBugemon;
import ulb.models.combat.CombatTeam;
import ulb.models.effect.Effect;

public sealed interface TurnStep {

    record AttackStep(CombatBugemon attacker, CombatBugemon defender, Attack attack, int damage,
            int defenderHpAfter) implements TurnStep {
        public String getAttackName() {
            return this.attack.name();
        }

        public List<Effect> getAttackEffects() {
            return this.attack.effects();
        }
    }

    record KoStep(CombatBugemon koBugemon) implements TurnStep {
    }

    record SwitchStep(CombatBugemon bugemon) implements TurnStep {
    }

    // Placeholder for future item steps
    record ItemStep() implements TurnStep {
    }

    record HealBugemonStep(CombatBugemon healedBugemon) implements TurnStep {}

    record HealTeamStep(CombatTeam healedTeam) implements TurnStep {}
}
