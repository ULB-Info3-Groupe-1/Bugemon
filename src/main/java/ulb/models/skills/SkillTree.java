package ulb.models.skills;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public SkillNode getById(String id) {
        return this.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nœud inconnu : " + id));
    }

    public List<SkillNode> getDependents(String nodeId) {
        return this.nodes.stream().filter(n -> n.prerequisites().contains(nodeId)).toList();
    }
}
