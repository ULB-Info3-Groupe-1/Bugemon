package ulb.views;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.effect.EffectStat;
import ulb.views.components.BugemonCardView;

public class CreateBugemonView extends View {

    private final String fxmlPath = "/fxml/CreateBugemon.fxml";
    private Listener listener;

    // TODO: liste d'attaques ? comment gérer ? autre objet ?
    @FXML
    private ListView<String> attackListView;

    @FXML
    private TextField bugemonNameTextField;

    @FXML
    private BugemonCardView bugemonCardView;

    @FXML
    private Slider healthSlider;

    @FXML
    private Label healthLabel;

    @FXML
    private Slider attackSlider;

    @FXML
    private Label attackLabel;

    @FXML
    private Slider defenseSlider;

    @FXML
    private Label defenseLabel;

    @FXML
    private Slider initiativeSlider;

    @FXML
    private Label initiativeLabel;

    @FXML
    private void initialize() {
        // TODO: add bugemon, add bugemoncard (sprite)
    }

    @FXML
    private void onLoadButtonClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir le sprite du Bugemon");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png"));
        Stage stage = (Stage) this.getRoot().getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            this.bugemonCardView.setSprite(file);
        }
    }

    @FXML
    private void onRemoveButtonClicked() {
        this.bugemonCardView.removeSprite();
    }

    @FXML
    private void onKeyTyped() {
        String text = this.bugemonNameTextField.getText();
        this.bugemonCardView.setName(text);
    }

    @FXML
    private void onTypeClicked(ActionEvent event) throws IllegalArgumentException {
        Button button = (Button) event.getSource();
        BugemonType selectedType;

        switch (button.getText()) {
            case "FLORA" -> selectedType = BugemonType.FLORA;
            case "AQUA" -> selectedType = BugemonType.AQUA;
            case "PYRO" -> selectedType = BugemonType.PYRO;
            case "LITHO" -> selectedType = BugemonType.LITHO;
            default -> throw new IllegalArgumentException();
        }

        this.listener.onTypeSelected(selectedType);
    }

    @FXML
    private void onSaveClicked() {
        String bugemonName = this.bugemonNameTextField.getText();
        double healthValue = this.healthSlider.getValue();
        double attackValue = this.attackSlider.getValue();
        double defenseValue = this.defenseSlider.getValue();
        double initiativeValue = this.initiativeSlider.getValue();

        this.listener.onSave(bugemonName, healthValue, attackValue, defenseValue, initiativeValue);
    }

    @FXML
    private void onReturnClicked() {
        this.listener.onReturnToMainMenu();
    }

    @FXML
    private void onSliderChanged(MouseEvent event) throws IllegalArgumentException {
        Slider slider = (Slider) event.getSource();
        EffectStat stat = (EffectStat) slider.getUserData();
        double value = slider.getValue();

        switch (stat) {
            case HP -> {
                this.healthLabel.setText(String.format("Vie (%.0f)", value));
            }
            case ATTACK -> {
                this.attackLabel.setText(String.format("Attaque (%.0f)", value));
            }
            case DEFENSE -> {
                this.defenseLabel.setText(String.format("Défense (%.0f)", value));
            }
            case INITIATIVE -> {
                this.initiativeLabel.setText(String.format("Initiative (%.0f)", value));
            }
            default -> {
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * Registers the listener that receives all user interaction events from this view.
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public String getPath() {
        return this.fxmlPath;
    }

    @Override
    public void refresh() {
        //
    }

    // public void setModel()

    public interface Listener {
        void onTypeSelected(BugemonType selectedType);

        void onSave(String bugemonName, double healthValue, double attackValue, double defenseValue,
                double initiativeValue);

        void onReturnToMainMenu();
    }

}
