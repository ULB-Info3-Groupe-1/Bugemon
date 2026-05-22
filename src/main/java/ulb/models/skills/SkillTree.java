package ulb.models.skills;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Immutable directed graph of {@link SkillNode}s representing the full skill tree definition.
 *
 * <p>
 * Nodes are indexed by ID for O(1) lookup. The graph is a DAG: a node's {@code prerequisites} list references parent
 * node IDs, and {@link #getDependents(String)} provides the reverse direction.
 */
public class SkillTree {

    private final List<SkillNode> nodes;
    private final Map<String, SkillNode> index;

    public SkillTree(List<SkillNode> nodes) {
        this.nodes = List.copyOf(nodes);
        this.index = nodes.stream().collect(Collectors.toMap(SkillNode::id, Function.identity()));
    }

    public List<SkillNode> getNodes() {
        return this.nodes;
    }

    public Optional<SkillNode> findById(String id) {
        return Optional.ofNullable(this.index.get(id));
    }

    /**
     * Returns the node with the given identifier, throwing if not found.
     *
     * @param id
     *            the node identifier to look up
     * @return the matching {@link SkillNode}
     * @throws IllegalArgumentException
     *             if no node with that id exists in this tree
     */
    public SkillNode getById(String id) {
        return this.findById(id).orElseThrow(() -> new IllegalArgumentException("Unknown node: " + id));
    }

    /**
     * Returns all nodes that list {@code nodeId} as a direct prerequisite.
     *
     * @param nodeId
     *            the prerequisite node identifier
     * @return an unmodifiable list of dependent nodes; empty if none
     */
    public List<SkillNode> getDependents(String nodeId) {
        return this.nodes.stream().filter(n -> n.prerequisites().contains(nodeId)).toList();
    }
}
