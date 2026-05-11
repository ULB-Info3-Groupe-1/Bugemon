package ulb.factories;

import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.models.combat.Combat;
import ulb.models.skills.Skill;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;

public class CombatFactory {

    private CombatFactory() {
        // Private constructor to prevent instantiation
    }

    public static Combat create(List<Bugemon> allBugemons, Trainer playerTrainer, List<Skill> skills, boolean isBoss) {
        // Later will handle the floor difficulty and boss ?
        AutoTrainer opponentTrainer = new AutoTrainer(
                isBoss ? TeamFactory.createRandomBossTeam(allBugemons, playerTrainer.getTeamSize())
                        : TeamFactory.createRandomTeam(allBugemons, playerTrainer.getTeamSize()));
        return new Combat(playerTrainer, opponentTrainer, skills);
    }

}
