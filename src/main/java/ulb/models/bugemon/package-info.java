/**
 * Contains the core model classes representing Bugemon entities and their
 * combat-related data within the Bugemon game.
 *
 * <h2>Package overview</h2>
 * <p>
 * A {@link ulb.models.bugemon.Bugemon} is the central game entity: it holds
 * identifying information (ID, name, elemental type, sprite path), a set of
 * combat statistics (HP, attack, defense, initiative), a list of learnable
 * {@link ulb.models.bugemon.Attack}s, and a flag indicating whether it is a
 * starter Bugemon.
 * </p>
 *
 * <h2>Key classes and enums</h2>
 * <ul>
 *   <li>{@link ulb.models.bugemon.Bugemon} — the main entity; instances are
 *       created exclusively through the
 *       {@link ulb.models.bugemon.BugemonBuilder} fluent builder.</li>
 *   <li>{@link ulb.models.bugemon.BugemonType} — elemental type enum
 *       ({@code FLORA}, {@code AQUA}, {@code PYRO}, {@code LITHO}) that drives
 *       type-effectiveness calculations in combat.</li>
 *   <li>{@link ulb.models.bugemon.Attack} — an attack move a Bugemon can use,
 *       carrying a power value and a list of {@link ulb.models.bugemon.effect.Effect}s.</li>
 *   <li>{@link ulb.models.bugemon.effect.Effect} — a stat modification or healing
 *       effect attached to an attack, defined by a
 *       {@link ulb.models.bugemon.effect.EffectType}, a
 *       {@link ulb.models.bugemon.effect.EffectTarget}, an
 *       {@link ulb.models.bugemon.effect.EffectStat}, a modifier value, and a
 *       duration string.</li>
 *   <li>{@link ulb.models.bugemon.ActiveEffect} — a runtime wrapper around an
 *       {@link ulb.models.bugemon.effect.Effect} that tracks the number of turns
 *       remaining before the effect expires and must be reversed.</li>
 *   <li>{@link ulb.models.bugemon.effect.EffectType} — whether an effect modifies a
 *       stat ({@code STAT_MODIFIER}) or restores HP ({@code SOIN}).</li>
 *   <li>{@link ulb.models.bugemon.effect.EffectTarget} — which Bugemon(s) an effect
 *       applies to: the attacker ({@code THROWER}), the defender
 *       ({@code ADVERSARY}), the attacker's whole team ({@code TEAM}), or
 *       nobody ({@code NONE}).</li>
 *   <li>{@link ulb.models.bugemon.effect.EffectStat} — which combat statistic is
 *       modified: {@code HP}, {@code ATTACK}, {@code DEFENSE}, or
 *       {@code INITIATIVE}.</li>
 * </ul>
 *
 * <h2>Design notes</h2>
 * <ul>
 *   <li>{@link ulb.models.bugemon.Bugemon} implements
 *       {@link ulb.common.dto.BugemonDTO} so that view and controller layers can
 *       consume Bugemon data without depending on the full model class.</li>
 *   <li>Bugemon instances are cloneable via {@link ulb.models.bugemon.Bugemon#clone()}
 *       to support deep-copying for team assembly.</li>
 *   <li>All JSON-mapped fields use Gson {@code @SerializedName} annotations to
 *       bridge French field names in the data files with Java naming
 *       conventions.</li>
 * </ul>
 *
 * @see ulb.models.combat
 * @see ulb.models.trainer
 * @see ulb.common.dto.BugemonDTO
 * @see ulb.utils.Parser
 */
package ulb.models.bugemon;
