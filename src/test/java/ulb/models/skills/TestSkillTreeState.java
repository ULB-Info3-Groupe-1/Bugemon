package ulb.models.skills;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ulb.models.skills.exceptions.IllegalNodeStateException;

public class TestSkillTreeState {

    private SkillTree tree;
    private SkillNode rootNode;
    private SkillNode childNode;
    private SkillTreeState state;

    @Before
    public void setUp() {
        this.rootNode = new SkillNode("root", "Root Skill", "desc", 0, 0, 3, 1,
                new SkillEffect.StatBonusEffect(ulb.common.StatType.HP, 10), List.of());

        this.childNode = new SkillNode("child", "Child Skill", "desc", 1, 0, 2, 1,
                new SkillEffect.StatBonusEffect(ulb.common.StatType.ATTACK, 5), List.of("root"));

        this.tree = new SkillTree(List.of(this.rootNode, this.childNode));
        this.state = new SkillTreeState();
    }

    @Test
    public void shouldThrowIllegalNodeStateException_whenAddingPointToNonUnlockableNode() {
        this.state.addPoint();
        assertThrows(IllegalNodeStateException.class, () -> {
            this.state.addPoint("child", this.tree);
        });
    }

    @Test
    public void shouldThrowIllegalNodeStateException_whenAddingPointWithoutEnoughSkillPoints() {
        assertThrows(IllegalNodeStateException.class, () -> {
            this.state.addPoint("root", this.tree);
        });
    }

    @Test
    public void shouldThrowIllegalNodeStateException_whenRemovingPointFromNodeWithZeroPoints() {
        assertThrows(IllegalNodeStateException.class, () -> {
            this.state.removePoint("root", this.tree);
        });
    }

    @Test
    public void shouldAddPoint_whenNodeIsAvailableAndHasEnoughSkillPoints() throws IllegalNodeStateException {
        this.state.addPoint();
        this.state.addPoint("root", this.tree);
        assertEquals(1, this.state.getNodeLevel("root"));
        assertEquals(0, this.state.getSkillPoints());
    }

    @Test
    public void shouldRemovePoint_whenNodeHasPoints() throws IllegalNodeStateException {
        this.state.addPoint();
        this.state.addPoint("root", this.tree);
        assertEquals(1, this.state.getNodeLevel("root"));

        this.state.removePoint("root", this.tree);
        assertEquals(0, this.state.getNodeLevel("root"));
        assertEquals(1, this.state.getSkillPoints());
    }

    @Test
    public void shouldUnlockChild_afterRootIsUnlocked() throws IllegalNodeStateException {
        this.state.addPoint();
        this.state.addPoint();
        this.state.addPoint("root", this.tree);

        this.state.addPoint("child", this.tree);
        assertEquals(1, this.state.getNodeLevel("child"));
    }
}
