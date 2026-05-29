package bugemon.common.net;

/**
 * Request asking the server for the immutable game catalogue (species, attacks, items, skill tree) needed by the client
 * to run combat, rewards and level-ups locally. The reply is a {@link StaticDataPacket}.
 */
public record GetStaticDataRequestPacket() implements Packet {
}
