package bugemon.common.models.skills;

import java.io.Serializable;
import java.util.List;

/**
 * Immutable definition of a single node in the skill tree.
 *
 * <p>
 * Coordinates {@code x} and {@code y} are used by the skill-tree view to position the node. The {@code prerequisites}
 * list contains the IDs of nodes that must be at their maximum level before this node can be unlocked.
 *
 * @param id
 *            unique identifier for this node
 * @param name
 *            display name shown in the UI
 * @param description
 *            human-readable description of the effect
 * @param x
 *            horizontal position in the skill-tree layout
 * @param y
 *            vertical position in the skill-tree layout
 * @param maxLevel
 *            maximum number of times this node can be unlocked
 * @param cost
 *            skill points required per unlock level
 * @param effect
 *            the gameplay effect applied when the node is unlocked
 * @param prerequisites
 *            IDs of nodes that must be fully unlocked first
 */
public record SkillNode(String id, String name, String description, int x, int y, int maxLevel, int cost,
        SkillEffect effect, List<String> prerequisites) implements Serializable {
}
