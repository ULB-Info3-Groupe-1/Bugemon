package ulb.models.tower.room;

import ulb.models.combat.Combat;
import ulb.models.tower.utils.CombatFactory;
import ulb.models.trainer.Trainer;

public final class CombatRoom implements Room {
    private final int floorLevel;
    private final boolean isBoss;
    private final CombatFactory combatFactory;
    private boolean isCompleted = false;

    public CombatRoom(CombatFactory combatFactory, int floorLevel, boolean isBoss) {
        this.combatFactory = combatFactory;
        this.floorLevel = floorLevel;
        this.isBoss = isBoss;
    }

    public Combat getCombat(Trainer playerTrainer) {
        // Not a real getter but lazily creates the combat :))) <3 Love
        return this.combatFactory.create(playerTrainer, this.floorLevel, this.isBoss);
    }

    public boolean isBoss() {
        return this.isBoss;
    }

    public void markCompleted() {
        this.isCompleted = true;
    }

    @Override
    public boolean isCompleted() {
        return this.isCompleted;
    }

    @Override
    public String toString() {
        return "CombatRoom{ isBoss=" + this.isBoss + '}';
    }
}
