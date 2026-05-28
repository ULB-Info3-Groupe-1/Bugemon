/**
 * Player inventory and item model.
 *
 * <p>
 * An {@link bugemon.common.models.item.Item} is an immutable value object (id, name, description, type, effect) loaded from the
 * game's data files. Items are categorised by {@link bugemon.common.models.item.ItemType} and carry an
 * {@link bugemon.common.models.effect.Effect} that is applied when the item is used in combat.
 *
 * <p>
 * {@link bugemon.common.models.item.Inventory} maintains the player's item-to-quantity map across a run; quantities are updated
 * via {@link bugemon.common.models.item.Inventory#addItem} and {@link bugemon.common.models.item.Inventory#useItem}.
 */
package bugemon.common.models.item;
