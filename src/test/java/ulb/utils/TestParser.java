package ulb.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectType;

public class TestParser {
    @Test
    public void testAttackParsing() {
        Parser tempInstance = Parser.getInstance();
        tempInstance.parse();

        // check if a list has been returned
        assertNotNull(tempInstance.getAttacks());

        // check if the attacks were parsed correctly
        Attack fouetLiane = tempInstance.getAttacks().values().stream()
                                    .filter(a -> "fouet_liane".equals(a.getId()))
                                    .findFirst()
                                    .orElseThrow();
        assertEquals("fouet_liane", fouetLiane.getId());

        // check effects
        Attack racinesVives = tempInstance.getAttacks().values().stream()
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
        Parser tempInstance = Parser.getInstance();
        tempInstance.parse();

        // check if a list has been returned
        assertNotNull(tempInstance.getBugemons());

        // check attributes
        Bugemon florachu = tempInstance.getBugemons().stream()
                                   .filter(b -> "Florachu".equals(b.getName()))
                                   .findFirst()
                                   .orElseThrow();
        assertEquals(florachu.getName(), "Florachu");

        Bugemon moussil = tempInstance.getBugemons().stream()
                                  .filter(b -> "Moussil".equals(b.getName()))
                                  .findFirst()
                                  .orElseThrow();
        assertEquals(moussil.getType(), BugemonType.FLORA);

        // check sprite URL begins with "png/"
        assertEquals(florachu.getSpriteURL(), "png/florachu.png");

        // check attacks
        Bugemon verdurion = tempInstance.getBugemons().stream()
                                    .filter(b -> "Verdurion".equals(b.getName()))
                                    .findFirst()
                                    .orElseThrow();
        List<Attack> verdurionAttackList = verdurion.getAttackList();

        for (Attack a : verdurionAttackList) {
            assertEquals(a, tempInstance.getAttacks().get(a.getId()));
        }

        // check stats
        Bugemon loopine = tempInstance.getBugemons().stream()
                                  .filter(b -> "Loopine".equals(b.getName()))
                                  .findFirst()
                                  .orElseThrow();

        assertEquals(loopine.getAttack(), 50);
        assertEquals(loopine.getHp(), 85);
        assertEquals(loopine.getDefense(), 50);
        assertEquals(loopine.getInitiative(), 60);
    }
}
