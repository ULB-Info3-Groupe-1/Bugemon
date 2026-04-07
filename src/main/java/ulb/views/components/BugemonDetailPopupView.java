package ulb.views.components;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;

/**
 * Read-only component displaying a {@link Bugemon}'s stats and attacks. Call {@link #show(Bugemon, ContextMenuEvent)}
 * to open it as a floating popup centred over the owner window.
 */
public class BugemonDetailPopupView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/BugemonDetailPopup.fxml";

    @FXML
    private ImageView sprite;
    @FXML
    private Label nameLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label hpValue;
    @FXML
    private Label attackValue;
    @FXML
    private Label defenseValue;
    @FXML
    private Label initiativeValue;
    @FXML
    private VBox attacksContainer;

    private BugemonDetailPopupView(Bugemon bugemon) {
        super(FXML_PATH);
        File spriteFile = new File("assets/sprites/" + bugemon.getSpriteURL());
        this.sprite.setImage(new Image(spriteFile.toURI().toString(), 72, 72, true, false));
        this.nameLabel.setText(bugemon.getName());
        this.nameLabel.getStyleClass().addAll("bugemon-name", bugemon.getType().toString());
        this.typeLabel.setText(bugemon.getType().toString());
        this.typeLabel.getStyleClass().add(bugemon.getType().toString());
        this.levelLabel.setText("Nv. " + bugemon.getLevel());
        this.hpValue.setText(bugemon.getHp() + " / " + bugemon.getMaxHp());
        this.attackValue.setText(String.valueOf(bugemon.getAttack()));
        this.defenseValue.setText(String.valueOf(bugemon.getDefense()));
        this.initiativeValue.setText(String.valueOf(bugemon.getInitiative()));
        for (Attack attack : bugemon.getAttackList()) {
            Label row = new Label("• " + attack.name() + "  (" + attack.type() + ")  [" + attack.power() + "]");
            row.getStyleClass().add("bugemon-popup-attack");
            this.attacksContainer.getChildren().add(row);
        }
    }

    /** Builds and shows a transparent popup with the Bugemon's details, closing it when it loses focus. */
    public static void show(Bugemon bugemon, ContextMenuEvent event) {
        Node source = (Node) event.getSource();
        Window owner = source.getScene().getWindow();

        Stage popup = new Stage(StageStyle.TRANSPARENT);
        popup.initOwner(owner);

        BugemonDetailPopupView content = new BugemonDetailPopupView(bugemon);
        Scene scene = new Scene(content);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().addAll(source.getScene().getStylesheets());

        popup.setScene(scene);
        popup.show();
        popup.setX(owner.getX() + (owner.getWidth() - popup.getWidth()) / 2);
        popup.setY(owner.getY() + (owner.getHeight() - popup.getHeight()) / 2);

        popup.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                popup.close();
            }
        });
    }
}
