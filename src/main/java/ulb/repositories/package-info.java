/**
 * Data access layer. SQL queries are loaded from {@code resources/sql/*.sql} at startup; each file follows the
 * {@code -- Query to ... / -- QueryName / SQL body} format.
 *
 * {@link ulb.repositories.DatabaseConnection} holds the single PostgreSQL connection.
 * {@link ulb.repositories.StaticDataRepository} creates the schema and bootstraps game data on first run.
 * {@link ulb.repositories.PlayerRepository} handles player, team, and Bugemon persistence.
 */
package ulb.repositories;
