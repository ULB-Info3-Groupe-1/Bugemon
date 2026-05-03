package ulb.models.skills;

import java.util.List;

import ulb.models.bugemon.effect.Effect;

public class Skill {

    private String id;

    private String name;

    private String description;

    private int cost;

    private int maxLevel;

    private Effect effect; // TODO: is it the right effect ?

    // Position in the skill tree UI => TODO: should this be in the view instead ? It is not really a property of the
    // skill itself, but it is easier to keep it here for now
    private Position position;

    private List<String> prerequisites; // List of skill IDs that must be unlocked before this skill can be unlocked

    private boolean isUnlocked;

    record Position(int x, int y) {
    }

    public Skill(String id, String name, String description, int cost, int maxLevel, Effect effect, Position position,
            List<String> prerequisites) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.maxLevel = maxLevel;
        this.effect = effect;
        this.position = position;
        this.prerequisites = prerequisites;
    }

    public List<String> getPrerequisites() {
        return this.prerequisites;
    }

    public boolean isUnlocked() {
        return this.isUnlocked;
    }

    public String getId() {
        return this.id;
    }
}
