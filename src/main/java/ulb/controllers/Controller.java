package ulb.controllers;

import javafx.stage.Stage;

import ulb.views.View;

/**
 * Abstract base class for all screen controllers in the application.
 *
 * <p>
 * A {@code Controller} binds a {@link MetaController} (responsible for application-level
 * navigation) to a typed {@link View} (responsible for rendering a single screen). Concrete
 * subclasses implement the screen-specific logic and expose callback methods that the view can
 * invoke in response to user interactions.
 * </p>
 *
 * <p>
 * The type parameter {@code T} constrains the view to a specific {@link View} subtype, allowing
 * each concrete controller to access its view's dedicated API without casting.
 * </p>
 *
 * <p>
 * The {@link #show(Stage)} convenience method delegates directly to {@link View#show(Stage)}, so
 * the {@link MetaController} can display any controller's screen through a uniform interface.
 * </p>
 *
 * @param <T>
 *            the concrete {@link View} type managed by this controller.
 *
 * @see MetaController
 * @see View
 */
public abstract class Controller<T extends View> {
    /**
     * The application-level meta-controller used to trigger screen transitions and access shared
     * application state (e.g., the list of available Bugemons).
     */
    protected final MetaController metaController;

    /**
     * The view instance associated with this controller, typed to the concrete {@link View}
     * subclass so that screen-specific API calls require no cast.
     */
    protected final T view;

    /**
     * Constructs a {@code Controller} and associates it with the given {@link MetaController} and
     * {@link View}.
     *
     * <p>
     * Subclass constructors should call this via {@code super(metaController, view)} and then
     * typically call {@code view.setController(this)} to complete the two-way binding between view
     * and controller.
     * </p>
     *
     * @param metaController
     *            the application-level {@link MetaController} used for navigation and shared state
     *            access; must not be {@code null}.
     * @param view
     *            the {@link View} instance that this controller manages; must not be {@code null}.
     */
    protected Controller(MetaController metaController, T view) {
        this.metaController = metaController;
        this.view = view;
    }

    /**
     * Displays this controller's view on the given {@link Stage}.
     *
     * <p>
     * This method is called by the {@link MetaController} during a screen transition. It simply
     * delegates to {@link View#show(Stage)}, keeping the navigation logic entirely within the
     * controller layer.
     * </p>
     *
     * @param stage
     *            the {@link Stage} on which the view should be rendered; must not be {@code null}.
     */
    protected void show(Stage stage) {
        this.view.refresh();
        this.view.show(stage);
    }
}
