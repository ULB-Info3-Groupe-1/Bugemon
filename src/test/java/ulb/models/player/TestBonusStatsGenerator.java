package ulb.models.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import ulb.Configuration;

public class TestBonusStatsGenerator {

    @Test
    public void generatedBonusStatsShouldAlwaysTotalConfiguredPoints() {
        BonusStatsGenerator generator = new BonusStatsGenerator(new Random(0));

        for (int i = 0; i < 5; i++) {
            BonusStats bonus = generator.generateBonusStats();

            int hp = bonus.getBonusHp();
            int attack = bonus.getBonusAttack();
            int defense = bonus.getBonusDefense();
            int initiative = bonus.getBonusInitiative();

            assertTrue(hp >= 0 && attack >= 0 && defense >= 0 && initiative >= 0);

            assertEquals(0, hp % Configuration.Game.HP_GAIN_PER_POINT);
            assertEquals(0, attack % Configuration.Game.ATTACK_GAIN_PER_POINT);
            assertEquals(0, defense % Configuration.Game.DEFENSE_GAIN_PER_POINT);
            assertEquals(0, initiative % Configuration.Game.INITIATIVE_GAIN_PER_POINT);

            int pointsHp = hp / Configuration.Game.HP_GAIN_PER_POINT;
            int pointsAttack = attack / Configuration.Game.ATTACK_GAIN_PER_POINT;
            int pointsDefense = defense / Configuration.Game.DEFENSE_GAIN_PER_POINT;
            int pointsInitiative = initiative / Configuration.Game.INITIATIVE_GAIN_PER_POINT;

            int totalPoints = pointsHp + pointsAttack + pointsDefense + pointsInitiative;
            assertEquals(Configuration.Game.NUM_POINTS_PER_BONUS, totalPoints);
        }
    }
}
