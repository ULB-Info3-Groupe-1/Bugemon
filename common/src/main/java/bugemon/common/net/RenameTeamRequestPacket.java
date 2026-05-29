package bugemon.common.net;

import bugemon.common.models.team.Team;

/**
 * Request asking the server to rename a team (delete the old record and save it under the new name).
 *
 * <p>
 * The reply is a {@link TeamOpResponsePacket}: {@code NAME_EMPTY} if {@code newName} is blank, {@code NOT_FOUND} if the
 * team does not exist.
 *
 * @param team
 *            the team to rename (carries the current members and name)
 * @param newName
 *            the desired new name
 */
public record RenameTeamRequestPacket(Team team, String newName) implements Packet {
}
