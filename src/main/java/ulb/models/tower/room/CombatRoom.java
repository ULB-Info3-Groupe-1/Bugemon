package ulb.models.tower.room;

import ulb.models.combat.Combat;
import ulb.models.tower.utils.CombatFactory;
import ulb.models.trainer.Trainer;

public final class CombatRoom extends Room {
    private final boolean isBoss;
    private final CombatFactory combatFactory;

    private final Combat combat;

    public CombatRoom(CombatFactory combatFactory, Trainer playerTrainer, boolean isBoss) {
        this.combatFactory = combatFactory;
        this.isBoss = isBoss;
        this.combat = this.combatFactory.create(playerTrainer, this.isBoss);
    }

    public Combat getCombat() {
        // Not a real getter but lazily creates the combat :))) <3 Love
        return this.combat;
    }

    @Override
    public boolean hasPlayerWon() {
        return this.combat.hasPlayerWon();
    }

    public boolean isBoss() {
        return this.isBoss;
    }

    @Override
    public String toString() {
        return "CombatRoom{ isBoss=" + this.isBoss + '}';
    }

    @Override
    public void visitIfNotVisited(RoomVisitor roomVisitor) {
        if (this.isVisited()) {
            return;
        }
        roomVisitor.visitCombatRoom(this);
        this.setVisited();
    }

    @Override
    public RoomType getType() {
        return this.isBoss ? RoomType.BOSS : RoomType.COMBAT;
    }

}
