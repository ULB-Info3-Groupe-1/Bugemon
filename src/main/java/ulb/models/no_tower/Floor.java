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
import ulb.models.trainer.*;
import ulb.services.CombatService;
import ulb.services.PlayerService;

public class Floor {
    private final Trainer playerTrainer;
    private final PlayerService playerService;
    private Stack<Room> stages = new Stack<>();

    public Floor(Trainer playerTrainer, PlayerService playerService) {
        this.playerTrainer = playerTrainer;
        this.playerService = playerService;

        init();
        // 1. Combat obligatoire
        // 2. Récompense (choix parmi 3 options) (cf. histoire « Récompenses d’étage »)
        // 3. Combat obligatoire
        // 4. Combat obligatoire
        // 5. Récompense (choix parmi 3 options)
        // 6. Boss d’étage
    }

    public boolean isComplete() {
        return this.stages.isEmpty();
    }

    public Room getNextRoom() throws EmptyStackException {
        return stages.pop();
    }

    public Room getCurrentRoom() throws EmptyStackException {
        return stages.peek();
    }

    private void init() {
        stages.push(initBossCombatRoom());
        stages.push(initRewardRoom());
        stages.push(initCombatRoom());
        stages.push(initCombatRoom());
        stages.push(initRewardRoom());
        stages.push(initCombatRoom());
    }

    private CombatRoom initCombatRoom() {
        Trainer opponentTrainer = new ManualTrainer(
                CombatService.createRandomTeam(this.playerService.getAllDefaultBugemons(),
                                               playerTrainer.getTeamSize()),
                playerService.getInventory());
        Combat combat = new Combat(playerTrainer, opponentTrainer);

        return new CombatRoom(combat, false);
    }

    private RewardRoom initRewardRoom() {
        // TODO: histoire 11
        return new RewardRoom();
    }

    private CombatRoom initBossCombatRoom() {
        Trainer opponentTrainer = new ManualTrainer(
                CombatService.createBossTeam(this.playerService.getAllDefaultBugemons()),
                playerService.getInventory());
        Combat combat = new Combat(playerTrainer, opponentTrainer);

        return new CombatRoom(combat, true);
    }

    void advance() throws EmptyStackException {
        stages.pop();
    }
}