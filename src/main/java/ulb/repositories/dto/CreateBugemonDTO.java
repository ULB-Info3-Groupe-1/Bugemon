package ulb.repositories.dto;

import java.net.URL;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.ElementType;

public record CreateBugemonDTO(String name, ElementType type, URL spriteUrl, int defense, int attack, int initiative,
        int maxHp, boolean isStarter, Attack attack1, Attack attack2, Attack attack3) {
}
