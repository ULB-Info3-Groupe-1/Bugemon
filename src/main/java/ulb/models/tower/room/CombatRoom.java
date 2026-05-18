package ulb.models.tower.room;

import java.util.List;

import ulb.factories.CombatFactory;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.combat.Combat;
import ulb.models.skills.Skill;

public final class CombatRoom extends Room {
    private final boolean isBoss;

    private final List<Bugemon> allBugemons;
    private final BugemonTeam playerTeam;
    private final List<Skill> skills;

    private Combat combat;

    public CombatRoom(List<Bugemon> allBugemons, BugemonTeam playerTeam, List<Skill> skills, boolean isBoss) {
        this.isBoss = isBoss;
        this.allBugemons = allBugemons;
        this.playerTeam = playerTeam;
        this.skills = skills;
    }

    public Combat getCombat() {
        return this.combat;
    }

    @Override
    public boolean hasPlayerWon() {
        if (this.combat == null) {
            return false;
        }
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
        this.combat = CombatFactory.create(this.allBugemons, this.playerTeam, this.skills, this.isBoss);
        roomVisitor.visitCombatRoom(this);
        this.setVisited();
    }

    @Override
    public RoomType getType() {
        return this.isBoss ? RoomType.BOSS : RoomType.COMBAT;
    }

}
