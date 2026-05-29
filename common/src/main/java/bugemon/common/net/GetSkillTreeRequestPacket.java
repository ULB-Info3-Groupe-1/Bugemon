package bugemon.common.net;

/**
 * Request asking the server for the static skill-tree definition so the client can render it.
 *
 * <p>
 * The skill tree is player-independent static data. The reply is a {@link SkillTreeResponsePacket}.
 */
public record GetSkillTreeRequestPacket() implements Packet {
}
