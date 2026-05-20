package ulb.models.combat.snapshot;

import java.util.HashMap;
import java.util.Map;

import ulb.models.combat.CombatTeam;
import ulb.models.item.Item;

public record CombatSnapshot(TeamSnapshot playerTeam, TeamSnapshot aiTeam, Map<Item, Integer> aiInventory,
        Map<Item, Integer> opponentInventory) {

    public static CombatSnapshot fromAiPerspective(CombatTeam aiTeam, CombatTeam opponentTeam, Map<Item, Integer> aiInventory, Map<Item, Integer> opponentInventory) {
        if (opponentTeam == null || aiTeam == null) {
            throw new IllegalStateException("Both teams must be non-null to create a CombatSnapshot.");
        }
        return new CombatSnapshot(opponentTeam.getSnapshot(), aiTeam.getSnapshot(), aiInventory, opponentInventory);
    }

    public CombatBugemonSnapshot aiActive() {
        return this.aiTeam.active();
    }

    public CombatBugemonSnapshot opponentActive() {
        return this.playerTeam.active();
    }

    public boolean isAiDefeated() {
        for (CombatBugemonSnapshot bugemon : this.aiTeam.bugemons()) {
            if (bugemon.isAlive()) {
                return false;
            }
        }
        return true;
    }

    public boolean isOpponentDefeated() {
        for (CombatBugemonSnapshot bugemon : this.playerTeam.bugemons()) {
            if (bugemon.isAlive()) {
                return false;
            }
        }
        return true;
    }

}
