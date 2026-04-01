/**
 * Unit tests for {@link CombatService}.
 *
 * <p>Covers attack priority resolution, damage computation, and type
 * effectiveness calculations.</p>
 *
 * @author  Matteo Morbée
 * @author Lucas Verbeiren
 * @author Martin Gouverneur
 * @version 1.0
 * @date    05 mar. 2026
 */

package ulb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ulb.common.Efficiency;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.utils.test.TestUtilsBugemons;

public class TestCombatService {
    @Test
    public void testPriority() {
        Bugemon slowBugemon = new BugemonBuilder().id("1").initiative(0).build();

        Bugemon fastBugemon = new BugemonBuilder().id("2").initiative(1000).build();

        BugemonTeam slowTeam = new BugemonTeam();
        slowTeam.add(slowBugemon);

        BugemonTeam fastTeam = new BugemonTeam();
        fastTeam.add(fastBugemon);

        Trainer fasterTrainer = new AutoTrainer(fastTeam);
        Trainer slowTrainer = new AutoTrainer(slowTeam);

        assertEquals(fasterTrainer, CombatService.attackPriority(fasterTrainer, slowTrainer));
    }

    @Test
    public void testDamageApplied() {
        Attack attack = new Attack("1", "", BugemonType.FLORA, "", 30, new ArrayList<Effect>());

        Bugemon striker = new BugemonBuilder().id("1").attack(50).defense(30).addAttack(attack).build();
        Bugemon defender = new BugemonBuilder().id("2").attack(20).defense(20).addAttack(attack).type(BugemonType.PYRO)
                .build();

        double expectedDamage = attack.power() * ((100.0 + striker.getAttack()) / 100.0)
                * (100.0 / (100.0 + defender.getDefense()))
                * CombatService.getEfficiencyFactor(attack, defender.getType());

        int damage = CombatService.calculateDamage(attack, striker, defender, 1.0);

        assertEquals(expectedDamage, damage, expectedDamage / 2.0);
    }

    @Test
    public void testDamageMultiplicatorHigh() {
        Attack attack = new Attack("1", "", BugemonType.FLORA, "", 30, new ArrayList<Effect>());

        Bugemon striker = new BugemonBuilder().id("1").attack(50).defense(30).addAttack(attack).build();
        Bugemon defender = new BugemonBuilder().id("2").attack(20).defense(20).addAttack(attack).type(BugemonType.PYRO)
                .build();

        double neutralDamage = CombatService.calculateDamage(attack, striker, defender, 1.0);

        defender = new BugemonBuilder().id("2").attack(20).defense(20).addAttack(attack).type(BugemonType.AQUA).build();

        double highDamage = CombatService.calculateDamage(attack, striker, defender, 1.0);

        assertTrue(neutralDamage < highDamage);
    }

    @Test
    public void testDamageMultiplicatorLow() {
        Attack attack = new Attack("1", "", BugemonType.FLORA, "", 30, new ArrayList<Effect>());

        Bugemon striker = new BugemonBuilder().id("1").attack(50).defense(30).addAttack(attack).build();
        Bugemon defender = new BugemonBuilder().id("2").attack(20).defense(20).addAttack(attack).type(BugemonType.PYRO)
                .build();

        double neutralDamage = CombatService.calculateDamage(attack, striker, defender, 1.0);

        defender = new BugemonBuilder().id("2").attack(20).defense(20).addAttack(attack).type(BugemonType.LITHO)
                .build();

        double lowDamage = CombatService.calculateDamage(attack, striker, defender, 1.0);

        assertTrue(lowDamage < neutralDamage);
    }

    @Test
    public void testEfficiencyNeutralSameType() {
        BugemonType aquaType1 = BugemonType.AQUA;
        BugemonType aquaType2 = BugemonType.AQUA;
        assertEquals(Efficiency.NEUTRAL, CombatService.compareBugemonType(aquaType1, aquaType2));
    }

    @Test
    public void testEfficiencyNeutralDifferentType() {
        BugemonType aquaType = BugemonType.AQUA;
        BugemonType lithoType = BugemonType.LITHO;

        assertEquals(Efficiency.NEUTRAL, CombatService.compareBugemonType(aquaType, lithoType));
    }

    @Test
    public void testEfficiencyLow() {
        BugemonType aquaType = BugemonType.AQUA;
        BugemonType floraType = BugemonType.FLORA;

        assertEquals(Efficiency.LOW, CombatService.compareBugemonType(aquaType, floraType));
    }

    @Test
    public void testEfficiencyHigh() {
        BugemonType aquaType = BugemonType.AQUA;
        BugemonType pyroType = BugemonType.PYRO;

        assertEquals(Efficiency.HIGH, CombatService.compareBugemonType(aquaType, pyroType));
    }

    @Test
    public void testRandomTeamNumber() {
        List<Bugemon> bugemons = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            bugemons.add(TestUtilsBugemons.createDefaultBugemon(String.valueOf(i)));
        }
        BugemonTeam teamOfSix = CombatService.createRandomTeam(bugemons, 6);
        assertEquals(6, teamOfSix.size());
    }
}
