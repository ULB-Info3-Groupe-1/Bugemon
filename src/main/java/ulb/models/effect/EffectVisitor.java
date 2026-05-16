package ulb.models.effect;

public interface EffectVisitor {

    void visit(HealEffect healEffect);

    void visit(ResetMalusEffect resetMalusEffect);

    void visit(StatModifierEffect statModifierEffect);

}
