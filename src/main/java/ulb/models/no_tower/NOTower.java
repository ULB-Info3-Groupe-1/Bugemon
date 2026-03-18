/**
 * File name : NOTower.java
 * Description : Class that represents the NO Tower.
 *
 * @author Rocca Manuel
 * @date 17 mar. 2026
 * @version 1.0
 */

package ulb.models.no_tower;

import java.util.EmptyStackException;
import java.util.List;

import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.trainer.*;

public class NOTower {
    //

    private static final int MAX_FLOORS = 9;
    private int currentFloor = 0;
    private List<Floor> floors;

    // TODO: change to Player class when it will be implemented
    public NOTower(BugemonTeam playerTeam) {
        generateFloors(playerTeam);
    }

    public int getCurrentFloorNumber() {
        return currentFloor;
    }

    // TODO: is it really useful ?
    public boolean isFloorComplete() {
        return floors.get(currentFloor).isComplete();
    }

    public Floor getCurrentFloor() {
        return floors.get(currentFloor);
    }

    public boolean goToNextFloor() throws IllegalStateException {
        currentFloor++;
        try {
            getCurrentFloor().advance();
            return true;
        } catch (EmptyStackException e) {
            return false;
        }
    }

    private void generateFloors(BugemonTeam playerTeam) {
        Trainer playerTrainer = new ManualTrainer(playerTeam);
        for (int i = 0; i < MAX_FLOORS; i++) {
            floors.add(new Floor(playerTrainer));
        }
    }

}
