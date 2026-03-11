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
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectType;

public class TestParser {
    @Test
    public void testAttackParsing() {
        List<Attack> attackList = Parser.parseAttacks(new InputStreamReader(
                getClass().getResourceAsStream("/json/attaques.json"), StandardCharsets.UTF_8));

        // check if a list has been returned
        assertNotNull(attackList);

        // check if the attacks were parsed correctly
        Attack fouetLiane = attackList.stream()
                                    .filter(a -> "fouet_liane".equals(a.getId()))
                                    .findFirst()
                                    .orElseThrow();
        assertEquals("fouet_liane", fouetLiane.getId());

        // check effects
        Attack racinesVives = attackList.stream()
                                      .filter(a -> "racines_vives".equals(a.getId()))
                                      .findFirst()
                                      .orElseThrow();
        List<Effect> effects = racinesVives.getEffects();
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
        Bugemon florachu = bugemonsList.stream()
                                   .filter(b -> "Florachu".equals(b.getName()))
                                   .findFirst()
                                   .orElseThrow();
        assertEquals(florachu.getName(), "Florachu");

        Bugemon moussil = bugemonsList.stream()
                                  .filter(b -> "Moussil".equals(b.getName()))
                                  .findFirst()
                                  .orElseThrow();
        assertEquals(moussil.getType(), BugemonType.FLORA);

        // check sprite URL begins with "png/"
        assertEquals(florachu.getSpriteURL(), "png/florachu.png");

        // check attacks
        Bugemon verdurion = bugemonsList.stream()
                                    .filter(b -> "Verdurion".equals(b.getName()))
                                    .findFirst()
                                    .orElseThrow();
        List<Attack> verdurionAttackList = verdurion.getAttackList();

        for (Attack a : verdurionAttackList) {
            assertEquals(a, attacksMap.get(a.getId()));
        }

        // check stats
        Bugemon loopine = bugemonsList.stream()
                                  .filter(b -> "Loopine".equals(b.getName()))
                                  .findFirst()
                                  .orElseThrow();

        assertEquals(loopine.getAttack(), 50);
        assertEquals(loopine.getHp(), 85);
        assertEquals(loopine.getDefense(), 50);
        assertEquals(loopine.getInitiative(), 60);
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
        Bugemon florachu = bugemons.stream()
                                   .filter(b -> "Florachu".equals(b.getName()))
                                   .findFirst()
                                   .orElseThrow();
        assertEquals("Florachu", florachu.getName());

        Bugemon moussil = bugemons.stream()
                                  .filter(b -> "Moussil".equals(b.getName()))
                                  .findFirst()
                                  .orElseThrow();
        assertEquals(BugemonType.FLORA, moussil.getType());
    }
}
