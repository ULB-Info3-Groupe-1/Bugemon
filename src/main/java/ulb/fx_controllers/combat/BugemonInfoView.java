package ulb.fx_controllers.combat;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import ulb.common.dto.BugemonDTO;

/**
 * Reusable custom component displaying Bugemon info.
 */
public class BugemonInfoView extends VBox {
    private final static String FXML_PATH = "/fxml/BugemonInfo.fxml";

    @FXML private Text bugemonName;
    @FXML private Text bugemonType;
    @FXML private javafx.scene.control.ProgressBar bugemonHPBar;

    /**
     * Constructor for BugemonInfoView.
     * Loads the FXML layout and initializes the component.
     * @throws IOException if the FXML file cannot be loaded
     */
    public BugemonInfoView() {
        URL url = getClass().getResource(FXML_PATH);
        FXMLLoader loader = new FXMLLoader(url);

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load BugemonInfo.fxml", e);
        }
    }

    /**
     * Set the Bugemon info to display in this view.
     * @param bugemon
     */
    public void setBugemonInfo(BugemonDTO bugemon) {
        this.bugemonName.getStyleClass().clear();
        this.bugemonName.getStyleClass().add(bugemon.getType().toString());
        this.bugemonName.setText(bugemon.getName());
        this.bugemonType.setText("(" + bugemon.getType().toString() + ")");
        this.bugemonHPBar.setProgress((double)bugemon.getHp() / bugemon.getMaxHp());
    }
}
