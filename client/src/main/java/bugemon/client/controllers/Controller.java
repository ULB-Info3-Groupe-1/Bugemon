package bugemon.client.controllers;

import bugemon.client.views.View;

/**
 * Abstract base class for all screen controllers. Binds a {@link MetaController} to a typed {@link View}; subclasses
 * implement screen-specific logic and expose callbacks that the view invokes on player interaction.
 *
 * @param <T>
 *            the concrete {@link View} type managed by this controller.
 */
public abstract class Controller<T extends View> {
    protected final MetaController metaController;
    protected final T view;

    protected Controller(MetaController metaController, T view) {
        this.metaController = metaController;
        this.view = view;
    }

    protected void show() {
        this.view.refresh();
        this.metaController.showView(this.view);
    }
}
