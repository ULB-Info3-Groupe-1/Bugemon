package ulb.models.no_tower.room;

import ulb.models.combat.Combat;

public class CombatRoom extends Room {

    private final Combat combat;
    private final boolean isBoss;

    public CombatRoom(Combat combat, boolean isBoss) {
        this.combat = combat;
        this.isBoss = isBoss;
    }

    public Combat getCombat() {
        return this.combat;
    }

    public boolean isBoss() {
        return this.isBoss;
    }
}
