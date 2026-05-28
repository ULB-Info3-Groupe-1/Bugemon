package bugemon.common.models.combat;

import java.util.Objects;

import bugemon.common.models.combat.damage.DamageCalculator;
import bugemon.common.models.combat.strategy.CombatStrategy;
import bugemon.common.models.combat.utils.EffectProcessor;
import bugemon.common.models.item.Inventory;
import bugemon.common.models.skills.SkillContext;

/**
 * Fluent builder for constructing a {@link Combat} instance.
 *
 * <p>
 * All setter methods return {@code this} for chaining. {@link #build()} validates that every mandatory field is
 * non-null before constructing the {@code Combat}; if {@code playerSkillContext} is not provided,
 * {@link bugemon.common.models.skills.SkillContext#NONE} is used as a safe default.
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

    /**
     * Sets the player's combat team.
     *
     * @param team
     *            the player's {@link CombatTeam}; must not be {@code null}
     * @return {@code this} builder for chaining
     */
    public CombatBuilder playerTeam(CombatTeam team) {
        this.playerTeam = team;
        return this;
    }

    /**
     * Sets the opponent's combat team.
     *
     * @param team
     *            the opponent's {@link CombatTeam}; must not be {@code null}
     * @return {@code this} builder for chaining
     */
    public CombatBuilder opponentTeam(CombatTeam team) {
        this.opponentTeam = team;
        return this;
    }

    /**
     * Sets the tower floor on which this combat takes place.
     *
     * @param currentFloor
     *            the floor number
     * @return {@code this} builder for chaining
     */
    public CombatBuilder floor(int currentFloor) {
        this.floor = currentFloor;
        return this;
    }

    /**
     * Sets whether this combat is a boss encounter.
     *
     * @param mode
     *            {@code true} for a boss fight, {@code false} for a regular encounter
     * @return {@code this} builder for chaining
     */
    public CombatBuilder bossMode(boolean mode) {
        this.bossMode = mode;
        return this;
    }

    /**
     * Sets the player's item inventory.
     *
     * @param inventory
     *            the player's {@link Inventory}; must not be {@code null}
     * @return {@code this} builder for chaining
     */
    public CombatBuilder playerInventory(Inventory inventory) {
        this.playerInventory = inventory;
        return this;
    }

    /**
     * Sets the opponent's item inventory.
     *
     * @param inventory
     *            the opponent's {@link Inventory}; must not be {@code null}
     * @return {@code this} builder for chaining
     */
    public CombatBuilder opponentInventory(Inventory inventory) {
        this.opponentInventory = inventory;
        return this;
    }

    /**
     * Sets the strategy that drives the player's decisions.
     *
     * @param strategy
     *            the player-side {@link CombatStrategy}; must not be {@code null}
     * @return {@code this} builder for chaining
     */
    public CombatBuilder playerStrategy(CombatStrategy strategy) {
        this.playerStrategy = strategy;
        return this;
    }

    /**
     * Sets the strategy that drives the opponent's decisions.
     *
     * @param strategy
     *            the opponent-side {@link CombatStrategy}; must not be {@code null}
     * @return {@code this} builder for chaining
     */
    public CombatBuilder opponentStrategy(CombatStrategy strategy) {
        this.opponentStrategy = strategy;
        return this;
    }

    /**
     * Sets the damage calculator used to resolve attack damage.
     *
     * @param calculator
     *            the {@link DamageCalculator}; must not be {@code null}
     * @return {@code this} builder for chaining
     */
    public CombatBuilder damageCalculator(DamageCalculator calculator) {
        this.damageCalculator = calculator;
        return this;
    }

    /**
     * Sets the effect processor that applies secondary attack effects.
     *
     * @param processor
     *            the {@link EffectProcessor}; must not be {@code null}
     * @return {@code this} builder for chaining
     */
    public CombatBuilder effectProcessor(EffectProcessor processor) {
        this.effectProcessor = processor;
        return this;
    }

    /**
     * Sets the player's skill context supplying combat bonuses. If not called, {@link SkillContext#NONE} is used.
     *
     * @param skillContext
     *            the player's {@link SkillContext}
     * @return {@code this} builder for chaining
     */
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

    /** Returns the configured player team. */
    public CombatTeam getPlayerTeam() {
        return this.playerTeam;
    }

    /** Returns the configured opponent team. */
    public CombatTeam getOpponentTeam() {
        return this.opponentTeam;
    }

    /** Returns the configured floor number. */
    public int getFloor() {
        return this.floor;
    }

    /** Returns whether boss mode is enabled. */
    public boolean isBossMode() {
        return this.bossMode;
    }

    /** Returns the configured player inventory. */
    public Inventory getPlayerInventory() {
        return this.playerInventory;
    }

    /** Returns the configured opponent inventory. */
    public Inventory getOpponentInventory() {
        return this.opponentInventory;
    }

    /** Returns the configured player strategy. */
    public CombatStrategy getPlayerStrategy() {
        return this.playerStrategy;
    }

    /** Returns the configured opponent strategy. */
    public CombatStrategy getOpponentStrategy() {
        return this.opponentStrategy;
    }

    /** Returns the configured damage calculator. */
    public DamageCalculator getDamageCalculator() {
        return this.damageCalculator;
    }

    /** Returns the configured effect processor. */
    public EffectProcessor getEffectProcessor() {
        return this.effectProcessor;
    }

    /** Returns the configured player skill context, or {@link SkillContext#NONE} if not set. */
    public SkillContext getPlayerSkillContext() {
        return this.playerSkillContext;
    }
}
