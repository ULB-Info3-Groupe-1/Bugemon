package ulb.controllers;

import javafx.stage.Stage;
import ulb.views.View;

public abstract class Controller<T extends View> {

    protected final MetaController metaController;
    protected final T view;

    /**
     * Constructor of the controller.
     * @param metaController the meta controller to which this controller belongs
     * @param view the view associated to this controller
     */
    public Controller(MetaController metaController, T view) {
        this.metaController = metaController;
        this.view = view;
    }

    /**
     * Show the view of this controller on the given stage.
     * @param stage the stage on which to show the view
     */
    public void show(Stage stage) {
        this.view.show(stage);
    }

}
