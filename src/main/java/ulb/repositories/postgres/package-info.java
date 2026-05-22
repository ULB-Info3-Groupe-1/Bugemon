/**
 * PostgreSQL-backed implementations of the repository interfaces defined in {@link ulb.repositories}.
 *
 * <p>
 * All concrete classes extend {@link ulb.repositories.postgres.AbstractRepository}, which provides named-query
 * execution helpers. SQL strings are injected at construction time from the map produced by
 * {@link ulb.repositories.QueryLoader}.
 *
 * <p>
 * {@link ulb.repositories.postgres.DatabaseInitializer} runs the DDL schema and seeds static game data on first launch.
 * {@link ulb.repositories.postgres.PostgresDatabaseConnection} manages the underlying JDBC connection using credentials
 * from a {@code .env} file.
 */
package ulb.repositories.postgres;
