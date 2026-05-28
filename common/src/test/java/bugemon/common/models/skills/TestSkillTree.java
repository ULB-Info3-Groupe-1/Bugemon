package bugemon.common.models.skills;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class TestSkillTree {

    private SkillTree skillTree;
    private SkillNode nodeA;
    private SkillNode nodeB;

    @Before
    public void setUp() {
        this.nodeA = new SkillNode("A", "A", "desc", 0, 0, 1, 1, null, Collections.emptyList());
        this.nodeB = new SkillNode("B", "B", "desc", 0, 0, 1, 1, null, List.of("A"));
        this.skillTree = new SkillTree(List.of(this.nodeA, this.nodeB));
    }

    @Test
    public void shouldReturnAllNodes_whenGetNodesIsCalled() {
        List<SkillNode> nodes = this.skillTree.getNodes();
        assertEquals(2, nodes.size());
        assertTrue(nodes.contains(this.nodeA));
        assertTrue(nodes.contains(this.nodeB));
    }

    @Test
    public void shouldFindNodeById_whenNodeExists() {
        Optional<SkillNode> found = this.skillTree.findById("A");
        assertTrue(found.isPresent());
        assertEquals(this.nodeA, found.get());
    }

    @Test
    public void shouldReturnEmptyOptional_whenNodeDoesNotExist() {
        Optional<SkillNode> found = this.skillTree.findById("C");
        assertTrue(found.isEmpty());
    }

    @Test
    public void shouldGetNodeById_whenNodeExists() {
        SkillNode found = this.skillTree.getById("B");
        assertEquals(this.nodeB, found);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowException_whenGettingNonExistentNodeById() {
        this.skillTree.getById("C");
    }

    @Test
    public void shouldGetDependents_whenNodeIsAPrerequisite() {
        List<SkillNode> dependents = this.skillTree.getDependents("A");
        assertEquals(1, dependents.size());
        assertEquals(this.nodeB, dependents.get(0));
    }
}
