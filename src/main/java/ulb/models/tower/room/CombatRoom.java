package ulb.models.tower.room;

import ulb.models.combat.Combat;
import ulb.models.tower.utils.CombatFactory;
import ulb.models.trainer.Trainer;

/** A room that triggers a combat when visited; may be a regular encounter or the boss fight at floor end. */
public final class CombatRoom extends Room {
    private final int floorLevel;
    private final boolean isBoss;
    private final CombatFactory combatFactory;

    public CombatRoom(CombatFactory combatFactory, int floorLevel, boolean isBoss) {
        this.combatFactory = combatFactory;
        this.floorLevel = floorLevel;
        this.isBoss = isBoss;
    }

    /**
     * Creates and returns a new {@link Combat} for the given trainer. A new instance is created on every call.
     */
    public Combat getCombat(Trainer playerTrainer) {
        return this.combatFactory.create(playerTrainer, this.floorLevel, this.isBoss);
    }

    public boolean isBoss() {
        return this.isBoss;
    }

    @Override
    public String toString() {
        return "CombatRoom{ isBoss=" + this.isBoss + '}';
    }

    @Override
    public void visit(RoomVisitor roomVisitor) {
        roomVisitor.visitCombatRoom(this);
    }

    @Override
    public RoomType getType() {
        return this.isBoss ? RoomType.BOSS : RoomType.COMBAT;
    }

}
