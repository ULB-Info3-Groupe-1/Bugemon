package ulb.models.bugemon;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestEfficiency {
    

    @Test
    public void testEfficiencyNeutralSameType() {
        Bugemon aquaBugemon1 = new BugemonBuilder().name("aqua1").type(BugemonType.AQUA).build();
        Bugemon aquaBugemon2 = new BugemonBuilder().name("aqua2").type(BugemonType.AQUA).build();
        assertEquals(Efficiency.NEUTRAL, aquaBugemon1.getEfficiencyAgainst(aquaBugemon2));
    }

    @Test
    public void testEfficiencyNeutralDifferentType() {
        Bugemon aquaBugemon = new BugemonBuilder().name("aqua").type(BugemonType.AQUA).build();
        Bugemon lithoBugemon = new BugemonBuilder().name("litho").type(BugemonType.LITHO).build();
        assertEquals(Efficiency.NEUTRAL, aquaBugemon.getEfficiencyAgainst(lithoBugemon));
    }

    @Test
    public void testEfficiencyLow() {
        Bugemon aquaBugemon = new BugemonBuilder().name("aqua").type(BugemonType.AQUA).build();
        Bugemon floraBugemon = new BugemonBuilder().name("flora").type(BugemonType.FLORA).build();
        assertEquals(Efficiency.LOW, aquaBugemon.getEfficiencyAgainst(floraBugemon));
    }

    @Test
    public void testEfficiencyHigh() {
        Bugemon aquaBugemon = new BugemonBuilder().name("aqua").type(BugemonType.AQUA).build();
        Bugemon pyroBugemon = new BugemonBuilder().name("pyro").type(BugemonType.PYRO).build();
        assertEquals(Efficiency.HIGH, aquaBugemon.getEfficiencyAgainst(pyroBugemon));
    }

}
