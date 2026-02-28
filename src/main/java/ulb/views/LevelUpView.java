package ulb.views;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ulb.controllers.LevelUpController;

/**
 * LevelUpView
 *
 * View for the level up screen.
 */
public class LevelUpView extends View {

    @FXML
    private Label TextLevelUp;
    @FXML
    private Button Stat1;
    @FXML
    private Button Stat2;
    @FXML
    private Button Stat3;
    @FXML
    private ImageView imageBugemon;

    private LevelUpController controller;

    public LevelUpView() throws IOException {
        super("/fxml/LevelUp.fxml");
        this.controller = null;

        this.Stat1.setOnAction((e) -> this.controller.chooseOption(1));


        this.Stat2.setOnAction((e) -> this.controller.chooseOption(2));


        this.Stat3.setOnAction((e) -> this.controller.chooseOption(3));
    }
    public void setLevelUp() {
        StringBuilder texte = new StringBuilder();
        String filename = "ressources/Assets/png/" + LevelUpDTO.getBugemonName() + ".png";
        Image image = new Image(filename);

        texte.append(LevelUpDTO.getBugemonName());
        texte.append(" a atteint le niveau ");
        texte.append(LevelUpDTO.getNextLevel());

        TextLevelUp.setText(texte.toString());
        imageBugemon.setImage(image);
    }
    /**
     * Binds Level up view to its controller.
     *
     * @param controller controller handling level up
     */
    public void setController(LevelUpController controller) {
        this.controller = controller;
    }


}
