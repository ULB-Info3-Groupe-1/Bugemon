package ulb.views;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import ulb.Configuration;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.views.components.BugemonCardView;

public class CreateBugemonView extends View {

    private static final String ATTACK_COUNT_INCOMPLETE = "attack-count-incomplete";
    private static final String ATTACK_COUNT_COMPLETE = "attack-count-complete";
    private static final String INVALID_FORM = "Formulaire invalide";

    private Listener listener;

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
    private ToggleGroup typeToggleGroup;

    private final Map<String, Attack> attacksByName = new HashMap<>();

    @FXML
    private void initialize() {
        this.attackListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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
                } else if (selectionModel.getSelectedItems().size() < Bugemon.ATTACKS_COUNT) {
                    selectionModel.select(index);
                }

                this.updateAttackCountLabel();
                event.consume();
            });

            return cell;
        });

        this.attackListView.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<String>) change -> this.updateAttackCountLabel());

        this.healthSlider.valueProperty()
                .addListener((obs, oldVal, newVal) -> this.healthLabel.setText(String.format("Vie (%.0f)", newVal)));

        this.attackSlider.valueProperty().addListener(
                (obs, oldVal, newVal) -> this.attackLabel.setText(String.format("Attaque (%.0f)", newVal)));

        this.defenseSlider.valueProperty().addListener(
                (obs, oldVal, newVal) -> this.defenseLabel.setText(String.format("Défense (%.0f)", newVal)));

        this.initiativeSlider.valueProperty().addListener(
                (obs, oldVal, newVal) -> this.initiativeLabel.setText(String.format("Initiative (%.0f)", newVal)));

    }

    private void updateAttackCountLabel() {
        int selectedCount = this.attackListView.getSelectionModel().getSelectedItems().size();
        this.attackCountLabel.setText("Attaques sélectionnées : " + selectedCount + "/" + Bugemon.ATTACKS_COUNT);

        if (selectedCount == Bugemon.ATTACKS_COUNT) {
            this.attackCountLabel.getStyleClass().removeAll(ATTACK_COUNT_INCOMPLETE);
            this.attackCountLabel.getStyleClass().add(ATTACK_COUNT_COMPLETE);
        } else {
            this.attackCountLabel.getStyleClass().removeAll(ATTACK_COUNT_COMPLETE);
            this.attackCountLabel.getStyleClass().add(ATTACK_COUNT_INCOMPLETE);
        }
    }

    @FXML
    private void onLoadButtonClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionnez un sprite pour votre bugemon");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Fichiers PNG", "*.png"));
        Stage stage = (Stage) this.getRoot().getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            this.listener.onSpriteSelected(file);

            // TODO: this should be decided by the controller, not in this func
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
        ToggleButton selectedButton = (ToggleButton) this.typeToggleGroup.getSelectedToggle();
        if (selectedButton != null) {
            BugemonType selectedType = (BugemonType) selectedButton.getUserData();
            this.listener.onTypeSelected(selectedType);
        }

    }

    @FXML
    private void onSaveClicked() {
        String bugemonName = this.bugemonNameTextField.getText();
        double healthValue = this.healthSlider.getValue();
        double attackValue = this.attackSlider.getValue();
        double defenseValue = this.defenseSlider.getValue();
        double initiativeValue = this.initiativeSlider.getValue();

        List<Attack> attacks = List.of(this.getSelectedAttack1(), this.getSelectedAttack2(), this.getSelectedAttack3());

        this.listener.onAdd(bugemonName, healthValue, attackValue, defenseValue, initiativeValue, attacks);
    }

    @FXML
    private void onReturnClicked() {
        this.resetView();
        this.listener.onReturnToMainMenu();
    }

    private void resetView() {
        this.bugemonNameTextField.setText("");
        this.healthSlider.setValue(0);
        this.attackSlider.setValue(0);
        this.defenseSlider.setValue(0);
        this.initiativeSlider.setValue(0);
        this.bugemonCardView.removeSprite();
        this.attackListView.getItems().clear();
        this.typeToggleGroup.getToggles().forEach(toggle -> toggle.setSelected(false));
        this.attackCountLabel.setText("Attaques sélectionnées : 0/" + Bugemon.ATTACKS_COUNT);
        this.attackCountLabel.getStyleClass().removeAll(ATTACK_COUNT_INCOMPLETE, ATTACK_COUNT_COMPLETE);
    }

    /**
     * Registers the listener that receives all user interaction events from this view.
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.CREATE_BUGEMON_VIEW;
    }

    @Override
    public void refresh() {
        //
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

    public void showInvalidFormChooseBugemonType() {
        this.showWarningAlert(INVALID_FORM, "Choisissez un type pour votre Bugemon.");
    }

    public void showInvalidFormChooseSprite() {
        this.showWarningAlert(INVALID_FORM, "Choisissez un sprite pour votre Bugemon.");
    }

    public void showInvalidFormChooseAttacks() {
        // TODO: replace "trois" with a number constant directly from the bugemon class
        this.showWarningAlert(INVALID_FORM, "Vous devez choisir trois attaques pour votre Bugemon.");
    }

    public void showSaveSuccessAlert(String name) {
        this.showInfoAlert("Bugemon sauvegardé", "Le Bugemon " + name + " a bien été sauvegardé.");
    }

    public void showBugemonNameEmptyAlert() {
        this.showWarningAlert(INVALID_FORM, "Le nom du Bugemon ne peut pas être vide.");
    }

    public void showBugemonNameAlreadyUsedAlert() {
        this.showWarningAlert("Nom de Bugemon deja utilisé",
                "Le nom du Bugemon que vous avez choisi est deja utilisé.");
    }

    public interface Listener {
        void onTypeSelected(BugemonType selectedType);

        void onSpriteSelected(File selectedSprite);

        void onAdd(String bugemonName, double healthValue, double attackValue, double defenseValue,
                double initiativeValue, List<Attack> attacks);

        void onReturnToMainMenu();
    }

}
