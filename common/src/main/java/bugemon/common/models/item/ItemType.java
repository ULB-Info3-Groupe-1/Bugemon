package bugemon.common.models.item;

/**
 * Broad category of an {@link Item}, used to filter and group items in the inventory UI.
 *
 * <ul>
 * <li>{@link #HEALING} — restores hit points to one or more Bugemons.</li>
 * <li>{@link #BOOST} — applies a temporary or permanent stat modifier.</li>
 * </ul>
 */
public enum ItemType {
    HEALING,
    BOOST
}
