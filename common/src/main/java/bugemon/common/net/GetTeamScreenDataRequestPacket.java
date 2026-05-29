package bugemon.common.net;

/**
 * Request asking the server for everything the team-management screen needs in one round-trip: the player's selectable
 * Bugemons and their saved teams.
 *
 * <p>
 * The reply is a {@link TeamScreenDataPacket}. The client caches it and answers all read queries (team names, lookups,
 * "is saved" checks) locally, sending only mutations back to the server.
 */
public record GetTeamScreenDataRequestPacket() implements Packet {
}
