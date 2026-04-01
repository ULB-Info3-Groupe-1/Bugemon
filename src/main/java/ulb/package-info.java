/**
 * Root package of the Bugémon application.
 *
 * <p>
 * The Bugémon project is a Pokémon-inspired game developed as part of a ULB project. This package
 * contains the main entry points and coordinates the high-level architecture of the application.
 * </p>
 *
 * <h2>Architecture Overview</h2>
 * <p>
 * The application follows a Model-View-Controller (MVC) architecture to ensure separation of
 * concerns and maintainability:
 * </p>
 * <ul>
 * <li>{@link ulb.models} — <b>The Model</b>: Contains the core game logic, entities (Bugemons,
 * Trainers), and the combat system. It is decoupled from the UI and provides data through
 * DTOs.</li>
 * <li>{@link ulb.views} — <b>The View</b>: Handles the JavaFX visual representation and user
 * interface layout, defined through FXML and CSS.</li>
 * <li>{@link ulb.controllers} — <b>The Controller</b>: Mediates between the model and the view,
 * handling user input and updating the application state through the
 * {@link ulb.controllers.MetaController}.</li>
 * </ul>
 *
 * <h2>Supporting Packages</h2>
 * <ul>
 * <li>{@link ulb.common} — Shared Data Transfer Objects (DTOs) and common interfaces used for safe
 * communication between layers.</li>
 * <li>{@link ulb.utils} — Utility classes for JSON parsing and data deserialization.</li>
 * <li>{@link ulb.factory} — Creational patterns for complex object initialization.</li>
 * </ul>
 *
 * <h2>Entry Points</h2>
 * <ul>
 * <li>{@link ulb.Main} — The primary entry point that initializes the JavaFX application.</li>
 * <li>{@link ulb.AppLauncher} — A helper class used to launch the application, ensuring the JavaFX
 * environment is correctly set up.</li>
 * </ul>
 *
 * @see ulb.models
 * @see ulb.views
 * @see ulb.controllers
 */
package ulb;
