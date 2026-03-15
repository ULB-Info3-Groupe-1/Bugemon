package ulb.fx_controllers.components;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Reusable custom component displaying a dialog zone.
 */
public class DialogZoneComponent extends VBox {
    private final static String FXML_PATH = "/fxml/DialogZone.fxml";

    @FXML private Text dialogText;
    @FXML private Button continueButton;
    @FXML private Text additionalInfo;

    private CountDownLatch continueLatch;

    private Runnable onContinueCallback;

    /**
     * Constructor for DialogZoneView.
     * Loads the FXML layout and initializes the component.
     * @throws IOException if the FXML file cannot be loaded
     */
    public DialogZoneComponent() {
        URL url = getClass().getResource(FXML_PATH);
        FXMLLoader loader = new FXMLLoader(url);

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DialogZone.fxml", e);
        }
    }

    /**
     * set the dialog text.
     * @param text (String) the text to display in the dialog
     */
    public void setDialogText(String text) {
        this.dialogText.setText(text);
    }

    /**
     * Clear the dialog text.
     */
    public void clearDialog() {
        this.dialogText.setText("");
    }

    /**
     * set the additional info text.
     * @param text (String) the text to display as additional information
     */
    public void setAdditionalInfo(String text) {
        this.additionalInfo.setText(text);
    }

    /**
     * set the continue button text. (next or end)
     * @param text
     */
    public void setButtonText(String text) {
        this.continueButton.setText(text);
    }

    public void setContinueLatch(CountDownLatch latch) {
        this.continueLatch = latch;
    }

    public void setOnContinueCallback(Runnable callback) {
        this.onContinueCallback = callback;
    }


    @FXML
    public void onContinue() {
        if (this.continueLatch != null) {
            this.continueLatch.countDown();
        }
        if (this.onContinueCallback != null) {
            this.onContinueCallback.run();
        }
    }

}
