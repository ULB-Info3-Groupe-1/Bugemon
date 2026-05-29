package bugemon.common.dto.persistence;

import java.io.Serializable;

/**
 * Complete serialisable snapshot of an in-progress tower run, used to persist and resume the player's run state across
 * sessions.
 *
 * @param seed
 *            the random seed used to generate the tower's floor layouts deterministically
 * @param floorMap
 *            the player's current position and visited-room history on the active floor
 * @param team
 *            the team's in-run health state at the time of saving
 */
public record TowerDTO(int seed, FloorMapDTO floorMap, RunTeamDTO team) implements Serializable {

}
