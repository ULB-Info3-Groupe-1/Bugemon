package ulb.models.bugemon;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestEfficiency {

    @Test
    public void testEfficiencyNeutralSameType() {
        Attack aquaAttack = new Attack("aqua", null, BugemonType.AQUA, null, 0, null);
        Bugemon aquaBugemon2 = new BugemonBuilder().name("aqua2").type(BugemonType.AQUA).build();
        assertEquals(Efficiency.NEUTRAL, aquaAttack.getEfficiencyAgainst(aquaBugemon2));
    }

    @Test
    public void testEfficiencyNeutralDifferentType() {
        Attack aquaAttack = new Attack("aqua", null, BugemonType.AQUA, null, 0, null);
        Bugemon lithoBugemon = new BugemonBuilder().name("litho").type(BugemonType.LITHO).build();
        assertEquals(Efficiency.NEUTRAL, aquaAttack.getEfficiencyAgainst(lithoBugemon));
    }

    @Test
    public void testEfficiencyLow() {
        Attack aquaAttack = new Attack("aqua", null, BugemonType.AQUA, null, 0, null);
        Bugemon floraBugemon = new BugemonBuilder().name("flora").type(BugemonType.FLORA).build();
        assertEquals(Efficiency.LOW, aquaAttack.getEfficiencyAgainst(floraBugemon));
    }

    @Test
    public void testEfficiencyHigh() {
        Attack aquaAttack = new Attack("aqua", null, BugemonType.AQUA, null, 0, null);
        Bugemon pyroBugemon = new BugemonBuilder().name("pyro").type(BugemonType.PYRO).build();
        assertEquals(Efficiency.HIGH, aquaAttack.getEfficiencyAgainst(pyroBugemon));
    }

}
