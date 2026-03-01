package ulb.models.level_up;

import ulb.common.BugemonDTO;
import ulb.common.LevelUpDTO;
import ulb.models.bugemon.Bugemon;

public class LevelUp implements LevelUpDTO {

    Bugemon bugemon;

    public LevelUp(Bugemon bugemon) {
        this.bugemon = bugemon;
    }

    @Override
    public BugemonDTO getBugemon() {
        return this.bugemon;
    }

}
