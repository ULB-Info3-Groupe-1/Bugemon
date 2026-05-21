package ulb.common.dto.persistence;

import java.net.URL;
import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.ElementType;

public record CreateBugemonDTO(String name, ElementType type, URL spriteUrl, int defense, int attack, int initiative,
                int maxHp, boolean isStarter, List<Attack> attacks) {
}
