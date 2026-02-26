package ulb.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.*;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.AttackList;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Stats;
import ulb.models.bugemon.Bugemon.Type;
import ulb.models.bugemon.Effect;

public class TestParser {

    private Path dirPath = Paths.get("resources/Assets/json/");

    @Test
    public void testAttackParsing() {
        AttackList attackList = Parser.parseAttacks(dirPath.resolve("attaques.json"));

        List<Attack> attacks = attackList.getAttacks();

        // check if a list has been returned
        assertNotNull(attacks);

        // check if the attacks were parsed correctly
        assertEquals(attacks.get(0).getId(), "fouet_liane");
        assertEquals(attacks.get(1).getType(), "Flora");

        // check effects
        List<Effect> effects = attacks.get(2).getEffects();
        assertEquals(effects.get(0).getType(), "stat_modifier");
        assertEquals(effects.get(0).getModifier(), 5);
    }

    @Test
    public void testBugemonParsing() {
        AttackList attackList = Parser.parseAttacks(dirPath.resolve("attaques.json"));

        List<Attack> attacks = attackList.getAttacks();
        Map<String, Attack> attacksMap = new HashMap<>();

        for (Attack a : attacks) {
            attacksMap.put(a.getId(), a);
        }

        List<Bugemon> bugemonsList = Parser.parseBugemons(dirPath.resolve("bugemons.json"), attacksMap);

        // check if a list has been returned
        assertNotNull(bugemonsList);

        // check attributes
        assertEquals(bugemonsList.get(0).getName(), "Florachu");
        assertEquals(bugemonsList.get(1).getType(), Type.FLORA);

        // check attacks
        Bugemon bugemon2 = bugemonsList.get(2);
        AttackList bugemon2AttackList = bugemon2.getAttackList();
        List<Attack> bugemon2Attacks = bugemon2AttackList.getAttacks();

        for (Attack a : bugemon2Attacks) {
            assertEquals(a, attacks.get(attacks.indexOf(a)));
        }

        // check stats
        Bugemon bugemon3 = bugemonsList.get(3);
        Stats bugemon3Stats = bugemon3.getStats();

        assertEquals(bugemon3Stats.getAttack(), 50);
        assertEquals(bugemon3Stats.getHp(), 85);
        assertEquals(bugemon3Stats.getDefense(), 50);
        assertEquals(bugemon3Stats.getInitiative(), 60);
    }
}