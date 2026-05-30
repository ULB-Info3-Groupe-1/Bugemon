package ulb.views;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import ulb.Configuration;

/**
 * Settings dialog shown as an application-modal overlay above the current screen. Lets the player adjust the master
 * audio volume and switch between fullscreen and windowed display. Like every other {@link View} it owns no application
 * logic: it forwards changes to a {@link Listener} and reflects state handed to it via
 * {@link #initState(double, boolean)}.
 */
public class SettingsView extends View {

    private static final String MODE_ACTIVE_CLASS = "mode-active";

    @FXML
    private Slider volumeSlider;
    @FXML
    private Label volumeValueLabel;
    @FXML
    private Button fullscreenButton;
    @FXML
    private Button windowedButton;

    private Listener listener;
    private Stage dialogStage;

    /** The two display modes the player can toggle between. */
    public enum DisplayMode {
        FULLSCREEN,
        WINDOWED
    }

    @FXML
    private void initialize() {
        this.volumeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            int percent = (int) Math.round(newValue.doubleValue());
            this.volumeValueLabel.setText(percent + " %");
            if (this.listener != null) {
                this.listener.onVolumeChanged(percent / 100.0);
            }
        });
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Seeds the controls with the current model state. Called by the controller just before {@link #showModal(Stage)}.
     *
     * @param volume
     *            current master volume in {@code [0.0, 1.0]}
     * @param fullscreen
     *            {@code true} if the stage is currently fullscreen
     */
    public void initState(double volume, boolean fullscreen) {
        int percent = (int) Math.round(volume * 100);
        this.volumeSlider.setValue(percent);
        this.volumeValueLabel.setText(percent + " %");
        this.markActiveMode(fullscreen);
    }

    @FXML
    private void onFullscreenSelected() {
        this.markActiveMode(true);
        if (this.listener != null) {
            this.listener.onDisplayModeChanged(DisplayMode.FULLSCREEN);
        }
    }

    @FXML
    private void onWindowedSelected() {
        this.markActiveMode(false);
        if (this.listener != null) {
            this.listener.onDisplayModeChanged(DisplayMode.WINDOWED);
        }
    }

    @FXML
    private void onCloseClicked() {
        if (this.dialogStage != null) {
            this.dialogStage.close();
        }
    }

    /** Highlights whichever display-mode button matches the active mode. */
    private void markActiveMode(boolean fullscreen) {
        this.fullscreenButton.getStyleClass().remove(MODE_ACTIVE_CLASS);
        this.windowedButton.getStyleClass().remove(MODE_ACTIVE_CLASS);
        (fullscreen ? this.fullscreenButton : this.windowedButton).getStyleClass().add(MODE_ACTIVE_CLASS);
    }

    /**
     * Shows this panel as an application-modal dialog centred over {@code owner}. The dialog is created once and reused
     * on subsequent calls. Escape or the close button dismisses it.
     *
     * @param owner
     *            the stage the dialog blocks and centres over
     */
    public void showModal(Stage owner) {
        if (this.dialogStage == null) {
            this.dialogStage = new Stage(StageStyle.TRANSPARENT);
            this.dialogStage.initOwner(owner);
            this.dialogStage.initModality(Modality.APPLICATION_MODAL);

            Scene scene = new Scene(this.root);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().addAll(owner.getScene().getStylesheets());
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    this.dialogStage.close();
                }
            });
            this.dialogStage.setScene(scene);
        }

        this.dialogStage.show();
        this.dialogStage.setX(owner.getX() + (owner.getWidth() - this.dialogStage.getWidth()) / 2);
        this.dialogStage.setY(owner.getY() + (owner.getHeight() - this.dialogStage.getHeight()) / 2);
    }

    @Override
    public void refresh() {
        // Nothing to refresh: state is pushed explicitly via initState before each showing.
    }

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.SETTINGS_VIEW;
    }

    /** Callbacks fired when the player changes a setting. */
    public interface Listener {
        void onVolumeChanged(double volume);

        void onDisplayModeChanged(DisplayMode mode);
    }
}
