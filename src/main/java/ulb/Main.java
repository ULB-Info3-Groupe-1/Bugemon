package ulb;

import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;

public class Main extends Application {

    private String STAGE_TITLE = "Bugemon";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setMaximized(true);
            Rectangle2D rectangle2d = Screen.getPrimary().getVisualBounds();
            primaryStage.setMinWidth(rectangle2d.getWidth() * 0.6);
            primaryStage.setMinHeight(rectangle2d.getHeight() * 0.5);
            primaryStage.setTitle(STAGE_TITLE);

            MetaController controller = new MetaController(primaryStage);
            controller.switchTo(Window.MAIN_MENU);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}
