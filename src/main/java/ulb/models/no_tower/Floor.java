/**
 * File name : Floor.java
 * Description : Class that represents a floor in the NO Tower.
 *
 * @author Rocca Manuel
 * @date 17 mar. 2026
 * @version 1.0
 */

package ulb.models.no_tower;

import java.util.EmptyStackException;
import java.util.Stack;

import ulb.models.combat.Combat;
import ulb.models.no_tower.room.CombatRoom;
import ulb.models.no_tower.room.RewardRoom;
import ulb.models.no_tower.room.Room;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.services.CombatService;
import ulb.services.PlayerService;

public class Floor {
    private final Trainer playerTrainer;
    private final PlayerService playerService;
    private Stack<Room> stages = new Stack<>();

    public Floor(Trainer playerTrainer, PlayerService playerService) {
        this.playerTrainer = playerTrainer;
        this.playerService = playerService;

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
        Trainer opponentTrainer = new AutoTrainer(CombatService.createRandomTeam(
                this.playerService.getAllDefaultBugemons(), this.playerTrainer.getTeamSize()));
        Combat combat = new Combat(this.playerTrainer, opponentTrainer);

        return new CombatRoom(combat, false);
    }

    private RewardRoom initRewardRoom() {
        // TODO: histoire 11
        return new RewardRoom();
    }

    private CombatRoom initBossCombatRoom() {
        Trainer opponentTrainer = new AutoTrainer(
                CombatService.createBossTeam(this.playerService.getAllDefaultBugemons()));
        Combat combat = new Combat(this.playerTrainer, opponentTrainer);

        return new CombatRoom(combat, true);
    }

    void advance() throws EmptyStackException {
        this.stages.pop();
    }
}
