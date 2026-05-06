package ulb.repositories.dto;

/**
 * Data transfer object for a player's bugemon
 *
 * @param playername
 *            the name of the player
 * @param bugemonName
 *            the name of the bugemon
 * @param currentDefense
 *            the current defense of the bugemon
 * @param currentAttackPower
 *            the current attack power of the bugemon
 * @param currentInitiative
 *            the current initiative of the bugemon
 * @param currentMaxHp
 *            the current max hp of the bugemon
 * @param currentXp
 *            the current xp of the bugemon
 * @param currentLevel
 *            the current level of the bugemon
 */
public record PlayerBugemonDTO(String playername, String bugemonName, int currentDefense, int currentAttackPower,
        int currentInitiative, int currentMaxHp, int currentXp, int currentLevel) {
}
