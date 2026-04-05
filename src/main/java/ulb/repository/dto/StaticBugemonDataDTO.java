package ulb.repository.dto;

import java.util.List;

import ulb.models.bugemon.Attack;

/**
 * Data transfer object for a Bugemon with just the static data (immutable).
 */
public record StaticBugemonDataDTO(String name, String type, String spriteUrl, List<Attack> attackList,
        boolean isStarter) {
}
