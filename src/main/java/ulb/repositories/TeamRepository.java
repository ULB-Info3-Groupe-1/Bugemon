package ulb.repositories;

import java.util.List;
import java.util.Optional;

import ulb.common.dto.persistence.TeamDTO;

/**
 * Repository for managing a player's named Bugemon teams and their active-team selection.
 */
public interface TeamRepository {

    /**
     * Returns all teams belonging to the given player.
     *
     * @param playerName
     *            the player's unique name
     * @return list of {@link TeamDTO} records, possibly empty
     */
    List<TeamDTO> findAll(String playerName);

    /**
     * Finds a team by its name within a player's collection.
     *
     * @param playerName
     *            the player's unique name
     * @param teamName
     *            the team's display name
     * @return an {@link Optional} containing the team, or empty if not found
     */
    Optional<TeamDTO> findByName(String playerName, String teamName);

    /**
     * Returns the player's currently selected active team, if any.
     *
     * @param playerName
     *            the player's unique name
     * @return an {@link Optional} containing the active {@link TeamDTO}, or empty if none is set
     */
    Optional<TeamDTO> getActiveTeam(String playerName);

    /**
     * Sets the named team as the player's active team.
     *
     * @param playerName
     *            the player's unique name
     * @param teamName
     *            the team to activate
     */
    void setActiveTeam(String playerName, String teamName);

    /**
     * Clears the player's active-team selection without deleting the team itself.
     *
     * @param playerName
     *            the player's unique name
     */
    void unSetActiveTeam(String playerName);

    /**
     * Inserts or updates a team and its composition for the given player.
     *
     * @param playerName
     *            the player's unique name
     * @param team
     *            the team data to persist
     */
    void save(String playerName, TeamDTO team);

    /**
     * Deletes a specific team and all its member records.
     *
     * @param playerName
     *            the player's unique name
     * @param teamName
     *            the team to delete
     */
    void delete(String playerName, String teamName);

    /**
     * Deletes every team and member record owned by the given player.
     *
     * @param playerName
     *            the player's unique name
     */
    void deleteAll(String playerName);
}
