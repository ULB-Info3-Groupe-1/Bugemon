package bugemon.common.dto.persistence;

import java.util.Map;

/**
 * Serialisable snapshot of the team's in-run health state, used to persist and restore mid-run progress when the player
 * quits and resumes.
 *
 * @param playerName
 *            the owning player's name
 * @param teamName
 *            the name of the team being tracked
 * @param hpPerMember
 *            mapping from each {@link TeamMemberDTO} to the Bugemon's current HP
 */
public record RunTeamDTO(String playerName, String teamName, Map<TeamMemberDTO, Integer> hpPerMember) {
}
