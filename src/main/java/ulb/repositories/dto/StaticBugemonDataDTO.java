package ulb.repositories.dto;

import java.util.List;

import ulb.models.bugemon.Attack;

public record StaticBugemonDataDTO(String name, String type, String spriteUrl, List<Attack> attackList,
        boolean isStarter) {
}
