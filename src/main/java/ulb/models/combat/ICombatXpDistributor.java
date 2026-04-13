package ulb.models.combat;

/**
 * Strategy for distributing XP after a combat ends. Injected into {@link Combat} to keep XP logic replaceable in tests.
 */
public interface ICombatXpDistributor {

    /** Distributes XP to the winning trainer's participating Bugemons. */
    void distributeXp(CombatContext combatCtx);

}
