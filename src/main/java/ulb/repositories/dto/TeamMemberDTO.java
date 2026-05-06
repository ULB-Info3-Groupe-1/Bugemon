package ulb.repositories.dto;

/**
 * Data transfer object for a team member
 *
 * @param playername
 *            the name of the player
 * @param teamName
 *            the name of the team
 * @param bugemonName
 *            the name of the bugemon
 * @param slotPosition
 *            the position of the bugemon in the team
 */
public record TeamMemberDTO(String playername, String teamName, String bugemonName, int slotPosition) {
}
