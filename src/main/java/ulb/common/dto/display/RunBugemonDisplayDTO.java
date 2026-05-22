package ulb.common.dto.display;

import ulb.models.bugemon.ElementType;

public record RunBugemonDisplayDTO(String name, int level, int currentHp, int maxHp, ElementType type) {
}
