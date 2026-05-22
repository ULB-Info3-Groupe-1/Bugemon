package ulb.common.dto.persistence;

import java.net.URL;
import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.ElementType;

/**
 * Data required to insert a new Bugemon definition into the database for the first time.
 *
 * <p>
 * This DTO is produced by the JSON parser and consumed exclusively by {@code DatabaseInitializer} during the seeding
 * phase; it is never used at runtime.
 *
 * @param name
 *            unique display name of the Bugemon species
 * @param type
 *            elemental type
 * @param spriteUrl
 *            classpath URL pointing to the Bugemon's sprite image
 * @param defense
 *            base defense stat
 * @param attack
 *            base attack stat
 * @param initiative
 *            base initiative stat
 * @param maxHp
 *            base max-HP stat
 * @param isStarter
 *            {@code true} if this Bugemon can be chosen as a starter
 * @param attacks
 *            list of attacks the species knows by default
 */
public record CreateBugemonDTO(String name, ElementType type, URL spriteUrl, int defense, int attack, int initiative,
        int maxHp, boolean isStarter, List<Attack> attacks) {
}
