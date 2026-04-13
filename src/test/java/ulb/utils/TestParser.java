package ulb.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectStatModifier;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.repositories.dto.CreateBugemonDTO;

public class TestParser {
    @Test
    public void testAttackParsing() {
        Parser tempInstance = new Parser();
        tempInstance.parse();

        // check if a list has been returned
        assertNotNull(tempInstance.getAttacks());

        // check if the attacks were parsed correctly
        Attack fouetLiane = tempInstance.getAttacks().values().stream().filter(a -> "fouet_liane".equals(a.id()))
                .findFirst().orElseThrow();
        assertEquals("fouet_liane", fouetLiane.id());

        // check s
        Attack racinesVives = tempInstance.getAttacks().values().stream().filter(a -> "racines_vives".equals(a.id()))
                .findFirst().orElseThrow();
        List<Effect> effects = racinesVives.effects();
        assertEquals(effects.get(0).getClass(), EffectStatModifier.class);
    }

    @Test
    public void testBugemonParsing() {
        Parser tempInstance = new Parser();
        tempInstance.parse();

        // check if a list has been returned
        assertNotNull(tempInstance.getBugemons());

        // check attributes
        CreateBugemonDTO florachu = tempInstance.getBugemons().stream().filter(b -> "Florachu".equals(b.name()))
                .findFirst().orElseThrow();
        assertEquals("Florachu", florachu.name());

        CreateBugemonDTO moussil = tempInstance.getBugemons().stream().filter(b -> "Moussil".equals(b.name()))
                .findFirst().orElseThrow();
        assertEquals(BugemonType.FLORA, moussil.type());

        // check attacks
        CreateBugemonDTO verdurion = tempInstance.getBugemons().stream().filter(b -> "Verdurion".equals(b.name()))
                .findFirst().orElseThrow();

        List<Attack> verdurionAttackList = new ArrayList<>();
        verdurionAttackList.add(verdurion.attack1());
        verdurionAttackList.add(verdurion.attack2());
        verdurionAttackList.add(verdurion.attack3());

        for (Attack a : verdurionAttackList) {
            assertEquals(a, tempInstance.getAttacks().get(a.id()));
        }

        // check stats
        CreateBugemonDTO loopine = tempInstance.getBugemons().stream().filter(b -> "Loopine".equals(b.name()))
                .findFirst().orElseThrow();

        assertEquals(50, loopine.attack());
        assertEquals(85, loopine.maxHp());
        assertEquals(50, loopine.defense());
        assertEquals(60, loopine.initiative());
    }

    @Test
    public void testParseWithInputStreams() {
        InputStream attacksStream = getClass().getResourceAsStream("/json/attaques.json");
        InputStream bugemonsStream = getClass().getResourceAsStream("/json/bugemons.json");
        InputStream itemsStream = getClass().getResourceAsStream("/json/objets.json");

        assertNotNull(attacksStream);
        assertNotNull(bugemonsStream);
        assertNotNull(itemsStream);

        Parser parser = new Parser();
        parser.parse();

        // check attacks
        Map<String, Attack> attacksMap = parser.getAttacks();
        Attack fouetLiane = attacksMap.get("fouet_liane");
        assertNotNull(fouetLiane);
        assertEquals("fouet_liane", fouetLiane.id());

        // check bugemons
        List<CreateBugemonDTO> bugemons = parser.getBugemons();
        CreateBugemonDTO florachu = bugemons.stream().filter(b -> "Florachu".equals(b.name())).findFirst()
                .orElseThrow();
        assertEquals("Florachu", florachu.name());

        CreateBugemonDTO moussil = bugemons.stream().filter(b -> "Moussil".equals(b.name())).findFirst().orElseThrow();
        assertEquals(BugemonType.FLORA, moussil.type());
    }

    @Test
    public void testParseItems() {
        InputStream itemsStream = getClass().getResourceAsStream("/json/objets.json");

        assertNotNull(itemsStream);

        Parser tempInstance = new Parser();
        tempInstance.parse();

        List<Item> itemsList = tempInstance.getItems();
        Inventory inventory = tempInstance.getInventory();

        assertNotNull(itemsList);
        assertNotNull(inventory);

        Item testItem = itemsList.stream().filter(o -> "baie_revigorante".equals(o.id())).findFirst().orElseThrow();

        Effect effect = new EffectStatModifier(EffectTarget.THROWER, EffectStat.HP, 20, null);
        Item potion = new Item("baie_revigorante", "Baie Revigorante", "Restaure 20 PV au Bugémon actif.",
                Item.ItemType.HEALING, effect);

        assertEquals(potion.id(), testItem.id());
        assertEquals(potion.name(), testItem.name());
        assertEquals(potion.description(), testItem.description());
        assertEquals(potion.type(), testItem.type());

        assertEquals(7, inventory.getMap().values().stream().mapToInt(i -> i).sum());
        Map<Item, Integer> items = inventory.getMap();
        long revigoranteCount = items.entrySet().stream().filter(e -> "baie_revigorante".equals(e.getKey().id()))
                .mapToLong(Map.Entry::getValue).sum();
        long toniqueCount = items.entrySet().stream().filter(e -> "baie_tonique".equals(e.getKey().id()))
                .mapToLong(Map.Entry::getValue).sum();
        long gelCount = items.entrySet().stream().filter(e -> "gel_defensif".equals(e.getKey().id()))
                .mapToLong(Map.Entry::getValue).sum();

        long serumCount = items.entrySet().stream().filter(e -> "serum_offensif".equals(e.getKey().id()))
                .mapToLong(Map.Entry::getValue).sum();

        assertEquals(3, revigoranteCount);
        assertEquals(2, toniqueCount);
        assertEquals(1, gelCount);
        assertEquals(1, serumCount);
    }
}
