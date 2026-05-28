package bugemon.common.models.skills;

/**
 * Represents the availability state of a {@link SkillNode} from the player's perspective.
 *
 * <ul>
 * <li>{@link #ACTIVE} — the node is fully or partially unlocked by the player.</li>
 * <li>{@link #AVAILABLE} — all prerequisites are met; the player may spend points to unlock it.</li>
 * <li>{@link #LOCKED} — one or more prerequisites are not yet satisfied.</li>
 * </ul>
 */
public enum SkillStatus {
    ACTIVE,
    AVAILABLE,
    LOCKED
}
