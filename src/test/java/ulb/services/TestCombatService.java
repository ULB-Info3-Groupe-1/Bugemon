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

import ulb.factories.TeamFactory;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon_team.Team;
import ulb.models.skills.Skill;
import ulb.models.skills.SkillBuilder;
import ulb.models.skills.SkillEffect.CritBonusEffect;
import ulb.models.skills.SkillEffect.StatBonusEffect;
import ulb.models.skills.SkillEffect.TypeMultiplierEffect;
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

        Team slowTeam = new Team();
        slowTeam.add(slowBugemon);

        Team fastTeam = new Team();
        fastTeam.add(fastBugemon);

        Trainer fasterTrainer = new AutoTrainer(fastTeam);
        Trainer slowTrainer = new AutoTrainer(slowTeam);

        assertEquals(fasterTrainer, CombatService.attackPriority(fasterTrainer, slowTrainer));
    }

    @Test
    public void testDamageApplied() {
        CombatService combatService = new CombatService();
        Attack attack = new Attack("1", "", BugemonType.FLORA, "", 30, new ArrayList<Effect>());

        List<Attack> attacks = List.of(attack, TestUtilsBugemons.createAttack("2", BugemonType.FLORA, 0),
                TestUtilsBugemons.createAttack("3", BugemonType.FLORA, 0));
        Bugemon striker = new BugemonBuilder().name("1").attack(50).defense(30).attackList(attacks).build();
        Bugemon defender = new BugemonBuilder().name("2").attack(20).defense(20).attackList(attacks)
                .type(BugemonType.PYRO).build();

        double expectedDamage = attack.power() * ((100.0 + striker.getAttack()) / 100.0)
                * (100.0 / (100.0 + defender.getDefense())) * CombatService.getEfficiencyFactor(attack, defender);

        int damage = combatService.calculateDamage(attack, striker, defender, 1.0);
        assertEquals(expectedDamage, damage, expectedDamage / 2.0);
    }

    @Test
    public void testDamageMultiplicatorHigh() {
        CombatService combatService = new CombatService();
        Attack attack = new Attack("1", "", BugemonType.FLORA, "", 30, new ArrayList<Effect>());

        List<Attack> attacks = List.of(attack, TestUtilsBugemons.createAttack("2", BugemonType.FLORA, 0),
                TestUtilsBugemons.createAttack("3", BugemonType.FLORA, 0));
        Bugemon striker = new BugemonBuilder().name("1").attack(50).defense(30).attackList(attacks).build();
        Bugemon defender = new BugemonBuilder().name("2").attack(20).defense(20).attackList(attacks)
                .type(BugemonType.PYRO).build();

        double neutralDamage = combatService.calculateDamage(attack, striker, defender, 1.0);

        defender = new BugemonBuilder().name("2").attack(20).defense(20).attackList(attacks).type(BugemonType.AQUA)
                .build();

        double highDamage = combatService.calculateDamage(attack, striker, defender, 1.0);

        assertTrue(neutralDamage < highDamage);
    }

    @Test
    public void testDamageMultiplicatorLow() {
        CombatService combatService = new CombatService();
        Attack attack = new Attack("1", "", BugemonType.FLORA, "", 30, new ArrayList<Effect>());

        List<Attack> attacks = List.of(attack, TestUtilsBugemons.createAttack("2", BugemonType.FLORA, 0),
                TestUtilsBugemons.createAttack("3", BugemonType.FLORA, 0));
        Bugemon striker = new BugemonBuilder().name("1").attack(50).defense(30).attackList(attacks).build();
        Bugemon defender = new BugemonBuilder().name("2").attack(20).defense(20).attackList(attacks)
                .type(BugemonType.PYRO).build();

        double neutralDamage = combatService.calculateDamage(attack, striker, defender, 1.0);

        defender = new BugemonBuilder().name("2").attack(20).defense(20).attackList(attacks).type(BugemonType.LITHO)
                .build();

        double lowDamage = combatService.calculateDamage(attack, striker, defender, 1.0);

        assertTrue(lowDamage < neutralDamage);
    }

    @Test
    public void testRandomTeamNumber() {
        List<Bugemon> bugemons = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            bugemons.add(TestUtilsBugemons.createDefaultBugemon(String.valueOf(i)));
        }
        Team teamOfSix = TeamFactory.createRandomTeam(bugemons, 6);
        assertEquals(6, teamOfSix.size());
    }

    // ── Skill effects ─────────────────────────────────────────────────────────

    @Test
    public void createUniqueCombat_shouldApplyStatBonusSkillsToPlayerTeam() {
        // Plus besoin de mocker SkillService, on passe directement la liste à la méthode
        Skill statBonusSkill = new SkillBuilder().effect(new StatBonusEffect(EffectStat.ATTACK, 10)).build();
        List<Skill> skills = List.of(statBonusSkill);

        CombatService combatService = new CombatService();

        Team playerTeam = TestUtilsBugemons.createDefaultTeam(3);
        int initialAttack = playerTeam.getFirst().getAttack();
        Trainer playerTrainer = new AutoTrainer(playerTeam);
        Trainer opponentTrainer = new AutoTrainer(TestUtilsBugemons.createDefaultTeam(1));

        // On passe la liste des skills en premier paramètre
        combatService.createUniqueCombat(skills, playerTrainer, opponentTrainer);

        for (Bugemon b : playerTeam) {
            assertEquals(initialAttack + 10, b.getAttack());
        }
    }

    @Test
    public void calculateDamage_shouldApplyTypeMultiplier_whenPlayerAttacksWithMatchingType() {
        Skill typeBoostSkill = new SkillBuilder().effect(new TypeMultiplierEffect(BugemonType.FLORA, 2.0)).build();
        List<Skill> skills = List.of(typeBoostSkill);
        List<Skill> emptySkills = List.of();

        CombatService combatService = new CombatService();

        Attack floraAttack = new Attack("1", "", BugemonType.FLORA, "", 30, new ArrayList<Effect>());
        List<Attack> attacks = List.of(floraAttack, TestUtilsBugemons.createAttack("2", BugemonType.FLORA, 0),
                TestUtilsBugemons.createAttack("3", BugemonType.FLORA, 0));
        Bugemon striker = new BugemonBuilder().name("1").attack(50).defense(30).attackList(attacks).build();
        Bugemon defender = new BugemonBuilder().name("2").attack(20).defense(20).attackList(attacks)
                .type(BugemonType.PYRO).build();

        for (int i = 0; i < 50; i++) {
            int boosted = combatService.calculateDamage(skills, floraAttack, striker, defender, true);
            int raw = combatService.calculateDamage(emptySkills, floraAttack, striker, defender, false);
            assertTrue("boosted=" + boosted + " should be > raw=" + raw, boosted > raw);
        }
    }

    @Test
    public void calculateDamage_shouldNotApplyTypeMultiplier_whenAttackTypeDoesNotMatch() {
        Skill pyroBoost = new SkillBuilder().effect(new TypeMultiplierEffect(BugemonType.PYRO, 2.0)).build();
        List<Skill> skills = List.of(pyroBoost);

        CombatService combatService = new CombatService();

        Attack floraAttack = new Attack("1", "", BugemonType.FLORA, "", 30, new ArrayList<Effect>());
        List<Attack> attacks = List.of(floraAttack, TestUtilsBugemons.createAttack("2", BugemonType.FLORA, 0),
                TestUtilsBugemons.createAttack("3", BugemonType.FLORA, 0));
        Bugemon striker = new BugemonBuilder().name("1").attack(50).defense(30).attackList(attacks).build();
        Bugemon defender = new BugemonBuilder().name("2").attack(20).defense(20).attackList(attacks)
                .type(BugemonType.PYRO).build();

        int playerDamage = combatService.calculateDamage(floraAttack, striker, defender, 1.0);
        int boosted = combatService.calculateDamage(skills, floraAttack, striker, defender, true);

        assertTrue(boosted >= playerDamage);
        assertTrue(boosted <= (int) Math.ceil(playerDamage * 1.5));
    }

    @Test
    public void calculateDamage_shouldAlwaysCrit_whenCritBonusGuaranteesIt() {
        Skill maxCritSkill = new SkillBuilder().effect(new CritBonusEffect(1.0)).build();
        List<Skill> skills = List.of(maxCritSkill);

        CombatService combatService = new CombatService();

        Attack attack = new Attack("1", "", BugemonType.FLORA, "", 30, new ArrayList<Effect>());
        List<Attack> attacks = List.of(attack, TestUtilsBugemons.createAttack("2", BugemonType.FLORA, 0),
                TestUtilsBugemons.createAttack("3", BugemonType.FLORA, 0));
        Bugemon striker = new BugemonBuilder().name("1").attack(50).defense(30).attackList(attacks).build();
        Bugemon defender = new BugemonBuilder().name("2").attack(20).defense(20).attackList(attacks)
                .type(BugemonType.PYRO).build();

        int critDamage = combatService.calculateDamage(attack, striker, defender, 1.5);
        for (int i = 0; i < 50; i++) {
            int actual = combatService.calculateDamage(skills, attack, striker, defender, true);
            assertEquals(critDamage, actual);
        }
    }
}
