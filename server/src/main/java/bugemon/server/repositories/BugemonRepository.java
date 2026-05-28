package bugemon.server.repositories;

import java.util.List;
import java.util.Optional;

import bugemon.common.dto.persistence.PlayerBugemonDTO;
import bugemon.common.models.bugemon.Bugemon;

/**
 * Repository for persisting and querying player-owned Bugemon instances.
 *
 * <p>
 * Distinguishes between <b>player Bugemon</b> (levelled-up copies owned by a specific player, represented as
 * {@link PlayerBugemonDTO}) and <b>base Bugemon</b> (immutable archetypes loaded from static game data).
 */
public interface BugemonRepository {

    /**
     * Returns all Bugemon owned by the given player.
     *
     * @param playerName
     *            the player's unique name
     * @return list of player-Bugemon DTOs, possibly empty
     */
    List<PlayerBugemonDTO> findAll(String playerName);

    /**
     * Deletes every Bugemon record owned by the given player.
     *
     * @param playerName
     *            the player's unique name
     */
    void removeAll(String playerName);

    /**
     * Finds a single player-owned Bugemon by its display name.
     *
     * @param playerName
     *            the player's unique name
     * @param bugemonName
     *            the Bugemon's display name
     * @return an {@link Optional} containing the DTO, or empty if not found
     */
    Optional<PlayerBugemonDTO> findByName(String playerName, String bugemonName);

    /**
     * Returns the base (archetype) Bugemon for the given name from static game data.
     *
     * @param bugemonName
     *            the Bugemon's canonical name
     * @return an {@link Optional} containing the base {@link Bugemon}, or empty if unknown
     */
    Optional<Bugemon> findBase(String bugemonName);

    /**
     * Inserts or updates a player-owned Bugemon record.
     *
     * @param playerBugemon
     *            the DTO to persist
     */
    void save(PlayerBugemonDTO playerBugemon);

    /**
     * Deletes a single player-owned Bugemon by name.
     *
     * @param playerName
     *            the player's unique name
     * @param bugemonName
     *            the Bugemon's display name
     */
    void delete(String playerName, String bugemonName);
}
