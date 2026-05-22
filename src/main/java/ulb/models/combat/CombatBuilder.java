package ulb.models.combat;

import java.util.Objects;

import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.strategy.CombatStrategy;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.item.Inventory;
import ulb.models.skills.SkillContext;

/**
 * Fluent builder for constructing a {@link Combat} instance.
 *
 * <p>
 * All setter methods return {@code this} for chaining. {@link #build()} validates that every mandatory field is
 * non-null before constructing the {@code Combat}; if {@code playerSkillContext} is not provided,
 * {@link ulb.models.skills.SkillContext#NONE} is used as a safe default.
 *
 * <p>
 * Example usage:
 *
 * <pre>{@code
 * Combat combat = new CombatBuilder().playerTeam(playerTeam).opponentTeam(opponentTeam).floor(currentFloor)
 *         .bossMode(false).playerInventory(inv).opponentInventory(emptyInv).playerStrategy(humanStrategy)
 *         .opponentStrategy(aiStrategy).damageCalculator(calculator).effectProcessor(processor).build();
 * }</pre>
 */
public final class CombatBuilder {

    private CombatTeam playerTeam;
    private CombatTeam opponentTeam;
    private int floor;
    private boolean bossMode;
    private Inventory playerInventory;
    private Inventory opponentInventory;
    private CombatStrategy playerStrategy;
    private CombatStrategy opponentStrategy;
    private DamageCalculator damageCalculator;
    private EffectProcessor effectProcessor;
    private SkillContext playerSkillContext;

    public CombatBuilder playerTeam(CombatTeam team) {
        this.playerTeam = team;
        return this;
    }

    public CombatBuilder opponentTeam(CombatTeam team) {
        this.opponentTeam = team;
        return this;
    }

    public CombatBuilder floor(int currentFloor) {
        this.floor = currentFloor;
        return this;
    }

    public CombatBuilder bossMode(boolean mode) {
        this.bossMode = mode;
        return this;
    }

    public CombatBuilder playerInventory(Inventory inventory) {
        this.playerInventory = inventory;
        return this;
    }

    public CombatBuilder opponentInventory(Inventory inventory) {
        this.opponentInventory = inventory;
        return this;
    }

    public CombatBuilder playerStrategy(CombatStrategy strategy) {
        this.playerStrategy = strategy;
        return this;
    }

    public CombatBuilder opponentStrategy(CombatStrategy strategy) {
        this.opponentStrategy = strategy;
        return this;
    }

    public CombatBuilder damageCalculator(DamageCalculator calculator) {
        this.damageCalculator = calculator;
        return this;
    }

    public CombatBuilder effectProcessor(EffectProcessor processor) {
        this.effectProcessor = processor;
        return this;
    }

    public CombatBuilder playerSkillContext(SkillContext skillContext) {
        this.playerSkillContext = skillContext;
        return this;
    }

    /**
     * Validates all mandatory fields and constructs the {@link Combat}.
     *
     * @return a new, fully initialised {@code Combat}
     * @throws NullPointerException
     *             if any mandatory field has not been set
     */
    public Combat build() {
        Objects.requireNonNull(this.playerTeam, "Player team cannot be null");
        Objects.requireNonNull(this.opponentTeam, "Opponent team cannot be null");
        Objects.requireNonNull(this.playerInventory, "Player inventory cannot be null");
        Objects.requireNonNull(this.opponentInventory, "Opponent inventory cannot be null");
        Objects.requireNonNull(this.playerStrategy, "Player strategy cannot be null");
        Objects.requireNonNull(this.opponentStrategy, "Opponent strategy cannot be null");
        Objects.requireNonNull(this.damageCalculator, "Damage calculator cannot be null");
        Objects.requireNonNull(this.effectProcessor, "Effect processor cannot be null");
        Objects.requireNonNull(this.floor, "Floor cannot be null");
        Objects.requireNonNull(this.bossMode, "Boss mode cannot be null");

        if (this.playerSkillContext == null) {
            this.playerSkillContext = SkillContext.NONE;
        }

        return new Combat(this);
    }

    public CombatTeam getPlayerTeam() {
        return this.playerTeam;
    }

    public CombatTeam getOpponentTeam() {
        return this.opponentTeam;
    }

    public int getFloor() {
        return this.floor;
    }

    public boolean isBossMode() {
        return this.bossMode;
    }

    public Inventory getPlayerInventory() {
        return this.playerInventory;
    }

    public Inventory getOpponentInventory() {
        return this.opponentInventory;
    }

    public CombatStrategy getPlayerStrategy() {
        return this.playerStrategy;
    }

    public CombatStrategy getOpponentStrategy() {
        return this.opponentStrategy;
    }

    public DamageCalculator getDamageCalculator() {
        return this.damageCalculator;
    }

    public EffectProcessor getEffectProcessor() {
        return this.effectProcessor;
    }

    public SkillContext getPlayerSkillContext() {
        return this.playerSkillContext;
    }
}
