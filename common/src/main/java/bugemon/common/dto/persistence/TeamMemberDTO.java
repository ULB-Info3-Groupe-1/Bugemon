package bugemon.common.dto.persistence;

/**
 * Serialisable reference to a Bugemon occupying a numbered slot in a team.
 *
 * @param bugemonName
 *            the species name, used as a foreign key to the player's Bugemon roster
 * @param slotPosition
 *            the 0-based index of the slot this Bugemon occupies within the team
 */
public record TeamMemberDTO(String bugemonName, int slotPosition) {
}
