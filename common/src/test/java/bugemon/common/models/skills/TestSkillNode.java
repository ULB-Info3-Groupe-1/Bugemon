package bugemon.common.models.skills;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TestSkillNode {

    @Test
    public void shouldConstructAndRetrieveProperties_whenRecordIsCreated() {
        List<String> prereqs = Collections.singletonList("start");
        SkillNode node = new SkillNode("hp_1", "+10 HP", "desc", -1, 1, 3, 1, null, prereqs);

        assertEquals("hp_1", node.id());
        assertEquals("+10 HP", node.name());
        assertEquals("desc", node.description());
        assertEquals(-1, node.x());
        assertEquals(1, node.y());
        assertEquals(3, node.maxLevel());
        assertEquals(1, node.cost());
        assertNull(node.effect());
        assertEquals(prereqs, node.prerequisites());
    }
}
