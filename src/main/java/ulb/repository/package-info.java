/**
 * Data access layer. All database interactions go through {@link ulb.repository.DatabaseRepository}.
 *
 * <p>
 * On construction, {@code DatabaseRepository} loads all SQL queries from {@code resources/sql/*.sql}, creates the
 * schema if absent, and bootstraps static game data. See {@code team/rules.md} for the required SQL file format.
 *
 * <p>
 * {@link ulb.repository.DatabaseConnection} holds the single PostgreSQL connection (credentials via {@code .env}).
 * {@link ulb.repository.UserRepository} and {@link ulb.repository.StaticDataRepository} are internal delegates —
 * callers should only use {@code DatabaseRepository}.
 *
 * <p>
 * DTOs ({@link ulb.repository.dto.UserBugemonDTO}, {@link ulb.repository.dto.TeamDTO},
 * {@link ulb.repository.dto.TeamMemberDTO}) are plain records used to carry data between layers.
 */
package ulb.repository;
