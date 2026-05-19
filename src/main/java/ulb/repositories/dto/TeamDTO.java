package ulb.repositories.dto;

import java.util.List;

public record TeamDTO(String playername, String teamName, List<TeamMemberDTO> members) {
}
