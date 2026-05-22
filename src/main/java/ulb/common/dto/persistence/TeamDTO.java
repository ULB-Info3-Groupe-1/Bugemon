package ulb.common.dto.persistence;

import java.util.List;

public record TeamDTO(String playername, String teamName, List<TeamMemberDTO> members) {

    public int size() {
        return this.members().size();
    }
}
