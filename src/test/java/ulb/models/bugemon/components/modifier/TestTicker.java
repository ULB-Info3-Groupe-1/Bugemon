package ulb.models.bugemon.components.modifier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestTicker {
    @Test
    public void testExpiry() {
        Ticker ticker = new Ticker(1);

        assertFalse(ticker.isExpired());

        ticker.tick();

        assertTrue(ticker.isExpired());
    }

    @Test
    public void testZeroDuration() {
        Ticker ticker = new Ticker(0);
        assertTrue(ticker.isExpired()); // should be expired immediately
    }

    @Test
    public void testNegativeDuration() {
        Ticker ticker = new Ticker(-1);
        assertTrue(ticker.isExpired()); // should be expired immediately
    }

    @Test
    public void testMultipleTicks() {
        Ticker ticker = new Ticker(3);

        assertFalse(ticker.isExpired());

        ticker.tick();
        assertFalse(ticker.isExpired()); // 2 remaining

        ticker.tick();
        assertFalse(ticker.isExpired()); // 1 remaining

        ticker.tick();
        assertTrue(ticker.isExpired()); // 0 remaining
    }

    @Test
    public void testTickBeyondExpiry() {
        Ticker ticker = new Ticker(1);

        ticker.tick();
        assertTrue(ticker.isExpired());

        ticker.tick();
        assertTrue(ticker.isExpired());

        ticker.tick();
        assertTrue(ticker.isExpired());
    }

    @Test
    public void testLongDuration() {
        Ticker ticker = new Ticker(100);

        for (int i = 0; i < 99; i++) {
            assertFalse(ticker.isExpired());
            ticker.tick();
        }

        assertFalse(ticker.isExpired()); // still not expired at 99 ticks

        ticker.tick(); // 100th tick
        assertTrue(ticker.isExpired());
    }
}
