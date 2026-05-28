package bugemon.client.views.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import bugemon.common.Configuration;

/**
 * Reusable custom component displaying a scrolling dialog text area with a configurable "Next" button. The button can
 * be disabled to block progression until an animation finishes; clicks are forwarded through {@link Listener}.
 */
public class DialogZoneView extends ComponentView {

    @FXML
    private Text dialogText;
    @FXML
    private Button nextButton;

    private Listener listener;

    public DialogZoneView() {
        super(Configuration.Paths.Fxml.COMPONENT_DIALOG_ZONE);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onNextButtonClicked() {
        this.listener.onNext();
    }

    public void setDialogText(String text) {
        this.dialogText.setText(text);
    }

    public void clearDialog() {
        this.dialogText.setText("");
    }

    public void setButtonText(String text) {
        this.nextButton.setText(text);
    }

    public void setNextButtonDisabled(boolean disabled) {
        this.nextButton.setDisable(disabled);
    }

    /** Callback interface for the dialog zone next button. */
    public interface Listener {

        /** Called when the player clicks the next button. */
        void onNext();

    }
}
