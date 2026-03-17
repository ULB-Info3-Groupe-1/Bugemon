package ulb.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.GameObject;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.ObjectWrapper;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.models.bugemon.effect.EffectType;

public class TestParser {
    @Test
    public void testAttackParsing() {
        Parser parser = new Parser();
        parser.parse();

        // check if a list has been returned
        assertNotNull(parser.getAttacks());

        // check if the attacks were parsed correctly
        Attack fouetLiane = parser.getAttacks()
                                    .values()
                                    .stream()
                                    .filter(a -> "fouet_liane".equals(a.id()))
                                    .findFirst()
                                    .orElseThrow();
        assertEquals("fouet_liane", fouetLiane.id());

        // check effects
        Attack racinesVives = parser.getAttacks()
                                      .values()
                                      .stream()
                                      .filter(a -> "racines_vives".equals(a.id()))
                                      .findFirst()
                                      .orElseThrow();
        List<Effect> effects = racinesVives.effects();
        assertEquals(effects.get(0).getTypeEffect(), EffectType.STAT_MODIFIER);
        assertEquals(effects.get(0).getModifier(), 5);
        assertEquals(effects.get(0).getStat(), EffectStat.DEFENSE);
    }

    @Test
    public void testBugemonParsing() {
        Parser parser = new Parser();
        parser.parse();

        // check if a list has been returned
        assertNotNull(parser.getBugemons());

        // check attributes
        Bugemon florachu = parser.getBugemons()
                                   .stream()
                                   .filter(b -> "Florachu".equals(b.getName()))
                                   .findFirst()
                                   .orElseThrow();
        assertEquals(florachu.getName(), "Florachu");

        Bugemon moussil = parser.getBugemons()
                                  .stream()
                                  .filter(b -> "Moussil".equals(b.getName()))
                                  .findFirst()
                                  .orElseThrow();
        assertEquals(moussil.getType(), BugemonType.FLORA);

        // check sprite URL begins with "png/"
        assertEquals(florachu.getSpriteURL(), "png/florachu.png");

        // check attacks
        Bugemon verdurion = parser.getBugemons()
                                    .stream()
                                    .filter(b -> "Verdurion".equals(b.getName()))
                                    .findFirst()
                                    .orElseThrow();
        List<Attack> verdurionAttackList = verdurion.getAttackList();

        for (Attack a : verdurionAttackList) {
            assertEquals(a, parser.getAttacks().get(a.id()));
        }

        // check stats
        Bugemon loopine = parser.getBugemons()
                                  .stream()
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

        Parser parser = new Parser();
        parser.parse();

        // check attacks
        Map<String, Attack> attacksMap = parser.getAttacks();
        Attack fouetLiane = attacksMap.get("fouet_liane");
        assertNotNull(fouetLiane);
        assertEquals("fouet_liane", fouetLiane.id());

        // check bugemons
        List<Bugemon> bugemons = parser.getBugemons();
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

    @Test
    public void testParseObjects() {
        InputStream objectsStream = getClass().getResourceAsStream("/json/objets.json");

        assertNotNull(objectsStream);

        ObjectWrapper wrapper = Parser.parseObjectsAndInventory(
                new InputStreamReader(objectsStream, StandardCharsets.UTF_8));
        List<GameObject> objectsList = wrapper.getObjects();
        Inventory inventory = wrapper.getInventory();

        assertNotNull(objectsList);
        assertNotNull(inventory);

        GameObject testObject = objectsList.stream()
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

        assertEquals(7, inventory.getObjects().values().stream().mapToInt(i -> i).sum());
        Map<GameObject, Integer> objects = inventory.getObjects();
        long revigoranteCount = objects.entrySet()
                                        .stream()
                                        .filter(e -> "baie_revigorante".equals(e.getKey().id()))
                                        .mapToLong(Map.Entry::getValue)
                                        .sum();
        long toniqueCount = objects.entrySet()
                                    .stream()
                                    .filter(e -> "baie_tonique".equals(e.getKey().id()))
                                    .mapToLong(Map.Entry::getValue)
                                    .sum();
        long gelCount = objects.entrySet()
                                .stream()
                                .filter(e -> "gel_defensif".equals(e.getKey().id()))
                                .mapToLong(Map.Entry::getValue)
                                .sum();
        long serumCount = objects.entrySet()
                                  .stream()
                                  .filter(e -> "serum_offensif".equals(e.getKey().id()))
                                  .mapToLong(Map.Entry::getValue)
                                  .sum();

        assertEquals(3, revigoranteCount);
        assertEquals(2, toniqueCount);
        assertEquals(1, gelCount);
        assertEquals(1, serumCount);
    }
}
