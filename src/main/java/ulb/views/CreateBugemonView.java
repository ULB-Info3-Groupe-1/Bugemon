package ulb.views;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.effect.EffectStat;
import ulb.views.components.BugemonCardView;

public class CreateBugemonView extends View {

    private static final String FXML_PATH = "/fxml/CreateBugemon.fxml";
    private static final String TYPE_SELECTED = "type-selected";
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
    private Label attackCountLabel;

    @FXML
    private Button floraTypeButton;

    @FXML
    private Button aquaTypeButton;

    @FXML
    private Button pyroTypeButton;

    @FXML
    private Button lithoTypeButton;

    private BugemonType selectedType;
    private URL selectedSpriteUrl;
    private final Map<String, Attack> attacksByName = new HashMap<>();

    @FXML
    private void initialize() {
        this.attackListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Permet le toggle au clic simple (sans Cmd/Ctrl) et bloque au-delà de 3 sélections.
        this.attackListView.setCellFactory(listView -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(empty ? null : item);
                }
            };

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (cell.isEmpty()) {
                    return;
                }

                int index = cell.getIndex();
                var selectionModel = this.attackListView.getSelectionModel();

                if (selectionModel.isSelected(index)) {
                    selectionModel.clearSelection(index);
                } else if (selectionModel.getSelectedItems().size() < 3) {
                    selectionModel.select(index);
                }

                this.updateAttackCountLabel();
                event.consume();
            });

            return cell;
        });

        this.attackListView.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<String>) change -> this.updateAttackCountLabel());
    }

    private void updateAttackCountLabel() {
        int selectedCount = this.attackListView.getSelectionModel().getSelectedItems().size();
        this.attackCountLabel.setText("Selected attacks: " + selectedCount + "/3");

        if (selectedCount == 3) {
            this.attackCountLabel.getStyleClass().removeAll("attack-count-incomplete");
            this.attackCountLabel.getStyleClass().add("attack-count-complete");
        } else {
            this.attackCountLabel.getStyleClass().removeAll("attack-count-complete");
            this.attackCountLabel.getStyleClass().add("attack-count-incomplete");
        }
    }

    @FXML
    private void onLoadButtonClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Bugemon Sprite");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png"));
        Stage stage = (Stage) this.getRoot().getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            this.bugemonCardView.setSprite(file);
            try {
                this.selectedSpriteUrl = file.toURI().toURL();
            } catch (MalformedURLException e) {
                this.selectedSpriteUrl = null;
            }
        }
    }

    @FXML
    private void onRemoveButtonClicked() {
        this.bugemonCardView.removeSprite();
        this.selectedSpriteUrl = null;
    }

    @FXML
    private void onKeyTyped() {
        String text = this.bugemonNameTextField.getText();
        this.bugemonCardView.setName(text);
    }

    @FXML
    private void onTypeClicked(ActionEvent event) throws IllegalArgumentException {
        Button button = (Button) event.getSource();

        this.selectedType = switch (button.getText()) {
            case "FLORA" -> BugemonType.FLORA;
            case "AQUA" -> BugemonType.AQUA;
            case "PYRO" -> BugemonType.PYRO;
            case "LITHO" -> BugemonType.LITHO;
            default -> throw new IllegalArgumentException("Unexpected type: " + button.getText());
        };

        this.updateTypeSelectionState(button);
        this.listener.onTypeSelected(this.selectedType);
    }

    private void updateTypeSelectionState(Button selectedButton) {
        this.floraTypeButton.getStyleClass().remove(TYPE_SELECTED);
        this.aquaTypeButton.getStyleClass().remove(TYPE_SELECTED);
        this.pyroTypeButton.getStyleClass().remove(TYPE_SELECTED);
        this.lithoTypeButton.getStyleClass().remove(TYPE_SELECTED);
        selectedButton.getStyleClass().add(TYPE_SELECTED);
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
            case HP -> this.healthLabel.setText(String.format("Vie (%.0f)", value));
            case ATTACK -> this.attackLabel.setText(String.format("Attaque (%.0f)", value));
            case DEFENSE -> this.defenseLabel.setText(String.format("Défense (%.0f)", value));
            case INITIATIVE -> this.initiativeLabel.setText(String.format("Initiative (%.0f)", value));
            default -> throw new IllegalArgumentException();
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
        return FXML_PATH;
    }

    @Override
    public void refresh() {
        //
    }

    public BugemonType getSelectedType() {
        return this.selectedType;
    }

    public URL getSelectedSpriteUrl() {
        return this.selectedSpriteUrl;
    }

    public void setAvailableAttacks(List<Attack> attacks) {
        this.attacksByName.clear();

        for (Attack attack : attacks) {
            this.attacksByName.put(attack.name(), attack);
        }

        this.attackListView.setItems(FXCollections.observableArrayList(this.attacksByName.keySet()));
        this.attackListView.getSelectionModel().clearSelection();
        this.updateAttackCountLabel();
    }

    public Attack getSelectedAttack1() {
        List<String> selectedNames = this.attackListView.getSelectionModel().getSelectedItems();
        if (selectedNames.isEmpty()) {
            return null;
        }
        return this.attacksByName.get(selectedNames.get(0));
    }

    public Attack getSelectedAttack2() {
        List<String> selectedNames = this.attackListView.getSelectionModel().getSelectedItems();
        if (selectedNames.size() < 2) {
            return null;
        }
        return this.attacksByName.get(selectedNames.get(1));
    }

    public Attack getSelectedAttack3() {
        List<String> selectedNames = this.attackListView.getSelectionModel().getSelectedItems();
        if (selectedNames.size() < 3) {
            return null;
        }
        return this.attacksByName.get(selectedNames.get(2));
    }

    public void showInvalidFormAlert(String message) {
        this.showAlert("Invalid Form", message);
    }

    public void showSaveSuccessAlert(String name) {
        this.showAlert("Bugemon Saved", "The Bugemon " + name + " has been saved successfully.");
    }

    public void showSaveErrorAlert(String message) {
        this.showAlert("Save Error", message);
    }

    // public void setModel()

    public interface Listener {
        void onTypeSelected(BugemonType selectedType);

        void onSave(String bugemonName, double healthValue, double attackValue, double defenseValue,
                double initiativeValue);

        void onReturnToMainMenu();
    }

}
