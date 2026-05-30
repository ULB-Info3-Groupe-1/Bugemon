package ulb.views;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Base class for all JavaFX views. Navigation swaps the root of the application's single {@link javafx.scene.Scene} via
 * {@link #show(Stage)}, avoiding the resize flash that occurs when replacing the scene itself.
 */
public abstract class View {
    protected Parent root;

    /**
     * Called by {@link ViewLoader} after the FXML root has been loaded and injected.
     */
    public void initRoot(Parent newroot) {
        this.root = newroot;
    }

    /**
     * Returns the FXML resource path used by {@link ViewLoader} to load this view.
     */
    protected abstract String getPath();

    /**
     * Reads the current state from the model and updates every UI component. Called by the controller after any model
     * mutation; the controller never pushes data into the view.
     */
    public abstract void refresh();

    /**
     * Replaces the scene's root with this view's root, keeping the stage size stable.
     */
    public void show(Stage stage) {
        stage.getScene().setRoot(this.root);
        stage.show();
    }

    /** Visual flavour of a dialog, used to colour its title. */
    private enum DialogKind {
        INFO("dialog-info"),
        WARNING("dialog-warning"),
        ERROR("dialog-error"),
        CONFIRM("dialog-confirm");

        private final String styleClass;

        DialogKind(String styleClass) {
            this.styleClass = styleClass;
        }

        String styleClass() {
            return this.styleClass;
        }
    }

    /** Displays a warning dialog with the given title and message. */
    protected void showWarningAlert(String title, String message) {
        this.showDialog(title, message, DialogKind.WARNING, "OK");
    }

    protected void showInfoAlert(String title, String message) {
        this.showDialog(title, message, DialogKind.INFO, "OK");
    }

    protected void showErrorAlert(String title, String message) {
        this.showDialog(title, message, DialogKind.ERROR, "OK");
    }

    /**
     * Displays a confirmation dialog with the given title and message and two buttons. If the user clicks the first
     * button its text is returned, otherwise (second button, Escape or close) the second button's text is returned.
     *
     * @param title
     *            the title of the dialog
     * @param message
     *            the message of the dialog
     * @param button1Text
     *            the text of the first button
     * @param button2Text
     *            the text of the second button
     * @return the text of the clicked button
     */
    protected String showAlertWithTwoButtons(String title, String message, String button1Text, String button2Text) {
        return this.showDialog(title, message, DialogKind.CONFIRM, button1Text, button2Text);
    }

    /**
     * Builds and shows an in-game styled, application-modal dialog centred over the current window, blocking until the
     * player dismisses it. Buttons are laid out left to right, the first being the primary action.
     *
     * @return the label of the clicked button, or the last label if the dialog is dismissed with Escape
     */
    private String showDialog(String title, String message, DialogKind kind, String... buttonLabels) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().addAll("game-dialog-title", kind.styleClass());
        titleLabel.setWrapText(true);

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("game-dialog-message");
        messageLabel.setWrapText(true);

        HBox buttonBar = new HBox();
        buttonBar.getStyleClass().add("game-dialog-buttons");
        buttonBar.setAlignment(Pos.CENTER);

        VBox panel = new VBox(titleLabel, messageLabel, buttonBar);
        panel.getStyleClass().addAll("settings-dialog", "game-dialog");
        panel.setAlignment(Pos.TOP_CENTER);

        StackPane overlay = new StackPane(panel);
        overlay.getStyleClass().add("settings-overlay");

        Scene ownerScene = this.root != null ? this.root.getScene() : null;
        Window owner = ownerScene != null ? ownerScene.getWindow() : null;

        Stage dialogStage = new Stage(StageStyle.TRANSPARENT);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            dialogStage.initOwner(owner);
        }

        Scene scene = new Scene(overlay);
        scene.setFill(Color.TRANSPARENT);
        if (ownerScene != null) {
            scene.getStylesheets().addAll(ownerScene.getStylesheets());
        }
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                dialogStage.close();
            }
        });
        dialogStage.setScene(scene);

        // Default result is the last label: the negative/cancel choice when dismissed with Escape.
        String[] result = {buttonLabels.length > 0 ? buttonLabels[buttonLabels.length - 1] : null};
        for (int i = 0; i < buttonLabels.length; i++) {
            String label = buttonLabels[i];
            Button button = new Button(label);
            button.getStyleClass().addAll("btn", i == 0 ? "btn-primary" : "btn-secondary", "game-dialog-btn");
            button.setDefaultButton(i == 0);
            button.setOnAction(event -> {
                result[0] = label;
                dialogStage.close();
            });
            buttonBar.getChildren().add(button);
        }

        if (owner != null) {
            dialogStage.setOnShown(event -> {
                dialogStage.setX(owner.getX() + (owner.getWidth() - dialogStage.getWidth()) / 2);
                dialogStage.setY(owner.getY() + (owner.getHeight() - dialogStage.getHeight()) / 2);
                if (!buttonBar.getChildren().isEmpty()) {
                    buttonBar.getChildren().get(0).requestFocus();
                }
            });
        }

        dialogStage.showAndWait();
        return result[0];
    }

    protected void showNoActiveTeamAlert(String message) {
        this.showWarningAlert("Aucune équipe active", message);
    }

    public void showAlertChooseTeamToLaunchCombat() {
        this.showNoActiveTeamAlert("Veuillez choisir une equipe pour lancer un combat.");
    }

    protected Parent getRoot() {
        return this.root;
    }
}
