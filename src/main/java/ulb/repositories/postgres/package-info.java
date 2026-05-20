/**
 * Data access layer.
 *
 * On startup, {@link ulb.repositories.postgres.QueryLoader} loads all SQL queries from {@code resources/sql/*.sql}.
 * Repositories execute these queries using {@link ulb.repositories.postgres.DatabaseConnection}.
 *
 * {@link ulb.repositories.postgres.PlayerRepository} and {@link ulb.repositories.BugemonRepository} implement
 * persistence for player state and static game data.
 *
 * DTOs ({@link ulb.common.dto.PlayerBugemonDTO}, {@link ulb.repositories.dto.TeamDTO},
 * {@link ulb.repositories.dto.TeamMemberDTO}) are plain records used to carry data between layers.
 */
package ulb.repositories.postgres;
