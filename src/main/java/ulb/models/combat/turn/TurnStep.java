package ulb.models.combat.turn;

import java.util.List;

import ulb.common.DamageResult;
import ulb.models.bugemon.Attack;
import ulb.models.combat.CombatBugemon;
import ulb.models.combat.CombatTeam;
import ulb.models.effect.Effect;
import ulb.models.item.Item;

/**
 * Represents a single observable event that occurred during turn resolution.
 *
 * <p>
 * A resolved turn is expressed as an ordered list of {@code TurnStep} instances
 * that the view layer animates
 * sequentially. The sealed hierarchy covers all event types the combat engine
 * currently produces:
 * <ul>
 * <li>{@link AttackStep} — an attack was used and damage was dealt.</li>
 * <li>{@link KoStep} — a Bugemon was knocked out.</li>
 * <li>{@link SwitchStep} — a Bugemon was switched in.</li>
 * <li>{@link ItemStep} — placeholder for a future item-use event.</li>
 * <li>{@link HealBugemonStep} — a single Bugemon was healed.</li>
 * <li>{@link HealTeamStep} — an entire team was healed.</li>
 * </ul>
 */
public sealed interface TurnStep {

    /**
     * Records that an attacker used an attack against a defender, including the
     * calculated damage and the defender's HP
     * after the hit.
     *
     * @param attacker
     *                        the Bugemon that performed the attack
     * @param defender
     *                        the Bugemon that was hit
     * @param attack
     *                        the attack that was used
     * @param damageResult
     *                        detailed breakdown of the damage calculation
     * @param defenderHpAfter
     *                        the defender's remaining HP after damage is applied
     */
    record AttackStep(CombatBugemon attacker, CombatBugemon defender, Attack attack, DamageResult damageResult,
            int defenderHpAfter) implements TurnStep {

        /**
         * Returns the display name of the attack used in this step.
         *
         * @return the attack name; never {@code null}
         */
        public String getAttackName() {
            return this.attack.name();
        }

        /**
         * Returns the list of secondary effects associated with the attack used in this
         * step.
         *
         * @return the attack's effect list; never {@code null}, may be empty
         */
        public List<Effect> getAttackEffects() {
            return this.attack.effects();
        }
    }

    /**
     * Records that a Bugemon was knocked out.
     *
     * @param koBugemon
     *                  the Bugemon that fainted
     */
    record KoStep(CombatBugemon koBugemon) implements TurnStep {
    }

    /**
     * Records that a Bugemon was switched in as the active combatant.
     *
     * <p>
     * The convenience constructor captures the Bugemon's current HP at the moment
     * of the switch so the view can display
     * it without querying live state later.
     *
     * @param bugemon
     *                   the Bugemon that entered the battle
     * @param hpAtSwitch
     *                   the Bugemon's HP at the moment it was switched in
     * @param isPlayer
     *                   {@code true} if the Bugemon belongs to the player's team
     */
    record SwitchStep(CombatBugemon bugemon, int hpAtSwitch, boolean isPlayer) implements TurnStep {

        /**
         * Creates a {@code SwitchStep} that captures the Bugemon's current HP
         * automatically.
         *
         * @param bugemon
         *                 the Bugemon being switched in
         * @param isPlayer
         *                 {@code true} if the Bugemon belongs to the player's team
         */
        public SwitchStep(CombatBugemon bugemon, boolean isPlayer) {
            this(bugemon, bugemon.getCurrentHp(), isPlayer);
        }
    }

    /**
     * Placeholder step for a future item-use event.
     * 
     * @param item the item that was used
     */
    // Placeholder for future item steps
    record ItemStep(Item item) implements TurnStep {
    }

    /**
     * Records that a single Bugemon was healed.
     *
     * @param healedBugemon
     *                      the Bugemon that received healing
     * @param hpAfterHeal
     *                      the Bugemon's HP after the heal was applied
     */
    record HealBugemonStep(CombatBugemon healedBugemon, int hpAfterHeal) implements TurnStep {
    }

    /**
     * Records that all living members of a team were healed.
     *
     * @param healedTeam
     *                          the team whose members were healed
     * @param activeHpAfterHeal
     *                          the active Bugemon's HP after the team heal was
     *                          applied
     */
    record HealTeamStep(CombatTeam healedTeam, int activeHpAfterHeal) implements TurnStep {
    }
}
