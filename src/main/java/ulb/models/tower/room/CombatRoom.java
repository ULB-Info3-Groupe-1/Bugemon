package ulb.models.tower.room;

import ulb.models.combat.Combat;
import ulb.models.tower.utils.CombatFactory;
import ulb.models.trainer.Trainer;

public final class CombatRoom extends Room {
    private final int floorLevel;
    private final boolean isBoss;
    private final CombatFactory combatFactory;

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
