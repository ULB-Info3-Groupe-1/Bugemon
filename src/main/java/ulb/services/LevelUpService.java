package ulb.services;

import ulb.models.level_up.Upgrade;
import ulb.models.run.RunBugemon;
import ulb.repositories.BugemonRepository;

public class LevelUpService {
    String playername;
    BugemonRepository bugemonRepository;

    public LevelUpService(String playername, BugemonRepository bugemonRepository) {
        this.bugemonRepository = bugemonRepository;
    }

    public void applyLevelUp(RunBugemon bugemon, Upgrade upgrade) {
        bugemon.applyUpgrade(upgrade);
        this.bugemonRepository.save(this.playername, bugemon.getPlayerBugemon().toDTO(this.playername));
    }
}
