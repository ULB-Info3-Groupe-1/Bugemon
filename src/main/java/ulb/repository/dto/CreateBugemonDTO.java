package ulb.repository.dto;

import java.util.Objects;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.BugemonType;

public record CreateBugemonDTO(String name, BugemonType type, String spriteUrl, int defense, int attack, int initiative,
        int maxHp, boolean isStarter, Attack attack1, Attack attack2, Attack attack3) {
    public CreateBugemonDTO {
        Objects.requireNonNull(name, "The name of the bugemon is required");
        Objects.requireNonNull(type, "The type of the bugemon is required");
        Objects.requireNonNull(spriteUrl, "The sprite current file path is required");
        Objects.requireNonNull(isStarter, "The starter status is required");
        Objects.requireNonNull(attack1, "Attack 1 is required");
        Objects.requireNonNull(attack2, "Attack 2 is required");
        Objects.requireNonNull(attack3, "Attack 3 is required");

        if (name.isBlank()) {
            throw new IllegalArgumentException("The name cannot be blank");
        }
    }
}
