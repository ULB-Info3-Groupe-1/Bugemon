package ulb.models.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestVec2 {

    @Test
    public void shouldReturnInterpolatedVector_whenLinearInterpolationIsCalled() {
        Vec2 a = new Vec2(0f, 0f);
        Vec2 b = new Vec2(10f, 20f);

        Vec2 result = Vec2.linearInterpolation(a, b, 0.5f);

        assertEquals(5f, result.x(), 0.001f);
        assertEquals(10f, result.y(), 0.001f);
    }
}
