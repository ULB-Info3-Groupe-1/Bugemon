package ulb.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Effect;
import ulb.models.bugemon.EffectStat;
import ulb.models.bugemon.EffectTarget;
import ulb.models.bugemon.EffectType;
import ulb.models.bugemon.GameObject;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.ObjectWrapper;

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
        assertEquals(moussil.getType(), Bugemon.BType.FLORA);

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
        InputStream objectsStream = getClass().getResourceAsStream("/json/objets.json");

        assertNotNull(attacksStream);
        assertNotNull(bugemonsStream);
        assertNotNull(objectsStream);

        Parser.ParseResult result = Parser.parse(attacksStream, bugemonsStream, objectsStream);

        assertNotNull(result);
        assertNotNull(result.getAttacksMap());
        assertNotNull(result.getBugemonsList());
        assertNotNull(result.getObjectsList());

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
        assertEquals(Bugemon.BType.FLORA, moussil.getType());
    }

    @Test
    public void testParseObjects() {
        InputStream objectsStream = getClass().getResourceAsStream("/json/objets.json");

        assertNotNull(objectsStream);

        ObjectWrapper wrapper = Parser.parseObjectsAndInventory(
                new InputStreamReader(objectsStream, StandardCharsets.UTF_8));
        List<GameObject> objects = wrapper.getObjects();
        Inventory inventory = wrapper.getInventory();

        assertNotNull(objects);
        assertNotNull(inventory);

        GameObject testObject = objects.stream()
                                        .filter(o -> "baie_revigorante".equals(o.id()))
                                        .findFirst()
                                        .orElseThrow();

        Effect effect = new Effect(EffectType.SOIN, EffectTarget.THROWER, null, 20, null);
        GameObject potion =
                new GameObject("baie_revigorante", "Baie Revigorante",
                               "Restaure 20 PV au Bugémon actif.", GameObject.OType.HEALING, effect,
                               "baie_revigorante.png"); // TODO: sprite with png/ or not?

        assertEquals(potion.id(), testObject.id());
        assertEquals(potion.name(), testObject.name());
        assertEquals(potion.description(), testObject.description());
        assertEquals(potion.type(), testObject.type());
        assertEquals(potion.sprite(), testObject.sprite());

        assertEquals(7, inventory.getObjects().size());
        long revigoranteCount = inventory.getObjects()
                                        .stream()
                                        .filter(o -> "baie_revigorante".equals(o.id()))
                                        .count();
        long toniqueCount =
                inventory.getObjects().stream().filter(o -> "baie_tonique".equals(o.id())).count();
        long gelCount =
                inventory.getObjects().stream().filter(o -> "gel_defensif".equals(o.id())).count();
        long serumCount = inventory.getObjects()
                                  .stream()
                                  .filter(o -> "serum_offensif".equals(o.id()))
                                  .count();

        assertEquals(3, revigoranteCount);
        assertEquals(2, toniqueCount);
        assertEquals(1, gelCount);
        assertEquals(1, serumCount);
    }
}
