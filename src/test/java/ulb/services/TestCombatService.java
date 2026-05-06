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
        Bugemon slowBugemon = new BugemonBuilder().name("1").initiative(0)
                .attackList(TestUtilsBugemons.createDefaultAttackList(BugemonType.FLORA)).build();

        Bugemon fastBugemon = new BugemonBuilder().name("2").initiative(1000)
                .attackList(TestUtilsBugemons.createDefaultAttackList(BugemonType.FLORA)).build();

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

        List<Attack> attacks = List.of(attack, TestUtilsBugemons.createAttack("2", BugemonType.FLORA, 0),
                TestUtilsBugemons.createAttack("3", BugemonType.FLORA, 0));
        Bugemon striker = new BugemonBuilder().name("1").attack(50).defense(30).attackList(attacks).build();
        Bugemon defender = new BugemonBuilder().name("2").attack(20).defense(20).attackList(attacks)
                .type(BugemonType.PYRO).build();

        double expectedDamage = attack.power() * ((100.0 + striker.getAttack()) / 100.0)
                * (100.0 / (100.0 + defender.getDefense())) * CombatService.getEfficiencyFactor(attack, defender);

        int damage = CombatService.calculateDamage(attack, striker, defender, 1.0);

        assertEquals(expectedDamage, damage, expectedDamage / 2.0);
    }

    @Test
    public void testDamageMultiplicatorHigh() {
        Attack attack = new Attack("1", "", BugemonType.FLORA, "", 30, new ArrayList<Effect>());

        List<Attack> attacks = List.of(attack, TestUtilsBugemons.createAttack("2", BugemonType.FLORA, 0),
                TestUtilsBugemons.createAttack("3", BugemonType.FLORA, 0));
        Bugemon striker = new BugemonBuilder().name("1").attack(50).defense(30).attackList(attacks).build();
        Bugemon defender = new BugemonBuilder().name("2").attack(20).defense(20).attackList(attacks)
                .type(BugemonType.PYRO).build();

        double neutralDamage = CombatService.calculateDamage(attack, striker, defender, 1.0);

        defender = new BugemonBuilder().name("2").attack(20).defense(20).attackList(attacks).type(BugemonType.AQUA)
                .build();

        double highDamage = CombatService.calculateDamage(attack, striker, defender, 1.0);

        assertTrue(neutralDamage < highDamage);
    }

    @Test
    public void testDamageMultiplicatorLow() {
        Attack attack = new Attack("1", "", BugemonType.FLORA, "", 30, new ArrayList<Effect>());

        List<Attack> attacks = List.of(attack, TestUtilsBugemons.createAttack("2", BugemonType.FLORA, 0),
                TestUtilsBugemons.createAttack("3", BugemonType.FLORA, 0));
        Bugemon striker = new BugemonBuilder().name("1").attack(50).defense(30).attackList(attacks).build();
        Bugemon defender = new BugemonBuilder().name("2").attack(20).defense(20).attackList(attacks)
                .type(BugemonType.PYRO).build();

        double neutralDamage = CombatService.calculateDamage(attack, striker, defender, 1.0);

        defender = new BugemonBuilder().name("2").attack(20).defense(20).attackList(attacks).type(BugemonType.LITHO)
                .build();

        double lowDamage = CombatService.calculateDamage(attack, striker, defender, 1.0);

        assertTrue(lowDamage < neutralDamage);
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
