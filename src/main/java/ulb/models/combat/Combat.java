package ulb.models.combat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Combat {
    private static final Logger LOG = LoggerFactory.getLogger(Combat.class);

    private final CombatTeam playerTeam;
    private final CombatTeam opponentTeam;

    private boolean finished;

    public Combat(CombatTeam playerTeam, CombatTeam opponentTeam) {
        this.playerTeam = playerTeam;
        this.opponentTeam = opponentTeam;
    }
}
