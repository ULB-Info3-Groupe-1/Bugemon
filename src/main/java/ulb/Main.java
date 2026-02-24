package ulb;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            MetaController controller = new MetaController(primaryStage);
            controller.switchTo(Window.MAIN_MENU);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}
