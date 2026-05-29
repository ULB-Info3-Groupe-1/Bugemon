package bugemon.common.net;

import java.util.List;

import bugemon.common.models.bugemon.Attack;

/**
 * Server reply carrying every static attack definition, requested via {@link GetAttacksRequestPacket}.
 *
 * @param attacks
 *            all attacks across all element types
 */
public record AttacksResponsePacket(List<Attack> attacks) implements Packet {
}
