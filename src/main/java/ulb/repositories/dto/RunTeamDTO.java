package ulb.repositories.dto;

import java.util.Map;

public record RunTeamDTO(String playername, String teamName, Map<TeamMemberDTO, Integer> hpPerMember) {
}
