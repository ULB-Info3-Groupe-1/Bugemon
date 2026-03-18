package ulb.common.dto;

import java.util.List;
import java.util.Optional;

public interface BugemonTeamDTO {
    Optional<BugemonDTO> getBugemon(String id);
    List<BugemonDTO> getTeam();
    boolean contains(String id);
    boolean isEmpty();
    boolean isFull();
    int size();
}
