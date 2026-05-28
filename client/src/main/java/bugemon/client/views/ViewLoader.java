package bugemon.client.views;

import java.io.IOException;
import java.util.function.Supplier;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import bugemon.client.views.exceptions.ViewLoadingException;

/**
 * Utility that loads a {@link View} from its FXML path and injects the loaded root back into the view instance.
 */
public class ViewLoader {

    private ViewLoader() {
    }

    /**
     * Loads the FXML declared by the view's {@link View#getPath()}, sets the view as its own controller, and returns
     * the fully initialised view.
     *
     * @param viewClass
     *            supplier called twice: once to resolve the FXML path, once as the controller factory
     */
    public static <V extends View> V load(Supplier<V> viewClass) {
        String fxmlPath = viewClass.get().getPath();
        try {
            FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource(fxmlPath));
            loader.setControllerFactory(cls -> viewClass.get());
            Parent root = loader.load();
            V view = loader.getController();
            view.initRoot(root);
            return view;
        } catch (IOException e) {
            throw new ViewLoadingException("Failed to load FXML: " + fxmlPath, e);
        } catch (Exception e) {
            throw new ViewLoadingException("Failed to instantiate view class: " + ViewLoader.class.getName(), e);
        }
    }
}
