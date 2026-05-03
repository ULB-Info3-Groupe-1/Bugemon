package ulb.models.skills;

import java.util.ArrayList;
import java.util.List;

import ulb.Configuration;
import ulb.models.bugemon.effect.Effect;
import ulb.models.utils.Position;

public final class SkillBuilder {

    private static final String DEFAULT_ID = Configuration.Skill.DEFAULT_ID;

    private static final String DEFAULT_NAME = Configuration.Skill.DEFAULT_NAME;

    private static final String DEFAULT_DESCRIPTION = Configuration.Skill.DEFAULT_DESCRIPTION;

    private static final int DEFAULT_COST = 0;

    private static final int DEFAULT_MAX_LEVEL = 1;

    private static final Effect DEFAULT_EFFECT = null;

    // TODO: Effect ? how should I handle it ? with the object's complexity, what
    // value
    // should I put as default ? null ?

    private static final List<String> DEFAULT_PREREQUISITES = new ArrayList<>();

    private static final boolean DEFAULT_IS_UNLOCKED = true; // the default skill is unlocked by default
}
