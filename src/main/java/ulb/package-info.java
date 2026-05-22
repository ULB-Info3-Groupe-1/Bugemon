/**
 * Root package of the Bugemon application, a Pokémon-inspired JavaFX monster-battling game.
 *
 * <p>
 * Contains the three top-level application classes:
 * <ul>
 * <li>{@link ulb.AppLauncher} — fat-JAR entry point, avoids the early JavaFX module check.</li>
 * <li>{@link ulb.Main} — JavaFX {@link javafx.application.Application} subclass that wires the database, services, and
 * the root controller.</li>
 * <li>{@link ulb.Configuration} — compile-time constants organised by concern (paths, UI, game rules, skill-tree
 * defaults, floor-map generation parameters).</li>
 * </ul>
 *
 * <p>
 * The application follows a layered MVC architecture:
 * <ul>
 * <li><b>models</b> — domain entities and game logic.</li>
 * <li><b>services</b> — use-case orchestration, decoupled from persistence.</li>
 * <li><b>repositories</b> — PostgreSQL persistence behind repository interfaces.</li>
 * <li><b>controllers</b> — JavaFX controllers that mediate between services and views.</li>
 * <li><b>views</b> — FXML-backed JavaFX nodes.</li>
 * </ul>
 */
package ulb;
