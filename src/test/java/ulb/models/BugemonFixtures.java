package ulb.models;

import java.util.List;

import ulb.common.EffectDuration;
import ulb.common.EffectTarget;
import ulb.common.StatType;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;
import ulb.models.combat.CombatBugemon;
import ulb.models.combat.CombatTeam;
import ulb.models.effect.HealEffect;
import ulb.models.effect.ResetMalusEffect;
import ulb.models.effect.StatModifierEffect;
import ulb.models.player.PlayerBugemon;
import ulb.models.run.RunBugemon;

public final class BugemonFixtures {

    private BugemonFixtures() {
    }

    // --- Attaques génériques (type NORMAL, type non pertinent) ---

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

    // --- Attaques typées (type pertinent pour tester l'efficacité) ---

    public static Attack floraAttack() {
        return new Attack("atk-flora", "Fouet-Liane", "", 40, ElementType.FLORA, List.of());
    }

    public static Attack aquaAttack() {
        return new Attack("atk-aqua", "Jet d'Eau", "", 35, ElementType.AQUA, List.of());
    }

    // --- Bugémons génériques (type NORMAL, type non pertinent) ---

    public static Bugemon bugemon(int hp, int attack, int defense, int initiative, List<Attack> attacks) {
        return new Bugemon("Bugémon", hp, attack, defense, initiative, ElementType.NORMAL, attacks, "", false, false);
    }

    // --- Bugémons typés (type pertinent pour tester l'efficacité) ---

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
