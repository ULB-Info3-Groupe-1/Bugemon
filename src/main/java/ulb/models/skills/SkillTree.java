package ulb.models.skills;

import java.util.ArrayDeque;
import java.util.Deque;

public class SkillTree {
    private static String ROOT_ID = "start";

    private SkillNode root;

    public SkillTree(SkillNode root) {
        this.root = root;
    }

    public SkillNode getRoot() {
        return this.root;
    }

    public boolean canUnlock(SkillNode node, int availablePoints) {
        return node.isUnlockable() && node.getSkill().getCurrentLevel() < node.getSkill().getMaxLevel()
                && availablePoints >= node.getSkill().getCost();
    }

    public boolean canDowngrade(SkillNode node) {
        if (!node.getSkill().isUnlocked())
            return false;
        if (ROOT_ID.equals(node.getSkill().getId()))
            return false;
        for (SkillNode child : node.getChildren()) {
            if (child.getSkill().isUnlocked() && isOnlyActiveParent(node, child)) {
                return false;
            }
        }
        return true;
    }

    public int downgrade(SkillNode node) {
        // probably a better way in recursive call ://

        node.getSkill().decrementLevel();
        int refunded = node.getSkill().getCost();

        // node is locked we lock all the childeren that are only currently dependant to it
        if (!node.getSkill().isUnlocked()) {
            Deque<SkillNode> queue = new ArrayDeque<>();
            for (SkillNode child : node.getChildren()) {
                if (child.getSkill().isUnlocked() && isOnlyActiveParent(node, child)) {
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
                    if (child.getSkill().isUnlocked() && isOnlyActiveParent(current, child)) {
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
