package ulb.repositories.dto;

import java.net.URL;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.BugemonType;

/**
 * Data transfer object for creating a new Bugemon.
 *
 * @param name
 *            the name of the bugemon
 * @param type
 *            the type of the bugemon
 * @param spriteUrl
 *            the sprite url of the bugemon
 * @param defense
 *            the defense of the bugemon
 * @param attack
 *            the attack of the bugemon
 * @param initiative
 *            the initiative of the bugemon
 * @param maxHp
 *            the max hp of the bugemon
 * @param isStarter
 *            whether the bugemon is a starter
 * @param attack1
 *            the first attack of the bugemon
 * @param attack2
 *            the second attack of the bugemon
 * @param attack3
 *            the third attack of the bugemon
 */
public record CreateBugemonDTO(String name, BugemonType type, URL spriteUrl, int defense, int attack, int initiative,
        int maxHp, boolean isStarter, Attack attack1, Attack attack2, Attack attack3) {
}
