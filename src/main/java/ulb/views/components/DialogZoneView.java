package ulb.views.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import ulb.Configuration;

/** Reusable custom component displaying a dialog zone with text and a next button. */
public class DialogZoneView extends ComponentView {

    @FXML
    private Text dialogText;
    @FXML
    private Button nextButton;

    private Listener listener;

    /**
     * Default constructor.
     */
    public DialogZoneView() {
        super(Configuration.Paths.Fxml.COMPONENT_DIALOG_ZONE);
    }

    /**
     * Sets the listener to be notified when the next button is clicked.
     *
     * @param listener
     *            the listener
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onNextButtonClicked() {
        this.listener.onNext();
    }

    /**
     * Sets the text to be displayed in the dialog zone.
     *
     * @param text
     *            the text
     */
    public void setDialogText(String text) {
        this.dialogText.setText(text);
    }

    /**
     * Clears the dialog zone.
     */
    public void clearDialog() {
        this.dialogText.setText("");
    }

    /**
     * Sets the text of the next button.
     *
     * @param text
     *            the text
     */
    public void setButtonText(String text) {
        this.nextButton.setText(text);
    }

    /**
     * Sets the disabled state of the next button.
     *
     * @param disabled
     *            whether the button is disabled
     */
    public void setNextButtonDisabled(boolean disabled) {
        this.nextButton.setDisable(disabled);
    }

    public interface Listener {

        /**
         * Dispatches a next action to the controller.
         */
        void onNext();

    }
}
