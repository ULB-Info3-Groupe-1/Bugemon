package ulb.fx_controllers.combat.components;

@FunctionalInterface
public interface CombatActionCallback {
    void onAction(String actionType);
}
