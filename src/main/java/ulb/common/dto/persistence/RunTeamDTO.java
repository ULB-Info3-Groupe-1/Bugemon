package ulb.common.dto.persistence;

import java.util.Map;

public record RunTeamDTO(String playername, String teamName, Map<TeamMemberDTO, Integer> hpPerMember) {
}
