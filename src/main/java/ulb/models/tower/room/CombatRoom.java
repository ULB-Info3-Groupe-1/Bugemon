package ulb.models.tower.room;

import java.util.List;

import ulb.factories.CombatFactory;
import ulb.models.bugemon.Bugemon;
import ulb.models.combat.Combat;
import ulb.models.skills.Skill;
import ulb.models.trainer.Trainer;

public final class CombatRoom extends Room {
    private final boolean isBoss;

    private final Combat combat;

    public CombatRoom(List<Bugemon> allBugemons, List<Skill> skills, Trainer playerTrainer, boolean isBoss) {
        this.isBoss = isBoss;
        this.combat = CombatFactory.create(allBugemons, playerTrainer, skills, this.isBoss);
    }

    public Combat getCombat() {
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
    public void visit(RoomVisitor roomVisitor) {
        roomVisitor.visitCombatRoom(this);
        this.setVisited();
    }

    @Override
    public RoomType getType() {
        return this.isBoss ? RoomType.BOSS : RoomType.COMBAT;
    }

}
