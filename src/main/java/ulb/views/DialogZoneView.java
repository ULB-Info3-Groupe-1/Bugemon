package ulb.views;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Reusable custom component displaying a dialog zone.
 */
public class DialogZoneView extends VBox {
    private static final String FXML_PATH = "/fxml/DialogZone.fxml";

    @FXML private Text dialogText;

    @FXML private Button nextButton;

    @FXML private Text additionalInfo;

    /**
     * Constructor for DialogZoneView.
     * Loads the FXML layout and initializes the component.
     * Throws a {@link RuntimeException} if the FXML file cannot be loaded.
     */
    public DialogZoneView() {
        URL url = getClass().getResource(FXML_PATH);
        FXMLLoader loader = new FXMLLoader(url);

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load DialogZone.fxml", e);
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
        this.nextButton.setText(text);
    }
}
