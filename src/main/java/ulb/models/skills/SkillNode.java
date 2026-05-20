package ulb.models.skills;

import java.util.List;

public record SkillNode(String id, String name, String description, int x, int y, int maxLevel, int cost,
        SkillEffect effect, List<String> prerequisites) {
}
