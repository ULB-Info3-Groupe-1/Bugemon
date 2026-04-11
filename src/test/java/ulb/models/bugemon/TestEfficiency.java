package ulb.models.bugemon;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;

import org.junit.Test;

public class TestEfficiency {

    @Test
    public void testEfficiencyNeutralSameType() {
        Bugemon attackerBugemon = new BugemonBuilder().name("attacker").build();
        Attack aquaAttack = new Attack("aqua", null, BugemonType.AQUA, null, 0, null);
        Bugemon aquaBugemon2 = new BugemonBuilder().name("aqua2").type(BugemonType.AQUA).build();
        assertEquals(Efficiency.NEUTRAL, attackerBugemon.getEfficiencyAgainst(aquaAttack.type(), aquaBugemon2));
    }

    @Test
    public void testEfficiencyNeutralDifferentType() {
        Bugemon attackerBugemon = new BugemonBuilder().name("attacker").build();
        Attack aquaAttack = new Attack("aqua", null, BugemonType.AQUA, null, 0, null);
        Bugemon lithoBugemon = new BugemonBuilder().name("litho").type(BugemonType.LITHO).build();
        assertEquals(Efficiency.NEUTRAL, attackerBugemon.getEfficiencyAgainst(aquaAttack.type(), lithoBugemon));
    }

    @Test
    public void testEfficiencyLow() {
        Bugemon attackerBugemon = new BugemonBuilder().name("attacker").build();
        Attack aquaAttack = new Attack("aqua", null, BugemonType.AQUA, null, 0, null);
        Bugemon floraBugemon = new BugemonBuilder().name("flora").type(BugemonType.FLORA).build();
        assertEquals(Efficiency.LOW, attackerBugemon.getEfficiencyAgainst(aquaAttack.type(), floraBugemon));
    }

    @Test
    public void testEfficiencyHigh() {
        Bugemon attackerBugemon = new BugemonBuilder().name("attacker").build();
        Attack aquaAttack = new Attack("aqua", null, BugemonType.AQUA, null, 0, null);
        Bugemon pyroBugemon = new BugemonBuilder().name("pyro").type(BugemonType.PYRO).build();
        assertEquals(Efficiency.HIGH, attackerBugemon.getEfficiencyAgainst(aquaAttack.type(), pyroBugemon));
    }

}
