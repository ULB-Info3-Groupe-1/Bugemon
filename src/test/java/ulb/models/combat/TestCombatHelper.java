/**
 * Unit tests for {@link CombatHelper}.
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

package ulb.models.combat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.AttackList;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Bugemon.BType;
import ulb.models.bugemon.Stats;
import ulb.models.combat.CombatHelper.Efficiency;
import ulb.models.trainer.Trainer;
import ulb.utils.TestUtilsBugemons;
import ulb.utils.TestUtilsTrainer;

public class TestCombatHelper {

    @Test
    public void testPriority() {
        Trainer fasterTrainer = TestUtilsTrainer.createDefaultTrainer();
        Trainer slowTrainer = TestUtilsTrainer.createDefaultTrainer();

        int fastInitiative = fasterTrainer.getCurrentBugemonInitiative();
        Bugemon slowBugemon = slowTrainer.getCurrentBugemon();
        Stats slowBugemonStat = slowBugemon.getStats();

        slowBugemonStat.setInitiative(fastInitiative - 1);

        assertEquals(
            fasterTrainer,
            CombatHelper.attackPriority(fasterTrainer, slowTrainer)
        );
    }

    @Test
    public void testDamageApplied() {
        Bugemon striker = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon defender = TestUtilsBugemons.createDefaultBugemon("2");
        defender.setType(Bugemon.BType.PYRO);

        AttackList attackList = striker.getAttackList();
        Attack strikerAttack = attackList.get(0);

        double expectedDamage =
            strikerAttack.getPower() *
            ((100.0 + striker.getStats().getAttack()) / 100.0) *
            (100.0 / (100.0 + defender.getStats().getDefense())) *
            CombatHelper.getEfficiencyFactor(strikerAttack, defender.getType());

        double damage = CombatHelper.calculateDamage(
            strikerAttack,
            striker.getStats(),
            defender.getStats(),
            defender.getType()
        );

        assertEquals(expectedDamage, damage, expectedDamage / 2.0);
    }

    @Test
    public void testDamageMultiplicatorHigh() {
        Bugemon striker = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon defender = TestUtilsBugemons.createDefaultBugemon("2");

        defender.setType(Bugemon.BType.PYRO);

        AttackList attackList = striker.getAttackList();
        Attack strikerAttack = attackList.get(0);

        double neutralDamage = CombatHelper.calculateDamage(
            strikerAttack,
            striker.getStats(),
            defender.getStats(),
            defender.getType()
        );

        defender = TestUtilsBugemons.createDefaultBugemon("2");
        defender.setType(Bugemon.BType.AQUA);

        double highDamage = CombatHelper.calculateDamage(
            strikerAttack,
            striker.getStats(),
            defender.getStats(),
            defender.getType()
        );

        assertTrue(neutralDamage < highDamage);
    }

    @Test
    public void testDamageMultiplicatorLow() {
        Bugemon striker = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon defender = TestUtilsBugemons.createDefaultBugemon("2");

        defender.setType(Bugemon.BType.PYRO);

        AttackList attackList = striker.getAttackList();
        Attack strikerAttack = attackList.get(0);

        double neutralDamage = CombatHelper.calculateDamage(
            strikerAttack,
            striker.getStats(),
            defender.getStats(),
            defender.getType()
        );

        defender = TestUtilsBugemons.createDefaultBugemon("2");
        defender.setType(Bugemon.BType.LITHO);

        double lowDamage = CombatHelper.calculateDamage(
            strikerAttack,
            striker.getStats(),
            defender.getStats(),
            defender.getType()
        );

        assertTrue(lowDamage < neutralDamage);
    }

    @Test
    public void testEfficiencyNeutralSameType() {
        BType aquaType1 = BType.AQUA;
        BType aquaType2 = BType.AQUA;
        assertEquals(
            Efficiency.NEUTRAL,
            CombatHelper.compareBType(aquaType1, aquaType2)
        );
    }

    @Test
    public void testEfficiencyNeutralDifferentType() {
        BType aquaType = BType.AQUA;
        BType lithoType = BType.LITHO;

        assertEquals(
            Efficiency.NEUTRAL,
            CombatHelper.compareBType(aquaType, lithoType)
        );
    }

    @Test
    public void testEfficiencyLow() {
        BType aquaType = BType.AQUA;
        BType floraType = BType.FLORA;

        assertEquals(
            Efficiency.LOW,
            CombatHelper.compareBType(aquaType, floraType)
        );
    }

    @Test
    public void testEfficiencyHigh() {
        BType aquaType = BType.AQUA;
        BType pyroType = BType.PYRO;

        assertEquals(
            Efficiency.HIGH,
            CombatHelper.compareBType(aquaType, pyroType)
        );
    }
}
