package ulb.models.skills;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class TestSkillTree {

    private SkillNode mockRoot;
    private SkillTree skillTree;
    private Skill mockSkill;

    @Before
    public void setUp() {
        this.mockRoot = mock(SkillNode.class);
        this.mockSkill = mock(Skill.class);
        when(this.mockRoot.getSkill()).thenReturn(this.mockSkill);
        this.skillTree = new SkillTree(this.mockRoot);
    }

    @Test
    public void shouldReturnFalseForCanUnlock_whenLevelIsMax() {
        when(this.mockSkill.getCurrentLevel()).thenReturn(3);
        when(this.mockSkill.getMaxLevel()).thenReturn(3);

        assertFalse(this.skillTree.canUnlock(this.mockRoot, 10));
    }

    @Test
    public void shouldReturnFalseForCanUnlock_whenNotEnoughPoints() {
        when(this.mockSkill.getCurrentLevel()).thenReturn(0);
        when(this.mockSkill.getMaxLevel()).thenReturn(3);
        when(this.mockSkill.getCost()).thenReturn(5);

        assertFalse(this.skillTree.canUnlock(this.mockRoot, 3));
    }

    @Test
    public void shouldReturnTrueForCanUnlock_whenAlreadyUnlocked() {
        when(this.mockSkill.getCurrentLevel()).thenReturn(1);
        when(this.mockSkill.getMaxLevel()).thenReturn(3);
        when(this.mockSkill.getCost()).thenReturn(5);
        when(this.mockSkill.isUnlocked()).thenReturn(true);

        assertTrue(this.skillTree.canUnlock(this.mockRoot, 10));
    }

    @Test
    public void shouldReturnUnlockableState_whenCanUnlockIsCalledAndConditionsMet() {
        when(this.mockSkill.getCurrentLevel()).thenReturn(0);
        when(this.mockSkill.getMaxLevel()).thenReturn(3);
        when(this.mockSkill.getCost()).thenReturn(5);
        when(this.mockSkill.isUnlocked()).thenReturn(false);
        when(this.mockRoot.isUnlockable()).thenReturn(true);

        assertTrue(this.skillTree.canUnlock(this.mockRoot, 10));
    }

    @Test
    public void shouldReturnFalseForCanDowngrade_whenNotUnlocked() {
        when(this.mockSkill.isUnlocked()).thenReturn(false);

        assertFalse(this.skillTree.canDowngrade(this.mockRoot));
    }

    @Test
    public void shouldReturnFalseForCanDowngrade_whenNodeIsRoot() {
        when(this.mockSkill.isUnlocked()).thenReturn(true);
        when(this.mockSkill.getId()).thenReturn("start");

        assertFalse(this.skillTree.canDowngrade(this.mockRoot));
    }

    @Test
    public void shouldReturnTrueForCanDowngrade_whenUnlockedAndNotRoot() {
        when(this.mockSkill.isUnlocked()).thenReturn(true);
        when(this.mockSkill.getId()).thenReturn("other");

        assertTrue(this.skillTree.canDowngrade(this.mockRoot));
    }

    @Test
    public void shouldDowngradeAndRefund_whenNodeIsDowngraded() {
        when(this.mockSkill.getCost()).thenReturn(5);
        when(this.mockSkill.isUnlocked()).thenReturn(true);

        int refunded = this.skillTree.downgrade(this.mockRoot);

        assertEquals(5, refunded);
        verify(this.mockSkill).decrementLevel();
    }
}
