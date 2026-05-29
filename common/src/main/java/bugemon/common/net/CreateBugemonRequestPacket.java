package bugemon.common.net;

import java.util.List;

import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.bugemon.ElementType;

/**
 * Request asking the server to create and persist a new custom Bugemon species.
 *
 * <p>
 * The sprite travels as raw bytes (not a local-filesystem URL) so the server can store it regardless of where the
 * client picked the file. The stat values are passed in the same order the client computes them; the server assembles
 * the persistence DTO. The reply is a {@link CreateBugemonResponsePacket}.
 *
 * @param name
 *            the species name
 * @param type
 *            the element type
 * @param attackValue
 *            rounded attack stat as entered by the player
 * @param defenseValue
 *            rounded defense stat as entered by the player
 * @param initiativeValue
 *            rounded initiative stat as entered by the player
 * @param hpValue
 *            rounded max-HP stat as entered by the player
 * @param attacks
 *            the attacks assigned to the species
 * @param spriteBytes
 *            the raw bytes of the chosen sprite image
 */
public record CreateBugemonRequestPacket(String name, ElementType type, int attackValue, int defenseValue,
        int initiativeValue, int hpValue, List<Attack> attacks, byte[] spriteBytes) implements Packet {
}
