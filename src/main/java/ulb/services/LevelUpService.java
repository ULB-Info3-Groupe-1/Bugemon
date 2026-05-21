package ulb.services;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import ulb.Configuration;
import ulb.models.player.BonusStats;
import ulb.models.player.BonusStatsGenerator;
import ulb.models.run.RunBugemon;
import ulb.repositories.BugemonRepository;

public class LevelUpService {
    String playername;
    BugemonRepository bugemonRepository;
    BonusStatsGenerator bonusStatsGenerator;

    public LevelUpService(String playername, BugemonRepository bugemonRepository, Random random) {
        this.playername = playername;
        this.bugemonRepository = bugemonRepository;
        this.bonusStatsGenerator = new BonusStatsGenerator(random);
    }

    public void applyLevelUp(RunBugemon bugemon, BonusStats bonus) {
        bugemon.applyUpgrade(bonus);
        this.bugemonRepository.save(bugemon.getPlayerBugemon().toDTO(this.playername));
    }

    public List<BonusStats> generateLevelUpOptions() {
        return IntStream.range(0, Configuration.Game.NUM_BONUS_PER_LEVEL_UP)
                .mapToObj(i -> this.bonusStatsGenerator.generateBonusStats()).toList();
    }
}
