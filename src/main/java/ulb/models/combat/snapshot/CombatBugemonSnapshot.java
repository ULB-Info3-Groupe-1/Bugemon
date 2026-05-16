package ulb.models.combat.snapshot;

import java.util.List;
import ulb.models.bugemon.Attack;

public record CombatBugemonSnapshot(int currentHp, List<Attack> attacks, int maxHp, int effectiveAttack,
        int effectiveDefense, int initiative) {
    public boolean isAlive() {
        return this.currentHp > 0;
    }
}
