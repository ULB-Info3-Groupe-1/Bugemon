package bugemon.server.repositories;

import java.util.Optional;

import bugemon.common.dto.persistence.TowerDTO;

/**
 * Repository for persisting the state of a player's in-progress tower run.
 *
 * <p>
 * Only one active run per player is stored at a time; calling {@link #save} replaces any previous snapshot.
 */
public interface TowerRepository {

    /**
     * Inserts or replaces the tower-run snapshot for the given player.
     *
     * @param playerName
     *            the player's unique name
     * @param towerDTO
     *            the run state to persist (floor map, team HP, random seed, etc.)
     */
    void save(String playerName, TowerDTO towerDTO);

    /**
     * Deletes the tower-run snapshot for the given player.
     *
     * @param playerName
     *            the player's unique name
     */
    void delete(String playerName);

    /**
     * Retrieves the persisted tower-run snapshot for the given player.
     *
     * @param playerName
     *            the player's unique name
     * @return an {@link Optional} containing the {@link TowerDTO}, or empty if no run is in progress
     */
    Optional<TowerDTO> find(String playerName);
}
