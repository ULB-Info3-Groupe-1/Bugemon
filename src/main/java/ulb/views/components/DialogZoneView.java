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

    public DialogZoneView() {
        super(Configuration.Paths.FXML.COMPONENT_DIALOG_ZONE);
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

    public interface Listener {

        void onNext();

    }
}
