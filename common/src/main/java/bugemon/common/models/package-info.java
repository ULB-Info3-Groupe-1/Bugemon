/**
 * Model layer of the MVC architecture.
 *
 * <p>
 * Contains no classes directly; all types live in sub-packages:
 * <ul>
 * <li>{@code bugemon} — core game entities ({@link bugemon.common.models.bugemon.Bugemon}, attacks, element types)</li>
 * <li>{@code effect} — item and attack effect hierarchy (Visitor pattern)</li>
 * <li>{@code item} — {@link bugemon.common.models.item.Item} and {@link bugemon.common.models.item.Inventory}</li>
 * <li>{@code music} — audio model (background ambiance, sound effects)</li>
 * <li>{@code player} — player state, Bugemon ownership, and input-handling contract</li>
 * <li>{@code run} — runtime wrappers used during an active tower run or standalone combat</li>
 * <li>{@code skills} — skill-tree nodes, state, and unlocking logic</li>
 * <li>{@code utils} — shared value types such as {@link bugemon.common.models.utils.Position}</li>
 * </ul>
 *
 * <p>
 * The model is deliberately decoupled from JavaFX; data consumed by views is exposed through DTO objects in
 * {@code bugemon.common.dto} rather than through the full model classes.
 */
package bugemon.common.models;
