package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.Test;

import java.util.List;
import models.*;

public class TestParser {

    @Test
    public void testAttackParsing() {
        List<Attack> attacks = Parser.parseAttacks("resources/Assets/json/attaques.json");

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
}