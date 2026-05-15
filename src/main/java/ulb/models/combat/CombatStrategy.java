package ulb.models.combat;

public interface CombatStrategy {
    void chooseAction(CombatContext ctx, ActionCallback callback);

    void chooseSwitch(CombatContext ctx, ActionCallback callback);
}
