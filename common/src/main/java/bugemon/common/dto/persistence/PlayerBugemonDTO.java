package bugemon.common.dto.persistence;

import java.util.List;

import bugemon.common.models.bugemon.Attack;

/**
 * Serialisable snapshot of a player-owned Bugemon as stored in the database, including all per-player progression data
 * layered on top of the base species stats.
 *
 * @param playerName
 *            the owning player's name
 * @param bugemonName
 *            the species name, used as a foreign key to the static Bugemon table
 * @param bonusDefense
 *            accumulated defense bonus from items or skills
 * @param bonusAttackPower
 *            accumulated attack bonus from items or skills
 * @param bonusInitiative
 *            accumulated initiative bonus from items or skills
 * @param bonusMaxHp
 *            accumulated max-HP bonus from items or skills
 * @param xp
 *            total experience points earned
 * @param level
 *            current level
 * @param attacks
 *            list of attacks currently known by this Bugemon
 */
public record PlayerBugemonDTO(String playerName, String bugemonName, int bonusDefense, int bonusAttackPower,
        int bonusInitiative, int bonusMaxHp, int xp, int level, List<Attack> attacks) {
}
