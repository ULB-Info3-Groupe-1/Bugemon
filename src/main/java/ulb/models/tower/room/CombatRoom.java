package ulb.models.tower.room;

import ulb.models.combat.Combat;
import ulb.models.tower.utils.TowerCombatFactory;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;
import ulb.services.CombatService;

public final class CombatRoom extends Room {
    private final boolean isBoss;
    private final TowerCombatFactory combatFactory;
    private final BugemonService bugemonService;

    private Combat combat;

    public CombatRoom(TowerCombatFactory combatFactory, BugemonService bugemonService, boolean isBoss) {
        this.combatFactory = combatFactory;
        this.bugemonService = bugemonService;
        this.isBoss = isBoss;
    }

    public Combat getCombat() {
        if (this.combat != null) {
            return this.combat;
        }
        throw new IllegalStateException("CombatRoom combat not initialised yet; call getCombat(Trainer) first");
    }

    public Combat getCombat(Trainer playerTrainer) {
        if (this.combat == null) {
            AutoTrainer opponentTrainer = new AutoTrainer(this.isBoss
                    ? CombatService.createRandomBossTeam(this.bugemonService.getAllDefaultBugemons(),
                            playerTrainer.getTeamSize())
                    : CombatService.createRandomTeam(this.bugemonService.getAllDefaultBugemons(),
                            playerTrainer.getTeamSize()));
            this.combat = this.combatFactory.create(playerTrainer, opponentTrainer);
        }
        return this.combat;
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
