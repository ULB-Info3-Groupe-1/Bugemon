package ulb.services;

import java.util.List;
import java.util.Random;

import ulb.models.level_up.LevelUpGenerator;
import ulb.models.player.BonusStats;
import ulb.models.run.RunBugemon;
import ulb.repositories.BugemonRepository;

public class LevelUpService {
    String playername;
    BugemonRepository bugemonRepository;
    LevelUpGenerator levelUpGenerator;

    public LevelUpService(String playername, BugemonRepository bugemonRepository, Random random) {
        this.playername = playername;
        this.bugemonRepository = bugemonRepository;
        this.levelUpGenerator = new LevelUpGenerator(random);
    }

    public void applyLevelUp(RunBugemon bugemon, BonusStats bonus) {
        bugemon.applyUpgrade(bonus);
        this.bugemonRepository.save(bugemon.getPlayerBugemon().toDTO(this.playername));
    }

    public List<BonusStats> generateLevelUpOptions() {
        return this.levelUpGenerator.generateOptions();
    }
}
