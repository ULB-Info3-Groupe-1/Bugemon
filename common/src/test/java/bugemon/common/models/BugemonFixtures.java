package bugemon.common.models;

import java.util.List;

import bugemon.common.EffectDuration;
import bugemon.common.EffectTarget;
import bugemon.common.StatType;
import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.bugemon.Bugemon;
import bugemon.common.models.bugemon.ElementType;
import bugemon.common.models.combat.CombatBugemon;
import bugemon.common.models.combat.CombatTeam;
import bugemon.common.models.effect.HealEffect;
import bugemon.common.models.effect.ResetMalusEffect;
import bugemon.common.models.effect.StatModifierEffect;
import bugemon.common.models.player.PlayerBugemon;
import bugemon.common.models.run.RunBugemon;

public final class BugemonFixtures {

    private BugemonFixtures() {
    }

    // --- Generic attacks ---

    public static Attack attack(String id, int power) {
        return new Attack(id, "Attaque", "", power, ElementType.NORMAL, List.of());
    }

    public static Attack zeroPowerAttack() {
        return attack("atk-zero", 0);
    }

    public static Attack attackWithThrowerHeal() {
        return new Attack("atk-heal", "Soin", "", 1, ElementType.NORMAL, List.of(throwerHpHealEffect()));
    }

    public static HealEffect throwerHpHealEffect() {
        return new HealEffect(EffectTarget.THROWER, 10);
    }

    public static Attack attackWithDefenseDebuffOnOpponent() {
        return new Attack("atk-debuff", "Morsure", "", 1, ElementType.NORMAL, List
                .of(new StatModifierEffect(EffectTarget.OPPONENT, StatType.DEFENSE, -10, EffectDuration.PERMANENT)));
    }

    public static Attack attackWithInitiativeBuffOnThrower() {
        return new Attack("atk-buf", "Élan", "", 1, ElementType.NORMAL, List
                .of(new StatModifierEffect(EffectTarget.THROWER, StatType.INITIATIVE, 15, EffectDuration.PERMANENT)));
    }

    public static Attack attackWithResetMalus() {
        return new Attack("atk-reset", "Purification", "", 1, ElementType.NORMAL,
                List.of(new ResetMalusEffect(EffectTarget.THROWER)));
    }

    // --- Typed attacks (type pertinent for testing effectiveness) ---

    public static Attack floraAttack() {
        return new Attack("atk-flora", "Fouet-Liane", "", 40, ElementType.FLORA, List.of());
    }

    public static Attack aquaAttack() {
        return new Attack("atk-aqua", "Jet d'Eau", "", 35, ElementType.AQUA, List.of());
    }

    // --- Generic Bugémons ---

    public static Bugemon bugemon(int hp, int attack, int defense, int initiative, List<Attack> attacks) {
        return new Bugemon("Bugémon", hp, attack, defense, initiative, ElementType.NORMAL, attacks, "", false, false);
    }

    // --- Typed Bugémons (type pertinent for testing effectiveness) ---

    public static Bugemon fastFlora() {
        return new Bugemon("FloraFast", 100, 50, 40, 70, ElementType.FLORA,
                List.of(floraAttack(), floraAttack(), floraAttack()), "", false, false);
    }

    public static Bugemon slowAqua() {
        return new Bugemon("AquaSlow", 100, 50, 40, 30, ElementType.AQUA,
                List.of(aquaAttack(), aquaAttack(), aquaAttack()), "", false, false);
    }

    // --- Helpers ---

    public static CombatTeam teamOf(Bugemon bugemon) {
        return new CombatTeam(List.of(new CombatBugemon(new RunBugemon(new PlayerBugemon(bugemon)))));
    }
}
