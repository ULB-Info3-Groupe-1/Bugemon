/**
 * Data access layer. All database interactions go through {@link ulb.repositories.StaticDataRepository} and
 * {@link ulb.repositories.PlayerRepository}.
 *
 * On construction, {@code DatabaseRepository} loads all SQL queries from {@code resources/sql/*.sql}, creates the
 * schema if absent, and bootstraps static game data. See {@code team/rules.md} for the required SQL file format.
 *
 * {@link ulb.repositories.DatabaseConnection} holds the single PostgreSQL connection (credentials via {@code .env}).
 * {@link ulb.repositories.PlayerRepository} and {@link ulb.repositories.StaticDataRepository} are internal delegates —
 * callers should only use {@code DatabaseRepository}.
 *
 * DTOs ({@link ulb.repositories.dto.PlayerBugemonDTO}, {@link ulb.repositories.dto.TeamDTO},
 * {@link ulb.repositories.dto.TeamMemberDTO}) are plain records used to carry data between layers.
 */
package ulb.repositories;
