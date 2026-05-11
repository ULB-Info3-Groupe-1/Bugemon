package ulb.services;

import java.util.List;

import ulb.models.bugemon.effect.EffectDuration;
import ulb.models.bugemon.effect.EffectStatModifier;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.models.combat.Combat;
import ulb.models.combat.CombatDamageCalculator;
import ulb.models.skills.Skill;
import ulb.models.skills.SkillEffect.StatBonusEffect;
import ulb.models.trainer.Trainer;

/**
 * Stateless utility for combat calculations: attack priority, damage formula.
 */
public class CombatService {
    /**
     * Creates a combat.
     *
     * At the end of the combat, HPs are restored and XP is distributed.
     */
    public Combat createUniqueCombat(List<Skill> skills, Trainer playerTrainer, Trainer opponentTrainer) {
        this.applyStatBonusSkills(skills, playerTrainer);
        CombatDamageCalculator combatDamageCalculator = new CombatDamageCalculator(skills);
        return new Combat(playerTrainer, opponentTrainer, combatDamageCalculator);
    }

    /**
     * Applies every unlocked {@link StatBonusEffect} skill as a permanent {@link EffectStatModifier} on each Bugemon of
     * the player's team — these bonuses are re-applied at the start of every combat.
     */
    private void applyStatBonusSkills(List<Skill> skills, Trainer playerTrainer) {
        for (Skill skill : skills) {
            StatBonusEffect bonus = (StatBonusEffect) skill.getEffect();
            EffectStatModifier modifier = new EffectStatModifier(EffectTarget.TEAM, bonus.stat(), bonus.bonus(),
                    EffectDuration.PERMANENT);
            playerTrainer.applyEffectToCurrentTeam(modifier);
        }
    }

    /**
     * Determines which trainer's Bugemon attacks first based on initiative. In case of a tie, the winner is chosen
     * randomly.
     *
     * @param trainer1
     *            the first trainer
     * @param trainer2
     *            the second trainer
     * @return the trainer whose Bugemon attacks first
     */
    public static Trainer attackPriority(final Trainer trainer1, final Trainer trainer2) {
        final int initiative1 = trainer1.getCurrentBugemonInitiative();
        final int initiative2 = trainer2.getCurrentBugemonInitiative();

        if (initiative1 < initiative2) {
            return trainer2;
        } else if (initiative1 > initiative2) {
            return trainer1;
        } else {
            return Math.random() <= 0.5 ? trainer1 : trainer2;
        }
    }
}
