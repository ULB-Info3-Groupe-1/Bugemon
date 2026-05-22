package ulb.models.bugemon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import ulb.models.bugemon.exceptions.InvalidAttackCountException;

public class TestBugemon {

    private List<Attack> createValidAttacksList() {
        List<Attack> attacks = new ArrayList<>();
        for (int i = 0; i < Bugemon.ATTACKS_COUNT; i++) {
            attacks.add(Mockito.mock(Attack.class));
        }
        return attacks;
    }

    @Test
    public void shouldThrowException_whenNameIsBlank() {
        List<Attack> validAttacks = this.createValidAttacksList();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Bugemon("", 100, 10, 10, 10, ElementType.FLORA, validAttacks, "sprite.png", true, false);
        });

        assertEquals("Bugemon's name cannot be empty", exception.getMessage());
    }

    @Test
    public void shouldThrowException_whenHpIsNegative() {
        List<Attack> validAttacks = this.createValidAttacksList();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Bugemon("Pikabug", -10, 10, 10, 10, ElementType.FLORA, validAttacks, "sprite.png", true, false);
        });

        assertEquals("Bugemon's current hp must be non-negative", exception.getMessage());
    }

    @Test
    public void shouldThrowException_whenStatsAreZeroOrNegative() {
        List<Attack> validAttacks = this.createValidAttacksList();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Bugemon("Pikabug", 100, 0, 10, 10, ElementType.FLORA, validAttacks, "sprite.png", true, false);
        });

        assertEquals("Stats must be strictly positive", exception.getMessage());
    }

    @Test
    public void shouldThrowInvalidAttackCountException_whenAttacksSizeIsWrong() {
        List<Attack> invalidAttacks = new ArrayList<>();

        assertThrows(InvalidAttackCountException.class, () -> {
            new Bugemon("Pikabug", 100, 10, 10, 10, ElementType.FLORA, invalidAttacks, "sprite.png", true, false);
        });
    }

    @Test
    public void shouldConsiderEqual_whenNamesAreIdentical() {
        List<Attack> validAttacks = this.createValidAttacksList();

        Bugemon bugemon1 = new Bugemon("UniqueName", 100, 10, 10, 10, ElementType.FLORA, validAttacks, "sprite1.png",
                true, false);
        Bugemon bugemon2 = new Bugemon("UniqueName", 150, 20, 20, 20, ElementType.AQUA, validAttacks, "sprite2.png",
                false, false);
        Bugemon bugemon3 = new Bugemon("OtherName", 100, 10, 10, 10, ElementType.FLORA, validAttacks, "sprite1.png",
                true, false);

        assertEquals(bugemon1, bugemon2);
        assertNotEquals(bugemon1, bugemon3);
    }
}
