package ulb.models.trainer;

import ulb.models.bugemon.Item;

/**
 * Represents a trainer's chosen action for a turn, as selected by the controller.
 */
record SimAction(SimActionKind kind, int index, Item item) {
    static SimAction attack(int attackIndex) {
        return new SimAction(SimActionKind.ATTACK, attackIndex, null);
    }

    static SimAction switchTo(int teamIndex) {
        return new SimAction(SimActionKind.SWITCH, teamIndex, null);
    }

    static SimAction useItem(Item item) {
        return new SimAction(SimActionKind.ITEM, -1, item);
    }

    static SimAction none() {
        return new SimAction(SimActionKind.NONE, -1, null);
    }
}
