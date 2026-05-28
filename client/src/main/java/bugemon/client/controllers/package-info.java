/**
 * Controller layer of the MVC architecture. Each controller manages one screen and delegates all navigation to
 * {@link bugemon.client.controllers.MetaController}, which is the sole authority for screen transitions.
 *
 * All controllers are instantiated once at startup and reused; FXML resources are loaded eagerly in each constructor.
 */
package bugemon.client.controllers;
