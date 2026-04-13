package ulb.models.tower.utils;

import ulb.models.combat.Combat;
import ulb.models.combat.Combat.EndOfCombatAction;
import ulb.models.combat.CombatXpDistributor;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;
import ulb.services.CombatService;

/** Creates {@link ulb.models.combat.Combat} instances for tower rooms, wiring XP distribution and HP restoration. */
public class CombatFactory {
    private final BugemonService bugemonService;

    public CombatFactory(BugemonService bugemonService) {
        this.bugemonService = bugemonService;
    }

    /**
     * Creates a {@link ulb.models.combat.Combat} with a random opponent sized to match the player's team. HP is
     * restored for both sides at end of combat via {@link ulb.models.combat.Combat.EndOfCombatAction#RESTORE_HP}.
     */
    public Combat create(Trainer playerTrainer, int floorNumber, boolean isBoss) {
        // Later will handle the floor difficulty
        AutoTrainer opponentTrainer = new AutoTrainer(CombatService
                .createRandomTeam(this.bugemonService.getAllDefaultBugemons(), playerTrainer.getTeamSize()));

        CombatXpDistributor combatxpDistributor = new CombatXpDistributor(this.bugemonService);
        return new Combat(combatxpDistributor, playerTrainer, opponentTrainer, EndOfCombatAction.RESTORE_HP);
    }
}
