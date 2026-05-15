package ulb.models.combat;

public interface CombatStrategy {
    void chooseSwitch(CombatContext ctx, ActionCallback callback);
}
