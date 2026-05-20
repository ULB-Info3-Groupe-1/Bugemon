package ulb.models.skills;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TestSkill {

    private Skill skill;

    @Before
    public void setUp() {
        this.skill = new Skill("id1", "name", "desc", 10, 3, 0);
    }

    @Test
    public void shouldReturnFalseForIsUnlocked_whenLevelIsZero() {
        assertFalse(this.skill.isUnlocked());
    }

    @Test
    public void shouldReturnTrueForIsUnlocked_whenLevelIsGreaterThanZero() {
        this.skill.setCurrentLevel(1);
        assertTrue(this.skill.isUnlocked());
    }

    @Test
    public void shouldIncrementLevel_whenIncrementLevelIsCalled() {
        this.skill.incrementLevel();
        assertEquals(1, this.skill.getCurrentLevel());
    }

    @Test
    public void shouldDecrementLevel_whenLevelIsGreaterThanZero() {
        this.skill.setCurrentLevel(2);
        this.skill.decrementLevel();
        assertEquals(1, this.skill.getCurrentLevel());
    }

    @Test
    public void shouldNotDecrementLevel_whenLevelIsZero() {
        this.skill.decrementLevel();
        assertEquals(0, this.skill.getCurrentLevel());
    }
}
