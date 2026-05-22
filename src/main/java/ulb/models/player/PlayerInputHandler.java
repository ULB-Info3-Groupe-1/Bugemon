package ulb.models.player;

import ulb.models.combat.turn.ActionCallback;
import ulb.models.combat.utils.CombatContext;

/**
 * Strategy interface for collecting player decisions during combat.
 *
 * <p>
 * Implementations drive different input sources (UI, AI, test scripts) without changing combat logic. Each method
 * receives the current {@link CombatContext} for display and an {@link ActionCallback} to invoke once the player has
 * made a choice.
 */
public interface PlayerInputHandler {

    /**
     * Prompts the player to choose an action (attack, item, switch, etc.) for the current turn.
     *
     * @param context
     *            the current combat snapshot used to populate choice menus
     * @param callback
     *            invoked with the chosen action once the player confirms
     */
    void requestActionChoice(CombatContext context, ActionCallback callback);

    /**
     * Prompts the player to choose a replacement Bugemon after an active member faints.
     *
     * @param context
     *            the current combat snapshot used to populate the switch menu
     * @param callback
     *            invoked with the chosen switch target once the player confirms
     */
    void requestSwitchChoice(CombatContext context, ActionCallback callback);
}
