/**
 * Player inventory and item model.
 *
 * <p>
 * An {@link ulb.models.item.Item} is an immutable value object (id, name, description, type, effect) loaded from the
 * game's data files. Items are categorised by {@link ulb.models.item.ItemType} and carry an
 * {@link ulb.models.effect.Effect} that is applied when the item is used in combat.
 *
 * <p>
 * {@link ulb.models.item.Inventory} maintains the player's item-to-quantity map across a run; quantities are updated
 * via {@link ulb.models.item.Inventory#addItem} and {@link ulb.models.item.Inventory#useItem}.
 */
package ulb.models.item;
