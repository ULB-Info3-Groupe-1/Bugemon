package ulb.models;

import java.util.List;

import ulb.common.EffectTarget;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;
import ulb.models.combat.CombatBugemon;
import ulb.models.combat.CombatTeam;
import ulb.models.effect.HealEffect;
import ulb.models.player.PlayerBugemon;
import ulb.models.run.RunBugemon;

public final class BugemonFixtures {

    private BugemonFixtures() {
    }

    public static Attack floraAttack() {
        return new Attack("atk-flora", "Fouet-Liane", "", 40, ElementType.FLORA, List.of());
    }

    public static Attack zeroPowerAttack() {
        return new Attack("atk-flora", "Fouet-Liane", "", 0, ElementType.FLORA, List.of());
    }

    public static Attack floraAttackWithThrowerHeal() {
        return new Attack("atk-flora", "Fouet-Liane", "", 1, ElementType.FLORA, List.of(throwerHpHealEffect()));
    }

    public static HealEffect throwerHpHealEffect() {
        return new HealEffect(EffectTarget.THROWER, 10);
    }

    public static Attack aquaAttack() {
        return new Attack("atk-aqua", "Jet d'Eau", "", 35, ElementType.AQUA, List.of());
    }

    public static Bugemon fastFlora() {
        return new Bugemon("flora-1", "FloraFast", 100, 50, 40, 70, ElementType.FLORA,
                List.of(floraAttack(), floraAttack(), floraAttack()), "", false);
    }

    public static Bugemon slowAqua() {
        return new Bugemon("aqua-1", "AquaSlow", 100, 50, 40, 30, ElementType.AQUA,
                List.of(aquaAttack(), aquaAttack(), aquaAttack()), "", false);
    }

    public static CombatTeam teamOf(Bugemon bugemon) {
        return new CombatTeam(List.of(new CombatBugemon(new RunBugemon(new PlayerBugemon(bugemon)))));
    }
}
