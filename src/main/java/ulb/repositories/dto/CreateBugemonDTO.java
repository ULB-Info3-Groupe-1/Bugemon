package ulb.repositories.dto;

import java.net.URL;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.BugemonType;

public record CreateBugemonDTO(String name, BugemonType type, URL spriteUrl, int defense, int attack, int initiative,
        int maxHp, boolean isStarter, Attack attack1, Attack attack2, Attack attack3) {
}
