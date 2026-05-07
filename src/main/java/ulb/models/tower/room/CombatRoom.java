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

    public CombatRoom(TowerCombatFactory combatFactory, BugemonService bugemonService, boolean isBoss) {
        this.combatFactory = combatFactory;
        this.bugemonService = bugemonService;
        this.isBoss = isBoss;
    }

    public Combat getCombat(Trainer playerTrainer) {
        // Not a real getter but lazily creates the combat :))) <3 Love

        AutoTrainer opponentTrainer = new AutoTrainer(this.isBoss
                ? CombatService.createRandomBossTeam(this.bugemonService.getAllDefaultBugemons(),
                        playerTrainer.getTeamSize())
                : CombatService.createRandomTeam(this.bugemonService.getAllDefaultBugemons(),
                        playerTrainer.getTeamSize()));

        return this.combatFactory.create(playerTrainer, opponentTrainer);
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
