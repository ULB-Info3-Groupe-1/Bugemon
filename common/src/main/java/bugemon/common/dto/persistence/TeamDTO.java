package bugemon.common.dto.persistence;

import java.util.List;

/**
 * Serialisable snapshot of a named team and its ordered roster of Bugemons.
 *
 * @param playerName
 *            the owning player's name
 * @param teamName
 *            the display name of the team
 * @param members
 *            ordered list of team members; slot position is encoded in each {@link TeamMemberDTO}
 */
public record TeamDTO(String playerName, String teamName, List<TeamMemberDTO> members) {

    /** Returns the number of Bugemons in this team. */
    public int size() {
        return this.members().size();
    }
}
