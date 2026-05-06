package ulb.repositories.dto;

/**
 * Data transfer object for a team
 * @param playername
 *            the name of the player
 * @param teamName
 *            the name of the team
 */
public record TeamDTO(String playername, String teamName) {
}
