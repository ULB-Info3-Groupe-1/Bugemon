package ulb.models.skills;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import ulb.models.utils.Position;

public class TestSkillNode {

    private Skill mockSkill;
    private Position mockPosition;
    private SkillNode skillNode;

    @Before
    public void setUp() {
        this.mockSkill = mock(Skill.class);
        this.mockPosition = mock(Position.class);
        this.skillNode = new SkillNode(this.mockSkill, this.mockPosition);
    }

    @Test
    public void shouldReturnFalseForIsUnlockable_whenSkillIsAlreadyUnlocked() {
        when(this.mockSkill.isUnlocked()).thenReturn(true);

        assertFalse(this.skillNode.isUnlockable());
    }

    @Test
    public void shouldReturnTrueForIsUnlockable_whenNodeHasNoParentsAndNotUnlocked() {
        when(this.mockSkill.isUnlocked()).thenReturn(false);

        assertTrue(this.skillNode.isUnlockable());
    }

    @Test
    public void shouldReturnTrueForIsUnlockable_whenAtLeastOneParentIsUnlocked() {
        when(this.mockSkill.isUnlocked()).thenReturn(false);

        Skill mockParentSkill1 = mock(Skill.class);
        when(mockParentSkill1.isUnlocked()).thenReturn(false);
        SkillNode parent1 = new SkillNode(mockParentSkill1, this.mockPosition);

        Skill mockParentSkill2 = mock(Skill.class);
        when(mockParentSkill2.isUnlocked()).thenReturn(true);
        SkillNode parent2 = new SkillNode(mockParentSkill2, this.mockPosition);

        this.skillNode.addParent(parent1);
        this.skillNode.addParent(parent2);

        assertTrue(this.skillNode.isUnlockable());
    }

    @Test
    public void shouldReturnFalseForIsUnlockable_whenAllParentsAreLocked() {
        when(this.mockSkill.isUnlocked()).thenReturn(false);

        Skill mockParentSkill1 = mock(Skill.class);
        when(mockParentSkill1.isUnlocked()).thenReturn(false);
        SkillNode parent1 = new SkillNode(mockParentSkill1, this.mockPosition);

        this.skillNode.addParent(parent1);

        assertFalse(this.skillNode.isUnlockable());
    }

    @Test
    public void shouldReturnActiveState_whenSkillIsUnlocked() {
        when(this.mockSkill.isUnlocked()).thenReturn(true);
        assertEquals(SkillNodeState.ACTIVE, this.skillNode.getState());
    }

    @Test
    public void shouldReturnAvailableState_whenSkillIsUnlockable() {
        when(this.mockSkill.isUnlocked()).thenReturn(false);
        assertEquals(SkillNodeState.AVAILABLE, this.skillNode.getState());
    }

    @Test
    public void shouldReturnLockedState_whenSkillIsNotUnlockable() {
        when(this.mockSkill.isUnlocked()).thenReturn(false);

        Skill mockParentSkill = mock(Skill.class);
        when(mockParentSkill.isUnlocked()).thenReturn(false);
        SkillNode parent = new SkillNode(mockParentSkill, this.mockPosition);

        this.skillNode.addParent(parent);

        assertEquals(SkillNodeState.LOCKED, this.skillNode.getState());
    }
}
