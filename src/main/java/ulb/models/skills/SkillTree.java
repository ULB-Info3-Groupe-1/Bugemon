package ulb.models.skills;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SkillTree {
    private static final String ROOT_ID = "start";

    private SkillNode root;

    public SkillTree(SkillNode root) {
        this.root = root;
    }

    public SkillNode getRoot() {
        return this.root;
    }

    public List<SkillNode> getAllNodes() {
        // Maybe I shouldn't have made a root node as simply storing
        // hashmap seems enough I gess... TODO: check if I'm true lol
        // @ManuelRocca could you please do that ?

        List<SkillNode> result = new ArrayList<>();
        Set<SkillNode> visited = new HashSet<>();
        Deque<SkillNode> queue = new ArrayDeque<>();

        queue.add(this.root);
        while (!queue.isEmpty()) {
            SkillNode current = queue.poll();
            if (!visited.add(current)) {
                continue;
            }
            result.add(current);
            queue.addAll(current.getChildren());
        }

        return result;
    }

    public boolean canUnlock(SkillNode node, int availablePoints) {
        if (node.getSkill().getCurrentLevel() >= node.getSkill().getMaxLevel()) {
            return false;
        }
        if (availablePoints < node.getSkill().getCost()) {
            return false;
        }
        if (node.getSkill().isUnlocked()) {
            return true;
        }
        return node.isUnlockable();
    }

    public boolean canDowngrade(SkillNode node) {
        if (!node.getSkill().isUnlocked()) {
            return false;
        }
        if (ROOT_ID.equals(node.getSkill().getId())) {
            return false;
        }
        return true;
    }

    public int downgrade(SkillNode node) {
        // probably a better way with maybe a recursive call ://
        // but I'm not a recursive expert sorry guys ...

        node.getSkill().decrementLevel();
        int refunded = node.getSkill().getCost();

        // node is locked we lock all the childeren that are only currently dependant to it
        if (!node.getSkill().isUnlocked()) {
            Deque<SkillNode> queue = new ArrayDeque<>();
            for (SkillNode child : node.getChildren()) {
                if (child.getSkill().isUnlocked() && this.isOnlyActiveParent(node, child)) {
                    queue.add(child);
                }
            }

            while (!queue.isEmpty()) {
                SkillNode current = queue.poll();

                while (current.getSkill().isUnlocked()) {
                    current.getSkill().decrementLevel();
                    refunded += current.getSkill().getCost();
                }

                for (SkillNode child : current.getChildren()) {
                    if (child.getSkill().isUnlocked() && this.isOnlyActiveParent(current, child)) {
                        queue.add(child);
                    }
                }
            }
        }

        return refunded;
    }

    private boolean isOnlyActiveParent(SkillNode candidate, SkillNode child) {
        for (SkillNode parent : child.getParents()) {
            if (parent != candidate && parent.getSkill().isUnlocked()) {
                return false;
            }
        }
        return true;
    }
}
