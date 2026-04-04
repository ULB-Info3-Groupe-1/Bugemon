package ulb.views;

import java.io.IOException;
import java.util.function.Supplier;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ViewLoader {

    public static <V extends View> V load(Supplier<V> viewClass) {
        String fxmlPath = viewClass.get().getPath();
        try {
            FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource(fxmlPath));
            loader.setControllerFactory(cls -> viewClass.get());
            Parent root = loader.load();
            V view = loader.getController();
            view.initScene(root);
            return view;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate view class: " + viewClass.getClass().toString(), e);
        }
    }
}
