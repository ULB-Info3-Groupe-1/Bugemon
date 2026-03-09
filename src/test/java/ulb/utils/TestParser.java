package ulb.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Effect;
import ulb.models.bugemon.EffectStat;
import ulb.models.bugemon.EffectType;

public class TestParser {
    @Test
    public void testAttackParsing() {
        List<Attack> attackList = Parser.parseAttacks(new InputStreamReader(
                getClass().getResourceAsStream("/json/attaques.json"), StandardCharsets.UTF_8));

        // check if a list has been returned
        assertNotNull(attackList);

        // check if the attacks were parsed correctly
        assertEquals(attackList.get(0).getId(), "fouet_liane");
        assertEquals(attackList.get(1).getType(), Bugemon.BType.FLORA);

        // check effects
        List<Effect> effects = attackList.get(2).getEffects();
        assertEquals(effects.get(0).getTypeEffect(), EffectType.STAT_MODIFIER);
        assertEquals(effects.get(0).getModifier(), 5);
        assertEquals(effects.get(0).getStat(), EffectStat.DEFENSE);
    }

    @Test
    public void testBugemonParsing() {
        List<Attack> attackList = Parser.parseAttacks(new InputStreamReader(
                getClass().getResourceAsStream("/json/attaques.json"), StandardCharsets.UTF_8));

        Map<String, Attack> attacksMap =
                attackList.stream().collect(Collectors.toMap(Attack::getId, Function.identity()));

        List<Bugemon> bugemonsList = Parser.parseBugemons(
                new InputStreamReader(getClass().getResourceAsStream("/json/bugemons.json"),
                                      StandardCharsets.UTF_8),
                attacksMap);

        // check if a list has been returned
        assertNotNull(bugemonsList);

        // check attributes
        assertEquals(bugemonsList.get(0).getName(), "Florachu");
        assertEquals(bugemonsList.get(1).getType(), Bugemon.BType.FLORA);

        // check sprite URL begins with "png/"
        assertEquals(bugemonsList.get(0).getSpriteURL(), "png/florachu.png");

        // check attacks
        Bugemon bugemon2 = bugemonsList.get(2);
        List<Attack> bugemon2AttackList = bugemon2.getAttackList();

        for (Attack a : bugemon2AttackList) {
            assertEquals(a, attackList.get(attackList.indexOf(a)));
        }

        // check stats
        Bugemon bugemon3 = bugemonsList.get(3);

        assertEquals(bugemon3.getAttack(), 50);
        assertEquals(bugemon3.getHp(), 85);
        assertEquals(bugemon3.getDefense(), 50);
        assertEquals(bugemon3.getInitiative(), 60);
    }

    @Test
    public void testParseWithInputStreams() {
        InputStream attacksStream = getClass().getResourceAsStream("/json/attaques.json");
        InputStream bugemonsStream = getClass().getResourceAsStream("/json/bugemons.json");

        assertNotNull(attacksStream);
        assertNotNull(bugemonsStream);

        Parser.ParseResult result = Parser.parse(attacksStream, bugemonsStream);

        assertNotNull(result);
        assertNotNull(result.getAttacksMap());
        assertNotNull(result.getBugemonsList());

        // check attacks
        Map<String, Attack> attacksMap = result.getAttacksMap();
        Attack fouetLiane = attacksMap.get("fouet_liane");
        assertNotNull(fouetLiane);
        assertEquals("fouet_liane", fouetLiane.getId());

        // check bugemons
        List<Bugemon> bugemons = result.getBugemonsList();
        assertEquals("Florachu", bugemons.get(0).getName());
        assertEquals(Bugemon.BType.FLORA, bugemons.get(1).getType());
    }
}
