package ulb.models.trainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Item;

/**
 * Represents the state of a combat from the perspective of the AI, used for MiniMax simulations. It includes the teams,
 * current active bugemon indices, and inventories of both trainers.
 */
final class CombatState {
    final List<Bugemon> aiTeam;
    final List<Bugemon> opponentTeam;
    final Map<Item, Integer> aiInventory;
    final Map<Item, Integer> opponentInventory;

    int aiCurrentIdx;
    int opponentCurrentIdx;

    CombatState(List<Bugemon> aiTeam, List<Bugemon> opponentTeam, int aiCurrentIdx, int opponentCurrentIdx,
            Map<Item, Integer> aiInventory, Map<Item, Integer> opponentInventory) {
        this.aiTeam = aiTeam;
        this.opponentTeam = opponentTeam;
        this.aiCurrentIdx = aiCurrentIdx;
        this.opponentCurrentIdx = opponentCurrentIdx;
        this.aiInventory = aiInventory;
        this.opponentInventory = opponentInventory;
    }

    /**
     * Factory method to create a CombatState from the current state of the trainers.
     *
     * @param aiTrainer
     *            the AI trainer for whom this state is being created.
     * @param opponentTrainer
     *            the opponent trainer.
     * @return a CombatState representing the current state of the combat from the AI's perspective.
     */
    static CombatState from(AITrainer aiTrainer, Trainer opponentTrainer) {
        if (opponentTrainer == null) {
            throw new IllegalStateException("MiniMax requires a known opponent trainer");
        }

        List<Bugemon> aiTeam = copyTeam(aiTrainer.getBugemons());
        List<Bugemon> oppTeam = copyTeam(opponentTrainer.getBugemons());

        int aiCurrentIdx = findCurrentIndex(aiTrainer.getCurrentBugemon(), aiTrainer.getBugemons());
        int oppCurrentIdx = findCurrentIndex(opponentTrainer.getCurrentBugemon(), opponentTrainer.getBugemons());

        Map<Item, Integer> aiInventory = new HashMap<>(aiTrainer.getInventoryMap());
        Map<Item, Integer> opponentInventory = extractInventoryMap(opponentTrainer);

        return new CombatState(aiTeam, oppTeam, aiCurrentIdx, oppCurrentIdx, aiInventory, opponentInventory);
    }

    /**
     * Creates a deep copy of this CombatState.
     *
     * @return a new CombatState with copied teams and inventories, and the same current indices.
     */
    CombatState copy() {
        return new CombatState(copyTeam(this.aiTeam), copyTeam(this.opponentTeam), this.aiCurrentIdx,
                this.opponentCurrentIdx, new HashMap<>(this.aiInventory), new HashMap<>(this.opponentInventory));
    }

    /**
     * Returns the current active Bugemon of the AI trainer.
     *
     * @return the current active Bugemon of the AI trainer.
     */
    Bugemon aiCurrent() {
        return this.aiTeam.get(this.aiCurrentIdx);
    }

    /**
     * Returns the current active Bugemon of the opponent trainer.
     *
     * @return the current active Bugemon of the opponent trainer.
     */
    Bugemon opponentCurrent() {
        return this.opponentTeam.get(this.opponentCurrentIdx);
    }

    /**
     * Checks if the AI trainer has been defeated.
     *
     * @return true if the AI trainer has been defeated, false otherwise.
     */
    boolean isAiDefeated() {
        for (Bugemon bugemon : this.aiTeam) {
            if (bugemon.isAlive()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the opponent trainer has been defeated.
     *
     * @return true if the opponent trainer has been defeated, false otherwise.
     */
    boolean isOpponentDefeated() {
        for (Bugemon bugemon : this.opponentTeam) {
            if (bugemon.isAlive()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a deep copy of a list of Bugemon.
     *
     * @param originalTeam
     *            the original list of Bugemon to copy.
     * @return a new list containing copies of the original Bugemon.
     */
    private static List<Bugemon> copyTeam(List<Bugemon> originalTeam) {
        List<Bugemon> copy = new ArrayList<>();
        originalTeam.forEach(bugemon -> copy.add(new Bugemon(bugemon)));
        return copy;
    }

    /**
     * Finds the index of the current active Bugemon in its team list.
     *
     * @param current
     *            the current active Bugemon.
     * @param team
     *            the list of Bugemon representing the trainer's team.
     * @return the index of the current active Bugemon in the team list.
     */
    private static int findCurrentIndex(Bugemon current, List<Bugemon> team) {
        int idx = team.indexOf(current);
        if (idx < 0) {
            throw new IllegalStateException("Current bugemon is not in its team");
        }
        return idx;
    }

    /**
     * Extracts the inventory map from a trainer.
     *
     * @param trainer
     *            the trainer whose inventory map is to be extracted.
     * @return a map representing the trainer's inventory.
     */
    private static Map<Item, Integer> extractInventoryMap(Trainer trainer) {
        if (trainer instanceof AITrainer aiTrainer) {
            return new HashMap<>(aiTrainer.getInventoryMap());
        }
        if (trainer instanceof ManualTrainer manualTrainer) {
            return new HashMap<>(manualTrainer.getInventoryMap());
        }
        return new HashMap<>();
    }
}
