package ulb.views.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

/** Reusable custom component displaying a dialog zone with text and a next button. */
public class DialogZoneView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/DialogZone.fxml";

    @FXML
    private Text dialogText;
    @FXML
    private Button nextButton;

    private Runnable onNext;

    public DialogZoneView() {
        super(FXML_PATH);
    }

    /** Sets the callback invoked when the user clicks the next button. */
    public void setOnNext(Runnable callback) {
        this.onNext = callback;
    }

    @FXML
    private void onNextButtonClicked() {
        if (this.onNext != null) {
            this.onNext.run();
        }
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
}
