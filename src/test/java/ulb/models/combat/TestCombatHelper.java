package ulb.models.combat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.Test;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.AttackList;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Effect;
import ulb.models.bugemon.EffectType;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.TestUtilsBugemonTeam;
import ulb.utils.TestUtilsBugemons;

public class TestCombatHelper {

    @Test
    public void testDamageApplied() {
        Bugemon striker = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon defender = TestUtilsBugemons.createDefaultBugemon("2");
        defender.setType(Bugemon.BType.PYRO);

        AttackList attackList = striker.getAttackList();
        Attack strikerAttack = attackList.get(0);

        int expectedDamage =
            strikerAttack.getPower() *
            ((100 + striker.getStats().getAttack()) / 100) *
            ((100 / 100 + defender.getStats().getDefense()));

        double damage = CombatHelper.calculateDamage(strikerAttack, defender);

        assertEquals(expectedDamage, damage, 0.1);
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
            defender
        );

        defender = TestUtilsBugemons.createDefaultBugemon("2");
        defender.setType(Bugemon.BType.AQUA);

        double highDamage = CombatHelper.calculateDamage(
            strikerAttack,
            defender
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
            defender
        );

        defender = TestUtilsBugemons.createDefaultBugemon("2");
        defender.setType(Bugemon.BType.LITHO);

        double lowDamage = CombatHelper.calculateDamage(
            strikerAttack,
            defender
        );

        assertTrue(neutralDamage < lowDamage);
    }

    @Test
    public void testEffectOnStriker() {
        Bugemon striker = TestUtilsBugemons.createDefaultBugemon("1");

        List<Effect> effects = new ArrayList<Effect>();
        effects.add(
            new Effect(
                EffectType.STAT_MODIFIER,
                "lanceur",
                "defense",
                5,
                "permanent"
            )
        );

        Attack attack = new Attack(
            "0",
            "onStrikerEffect",
            Bugemon.BType.AQUA,
            "",
            0,
            effects
        );

        int initialDefense = striker.getStats().getDefense();
        CombatHelper.applyEffect(attack, striker);
        int currentDefense = striker.getStats().getDefense();

        assertTrue(initialDefense < currentDefense);
    }

    @Test
    public void testEffectOnDefender() {
        Bugemon defender = TestUtilsBugemons.createDefaultBugemon("1");

        List<Effect> effects = new ArrayList<Effect>();
        effects.add(
            new Effect(
                EffectType.STAT_MODIFIER,
                "adversaire",
                "defense",
                -5,
                "permanent"
            )
        );

        Attack attack = new Attack(
            "0",
            "onDefenderEffect",
            Bugemon.BType.AQUA,
            "",
            0,
            effects
        );

        int initialDefense = defender.getStats().getDefense();
        CombatHelper.applyEffect(attack, defender);
        int currentDefense = defender.getStats().getDefense();

        assertTrue(initialDefense > currentDefense);
    }

    @Test
    public void testHealEffectOnTeam() {
        BugemonTeam bugemonTeam = TestUtilsBugemonTeam.createDefaultBugemonTeam(
            true
        );

        List<Effect> effects = new ArrayList<Effect>();
        effects.add(new Effect(EffectType.SOIN, "equipe", "", 10, ""));

        Attack attack = new Attack(
            "0",
            "onTeamEffect",
            Bugemon.BType.AQUA,
            "",
            0,
            effects
        );

        CombatHelper.applyEffect(attack, bugemonTeam);
        List<Bugemon> bugemonTeamList = bugemonTeam.getTeam();

        assertTrue(bugemonTeamList.stream().allMatch(Bugemon::isAlive));
    }

    @Test
    public void testStatEffectOnTeam() {
        BugemonTeam bugemonTeam = TestUtilsBugemonTeam.createDefaultBugemonTeam(
            true
        );

        List<Effect> effects = new ArrayList<Effect>();
        effects.add(
            new Effect(
                EffectType.STAT_MODIFIER,
                "equipe",
                "defense",
                5,
                "permanent"
            )
        );

        Attack attack = new Attack(
            "0",
            "onTeamEffect",
            Bugemon.BType.AQUA,
            "",
            0,
            effects
        );
        List<Bugemon> bugemonTeamList = bugemonTeam.getTeam();

        List<Integer> initialTeamDefense = new ArrayList<Integer>();
        for (Bugemon bugemon : bugemonTeamList) {
            initialTeamDefense.add(bugemon.getStats().getDefense());
        }

        CombatHelper.applyEffect(attack, bugemonTeam);

        List<Integer> currentTeamDefense = new ArrayList<Integer>();
        for (Bugemon bugemon : bugemonTeamList) {
            currentTeamDefense.add(bugemon.getStats().getDefense());
        }

        assertTrue(
            IntStream.range(0, initialTeamDefense.size()).allMatch(
                i -> initialTeamDefense.get(i) < currentTeamDefense.get(i)
            )
        );
    }
}
