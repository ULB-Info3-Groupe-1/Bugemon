package ulb.views;

import java.io.IOException;

import ch.qos.logback.core.joran.action.Action;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.effect.EffectStat;
import ulb.views.components.BugemonCardView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

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
        String bugemonName = bugemonNameTextField.getText();
        double healthValue = healthSlider.getValue();
        double attackValue = attackSlider.getValue();
        double defenseValue = defenseSlider.getValue();
        double initiativeValue = initiativeSlider.getValue();

        this.listener.onSave(bugemonName, healthValue, attackValue, defenseValue, initiativeValue);
    }

    @FXML
    private void onSliderChanged(MouseEvent event) {
        Slider slider = (Slider) event.getSource();
        EffectStat stat = (EffectStat) slider.getUserData();
        double value = slider.getValue();

        switch (stat) {
            case HP -> {
                healthLabel.setText(String.format("Vie (%.0f)", value));
            }
            case ATTACK -> {
                attackLabel.setText(String.format("Attaque (%.0f)", value));
            }
            case DEFENSE -> {
                defenseLabel.setText(String.format("Défense (%.0f)", value));
            }
            case INITIATIVE -> {
                initiativeLabel.setText(String.format("Initiative (%.0f)", value));
            }
        }
    }

    /**
     * Registers the listener that receives all user interaction events from this
     * view.
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
    }

}
