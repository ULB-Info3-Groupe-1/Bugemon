package ulb.models.skills;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TestSkillBuilder {

    @Test
    public void shouldBuildDefaultSkill_whenNoMethodsCalled() {
        Skill skill = new SkillBuilder().build();

        assertEquals(0, skill.getCost());
        assertEquals(1, skill.getMaxLevel());
        assertEquals(0, skill.getCurrentLevel());
        assertNull(skill.getEffect());
    }

    @Test
    public void shouldBuildCustomSkill_whenMethodsCalled() {
        Skill skill = new SkillBuilder().id("custom_id").name("Custom Name").description("Custom Desc").cost(5)
                .maxLevel(3).currentLevel(1).effect(null).build();

        assertEquals("custom_id", skill.getId());
        assertEquals("Custom Name", skill.getName());
        assertEquals("Custom Desc", skill.getDescription());
        assertEquals(5, skill.getCost());
        assertEquals(3, skill.getMaxLevel());
        assertEquals(1, skill.getCurrentLevel());
        assertNull(skill.getEffect());
    }
}
