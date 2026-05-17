package ulb.models.skills;

import ulb.Configuration;

@SuppressWarnings("checkstyle:HiddenField")
public final class SkillBuilder {

    private static final String DEFAULT_ID = Configuration.Skill.DEFAULT_ID;
    private static final String DEFAULT_NAME = Configuration.Skill.DEFAULT_NAME;
    private static final String DEFAULT_DESCRIPTION = Configuration.Skill.DEFAULT_DESCRIPTION;
    private static final int DEFAULT_COST = 0;
    private static final int DEFAULT_MAX_LEVEL = 1;
    private static final int DEFAULT_LEVEL = 0; // the default skill is unlocked by default

    private String id = DEFAULT_ID;
    private String name = DEFAULT_NAME;
    private String description = DEFAULT_DESCRIPTION;
    private int cost = DEFAULT_COST;
    private int maxLevel = DEFAULT_MAX_LEVEL;
    private int currentLevel = DEFAULT_LEVEL;
    private SkillEffect effect = null;

    public SkillBuilder id(String id) {
        this.id = id;
        return this;
    }

    public SkillBuilder name(String name) {
        this.name = name;
        return this;
    }

    public SkillBuilder description(String description) {
        this.description = description;
        return this;
    }

    public SkillBuilder cost(int cost) {
        this.cost = cost;
        return this;
    }

    public SkillBuilder maxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    public SkillBuilder effect(SkillEffect effect) {
        this.effect = effect;
        return this;
    }

    public SkillBuilder currentLevel(int level) {
        this.currentLevel = level;
        return this;
    }

    public Skill build() {
        return new Skill(this.id, this.name, this.description, this.cost, this.maxLevel, this.currentLevel,
                this.effect);
    }
}
