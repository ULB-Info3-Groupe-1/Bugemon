package bugemon.common.models.effect;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestTicker {

    @Test
    public void shouldNotBeExpired_whenCreatedWithPositiveTicks() {
        Ticker ticker = new Ticker(3);
        assertFalse("Ticker should not be expired if it has remaining ticks", ticker.isExpired());
    }

    @Test
    public void shouldBeExpired_whenCreatedWithZeroTicks() {
        Ticker ticker = new Ticker(0);
        assertTrue("Ticker should be expired upon creation with 0 ticks", ticker.isExpired());
    }

    @Test
    public void shouldBeExpired_whenCreatedWithNegativeTicks() {
        Ticker ticker = new Ticker(-5);
        assertTrue("Ticker should cap at 0 and be expired if initialized with a negative value", ticker.isExpired());
    }

    @Test
    public void shouldDecreaseTicks_whenTickIsCalled() {
        Ticker ticker = new Ticker(2);
        assertFalse(ticker.isExpired());

        ticker.tick();
        assertFalse("Ticker should still not be expired", ticker.isExpired());

        ticker.tick();
        assertTrue("Ticker should be expired after 2 ticks", ticker.isExpired());
    }

    @Test
    public void shouldNotGoBelowZero_whenTickIsCalledMultipleTimes() {
        Ticker ticker = new Ticker(1);
        ticker.tick();

        ticker.tick();
        assertTrue("Ticker should remain expired without error (not go below 0)", ticker.isExpired());
    }
}
