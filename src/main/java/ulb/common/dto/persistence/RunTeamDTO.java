package ulb.common.dto.persistence;

import java.util.Map;

public record RunTeamDTO(String playerName, String teamName, Map<TeamMemberDTO, Integer> hpPerMember) {
}
