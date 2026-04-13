/**
 * Trainer hierarchy used during combat. {@link ulb.models.trainer.AutoTrainer} selects actions randomly;
 * {@link ulb.models.trainer.ManualTrainer} requires the caller to enqueue a {@link ulb.models.trainer.TurnAction}
 * before each turn.
 *
 * {@code TurnAction} is a sealed interface with three permitted records: {@code AttackAction}, {@code SwitchAction},
 * and {@code ForfeitAction}.
 */
package ulb.models.trainer;
