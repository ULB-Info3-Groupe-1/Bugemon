package ulb.repositories;

import java.util.List;

import ulb.common.dto.persistence.SkillDTO;

/**
 * Repository for persisting a player's skill-tree progress (unlocked skills and available skill points).
 */
public interface SkillRepository {

    /**
     * Returns all skills unlocked by the given player.
     *
     * @param playerName
     *            the player's unique name
     * @return list of {@link SkillDTO} records, possibly empty
     */
    List<SkillDTO> findAll(String playerName);

    /**
     * Finds a single skill record by its identifier.
     *
     * @param playerName
     *            the player's unique name
     * @param skillId
     *            the unique skill identifier
     * @return the {@link SkillDTO}, or {@code null} if the player has not unlocked that skill
     */
    SkillDTO findById(String playerName, String skillId);

    /**
     * Returns the current level of a specific skill for the given player.
     *
     * @param playerName
     *            the player's unique name
     * @param skillId
     *            the unique skill identifier
     * @return the skill level, or {@code 0} if not yet unlocked
     */
    int findSkillLevel(String playerName, String skillId);

    /**
     * Returns the number of unspent skill points available to the player.
     *
     * @param playerName
     *            the player's unique name
     * @return the current skill-point balance
     */
    int findSkillPoints(String playerName);

    /**
     * Atomically replaces the player's skill list and skill-point balance.
     *
     * @param playerName
     *            the player's unique name
     * @param skills
     *            the complete set of skills to persist
     * @param skillPoints
     *            the new skill-point balance
     */
    void save(String playerName, List<SkillDTO> skills, int skillPoints);

    /**
     * Deletes all skill records for the given player.
     *
     * @param playerName
     *            the player's unique name
     */
    void delete(String playerName);
}
