/**
 * PostgreSQL-backed implementations of the repository interfaces defined in {@link bugemon.server.repositories}.
 *
 * <p>
 * All concrete classes extend {@link bugemon.server.repositories.postgres.AbstractRepository}, which provides named-query
 * execution helpers. SQL strings are injected at construction time from the map produced by
 * {@link bugemon.server.repositories.QueryLoader}.
 *
 * <p>
 * {@link bugemon.server.repositories.postgres.DatabaseInitializer} runs the DDL schema and seeds static game data on first launch.
 * {@link bugemon.server.repositories.postgres.PostgresDatabaseConnection} manages the underlying JDBC connection using credentials
 * from a {@code .env} file.
 */
package bugemon.server.repositories.postgres;
