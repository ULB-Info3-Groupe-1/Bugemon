package ulb.views.combat;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import ulb.common.dto.BugemonDTO;

/**
 * Reusable custom JavaFX component that displays the key information of a
 * single {@link BugemonDTO} in the combat HUD.
 *
 * <p>
 * The component is backed by the {@code BugemonInfo.fxml} layout and exposes
 * three visual elements:
 * <ul>
 *   <li>the Bugemon's name, styled according to its elemental type;</li>
 *   <li>the Bugemon's type label in parentheses;</li>
 *   <li>an HP progress bar scaled to {@code currentHp / maxHp}.</li>
 * </ul>
 * </p>
 *
 * <p>
 * The component loads and owns its FXML at construction time, so it can be
 * embedded directly in any JavaFX scene graph or in other FXML files via
 * {@code <fx:include>}.
 * </p>
 *
 * @see BugemonDTO
 * @see ulb.views.combat.CombatView
 */
public class BugemonInfoView extends VBox {

    private static final String FXML_PATH = "/fxml/BugemonInfo.fxml";

    @FXML private Text bugemonName;
    @FXML private Text bugemonType;
    @FXML private javafx.scene.control.ProgressBar bugemonHPBar;

    /**
     * Constructs a {@code BugemonInfoView} by loading the
     * {@code BugemonInfo.fxml} layout.
     *
     * <p>
     * This component acts as both the FXML root and its own controller, so
     * {@link FXMLLoader#setRoot(Object)} and
     * {@link FXMLLoader#setController(Object)} are both set to {@code this}
     * before loading.
     * </p>
     *
     * @throws RuntimeException if the FXML resource cannot be located or
     *                          loaded.
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
     * Populates the component with the data of the given {@link BugemonDTO}.
     *
     * <p>
     * The following fields are updated:
     * <ul>
     *   <li>{@code bugemonName} — set to {@link BugemonDTO#getName()} and
     *       re-styled with a CSS class matching the Bugemon's type string
     *       (any previously applied type class is cleared first).</li>
     *   <li>{@code bugemonType} — set to the type name wrapped in
     *       parentheses, e.g. {@code "(AQUA)"}.</li>
     *   <li>{@code bugemonHPBar} — progress value set to
     *       {@code currentHp / maxHp}, clamped to {@code [0.0, 1.0]} by the
     *       JavaFX {@link javafx.scene.control.ProgressBar} contract.</li>
     * </ul>
     * </p>
     *
     * @param bugemon the {@link BugemonDTO} whose data should be displayed;
     *                must not be {@code null}, and
     *                {@link BugemonDTO#getMaxHp()} must be greater than zero
     *                to avoid a division by zero in the HP bar calculation.
     */
    public void setBugemonInfo(BugemonDTO bugemon) {
        this.bugemonName.getStyleClass().clear();
        this.bugemonName.getStyleClass().add(bugemon.getType().toString());
        this.bugemonName.setText(bugemon.getName());
        this.bugemonType.setText("(" + bugemon.getType().toString() + ")");
        this.bugemonHPBar.setProgress((double) bugemon.getHp() / bugemon.getMaxHp());
    }
}
