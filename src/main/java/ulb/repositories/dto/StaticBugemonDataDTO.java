package ulb.repositories.dto;

import java.util.List;

import ulb.models.bugemon.Attack;

/**
 * Data transfer object for a Bugemon with just the static data (immutable).
 *
 * @param name
 *            the name of the bugemon
 * @param type
 *            the type of the bugemon
 * @param spriteUrl
 *            the sprite url of the bugemon
 * @param attackList
 *            the list of attacks of the bugemon
 * @param isStarter
 *            whether the bugemon is a starter
 */
public record StaticBugemonDataDTO(String name, String type, String spriteUrl, List<Attack> attackList,
        boolean isStarter) {
}
