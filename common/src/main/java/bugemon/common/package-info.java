/**
 * Shared domain types used across all layers of the Bugemon application.
 *
 * <p>
 * This package contains enumerations and small value objects that carry no dependencies on any specific layer (model,
 * service, repository, view). Placing them here avoids circular imports and makes them freely reusable throughout the
 * codebase.
 *
 * <p>
 * Included types:
 * <ul>
 * <li>{@link bugemon.common.EffectDuration} — lifetime of a combat effect</li>
 * <li>{@link bugemon.common.EffectTarget} — recipient of a combat effect</li>
 * <li>{@link bugemon.common.LevelUpResult} — outcome of a level-up event</li>
 * <li>{@link bugemon.common.RoomState} — navigation status of a tower room</li>
 * <li>{@link bugemon.common.RoomType} — category of a tower room</li>
 * <li>{@link bugemon.common.StatType} — Bugemon statistic modified by effects or skills</li>
 * </ul>
 */
package bugemon.common;
