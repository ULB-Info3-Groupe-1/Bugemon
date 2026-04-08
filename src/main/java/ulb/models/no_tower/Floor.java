package ulb.models.no_tower;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EmptyStackException;

import ulb.models.combat.Combat;
import ulb.models.no_tower.room.CombatRoom;
import ulb.models.no_tower.room.RewardRoom;
import ulb.models.no_tower.room.Room;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.BugemonService;
import ulb.services.CombatService;

public class Floor {
    private final Trainer playerTrainer;
    private final BugemonService bugemonService;
    private final Deque<Room> stages = new ArrayDeque<>();

    public Floor(Trainer playerTrainer, BugemonService bugemonService) {
        this.playerTrainer = playerTrainer;
        this.bugemonService = bugemonService;

        this.init();
    }

    public boolean isComplete() {
        return this.stages.isEmpty();
    }

    public Room getNextRoom() throws EmptyStackException {
        return this.stages.pop();
    }

    public Room getCurrentRoom() throws EmptyStackException {
        return this.stages.peek();
    }

    private void init() {
        this.stages.push(this.initBossCombatRoom());
        this.stages.push(this.initRewardRoom());
        this.stages.push(this.initCombatRoom());
        this.stages.push(this.initCombatRoom());
        this.stages.push(this.initRewardRoom());
        this.stages.push(this.initCombatRoom());
    }

    private CombatRoom initCombatRoom() {
        Trainer opponentTrainer = new AutoTrainer(CombatService
                .createRandomTeam(this.bugemonService.getAllDefaultBugemons(), this.playerTrainer.getTeamSize()));
        Combat combat = new Combat(this.playerTrainer, opponentTrainer);

        return new CombatRoom(combat, false);
    }

    private RewardRoom initRewardRoom() {
        // TODO: histoire 11
        return new RewardRoom();
    }

    private CombatRoom initBossCombatRoom() {
        Trainer opponentTrainer = new AutoTrainer(
                CombatService.createBossTeam(this.bugemonService.getAllDefaultBugemons()));
        Combat combat = new Combat(this.playerTrainer, opponentTrainer);

        return new CombatRoom(combat, true);
    }

    void advance() throws EmptyStackException {
        this.stages.pop();
    }
}
