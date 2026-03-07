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

import java.util.ArrayList;
import org.junit.Test;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Bugemon.BType;
import ulb.models.bugemon.Effect;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.combat.CombatHelper.Efficiency;
import ulb.models.trainer.Trainer;

public class TestCombatHelper {

    @Test
    public void testPriority() {
        Bugemon slowBugemon = new Bugemon.Builder()
            .id("1")
            .initiative(0)
            .build();

        Bugemon fastBugemon = new Bugemon.Builder()
            .id("2")
            .initiative(1000)
            .build();

        BugemonTeam slowTeam = new BugemonTeam();
        slowTeam.addBugemon(slowBugemon);

        BugemonTeam fastTeam = new BugemonTeam();
        fastTeam.addBugemon(fastBugemon);

        Trainer fasterTrainer = new Trainer(fastTeam);
        Trainer slowTrainer = new Trainer(slowTeam);

        assertEquals(
            fasterTrainer,
            CombatHelper.attackPriority(fasterTrainer, slowTrainer)
        );
    }

    @Test
    public void testDamageApplied() {
        Attack attack = new Attack(
            "1",
            "",
            Bugemon.BType.FLORA,
            "",
            30,
            new ArrayList<Effect>()
        );

        Bugemon striker = new Bugemon.Builder()
            .id("1")
            .attack(50)
            .defense(30)
            .addAttack(attack)
            .build();
        Bugemon defender = new Bugemon.Builder()
            .id("2")
            .attack(20)
            .defense(20)
            .addAttack(attack)
            .type(Bugemon.BType.PYRO)
            .build();

        double expectedDamage =
            attack.getPower() *
            ((100.0 + striker.getAttack()) / 100.0) *
            (100.0 / (100.0 + defender.getDefense())) *
            CombatHelper.getEfficiencyFactor(attack, defender.getType());

        double damage = CombatHelper.calculateDamage(attack, striker, defender);

        assertEquals(expectedDamage, damage, expectedDamage / 2.0);
    }

    @Test
    public void testDamageMultiplicatorHigh() {
        Attack attack = new Attack(
            "1",
            "",
            Bugemon.BType.FLORA,
            "",
            30,
            new ArrayList<Effect>()
        );

        Bugemon striker = new Bugemon.Builder()
            .id("1")
            .attack(50)
            .defense(30)
            .addAttack(attack)
            .build();
        Bugemon defender = new Bugemon.Builder()
            .id("2")
            .attack(20)
            .defense(20)
            .addAttack(attack)
            .type(Bugemon.BType.PYRO)
            .build();

        double neutralDamage = CombatHelper.calculateDamage(
            attack,
            striker,
            defender
        );

        defender = new Bugemon.Builder()
            .id("2")
            .attack(20)
            .defense(20)
            .addAttack(attack)
            .type(Bugemon.BType.AQUA)
            .build();

        double highDamage = CombatHelper.calculateDamage(
            attack,
            striker,
            defender
        );

        assertTrue(neutralDamage < highDamage);
    }

    @Test
    public void testDamageMultiplicatorLow() {
        Attack attack = new Attack(
            "1",
            "",
            Bugemon.BType.FLORA,
            "",
            30,
            new ArrayList<Effect>()
        );

        Bugemon striker = new Bugemon.Builder()
            .id("1")
            .attack(50)
            .defense(30)
            .addAttack(attack)
            .build();
        Bugemon defender = new Bugemon.Builder()
            .id("2")
            .attack(20)
            .defense(20)
            .addAttack(attack)
            .type(Bugemon.BType.PYRO)
            .build();

        double neutralDamage = CombatHelper.calculateDamage(
            attack,
            striker,
            defender
        );

        defender = new Bugemon.Builder()
            .id("2")
            .attack(20)
            .defense(20)
            .addAttack(attack)
            .type(Bugemon.BType.LITHO)
            .build();

        double lowDamage = CombatHelper.calculateDamage(
            attack,
            striker,
            defender
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
