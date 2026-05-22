package ulb.repositories;

import ulb.common.dto.persistence.DefaultInventoryDTO;
import ulb.repositories.exceptions.PlayernameAlreadyExistsException;

/**
 * Repository for creating and querying top-level player records.
 *
 * <p>
 * A player record acts as the root aggregate: all other per-player data (inventory, team, tower progress) is keyed by
 * the player's unique name.
 */
public interface PlayerRepository {

    /**
     * Creates a new player and seeds their inventory with the supplied defaults.
     *
     * @param playerName
     *            the desired unique player name
     * @param defaultInventory
     *            the starting item set to grant on creation
     * @throws PlayernameAlreadyExistsException
     *             if a player with that name already exists
     */
    void createPlayer(String playerName, DefaultInventoryDTO defaultInventory) throws PlayernameAlreadyExistsException;

    /**
     * Returns {@code true} if a player record with the given name exists.
     *
     * @param playerName
     *            the player's unique name
     */
    boolean playerExists(String playerName);

    /**
     * Returns the index of the tower floor the player is currently on.
     *
     * @param playerName
     *            the player's unique name
     * @return the current floor number (1-based)
     */
    int getPlayerCurrentFloor(String playerName);

    /**
     * Persists the player's current tower floor.
     *
     * @param playerName
     *            the player's unique name
     * @param floorNumber
     *            the floor index to store
     */
    void setPlayerCurrentFloor(String playerName, int floorNumber);

    /**
     * Resets the player's tower floor to the starting value.
     *
     * @param playerName
     *            the player's unique name
     */
    void resetPlayerCurrentFloor(String playerName);
}
