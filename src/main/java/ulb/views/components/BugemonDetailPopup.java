package ulb.views.components;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;

/** Utility class that shows a read-only floating detail popup for a {@link Bugemon} on right-click. */
public final class BugemonDetailPopup {

    private BugemonDetailPopup() {}

    /**
     * Displays a styled popup with the Bugemon's stats and attacks near the cursor.
     * Closes automatically when it loses focus.
     *
     * @param bugemon
     *            the Bugemon whose details are displayed
     * @param event
     *            the right-click event used to position the popup
     */
    public static void show(Bugemon bugemon, ContextMenuEvent event) {
        javafx.scene.Node source = (javafx.scene.Node) event.getSource();
        Window owner = source.getScene().getWindow();

        Stage popup = new Stage(StageStyle.TRANSPARENT);
        popup.initOwner(owner);

        VBox content = buildContent(bugemon);
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

    private static VBox buildContent(Bugemon bugemon) {
        VBox root = new VBox(8);
        root.getStyleClass().add("bugemon-popup");

        root.getChildren().addAll(
                buildHeader(bugemon),
                buildStats(bugemon),
                buildAttacks(bugemon)
        );

        return root;
    }

    private static HBox buildHeader(Bugemon bugemon) {
        ImageView sprite = new ImageView(new Image(bugemon.getSpriteURL(), 72, 72, true, false));

        Label name = new Label(bugemon.getName());
        name.getStyleClass().addAll("bugemon-name", bugemon.getType().toString());

        Label type = new Label(bugemon.getType().toString());
        type.getStyleClass().add(bugemon.getType().toString());

        Label level = new Label("Nv. " + bugemon.getLevel());
        level.getStyleClass().add("bugemon-popup-key");

        VBox info = new VBox(4, name, type, level);
        info.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        HBox header = new HBox(12, sprite, info);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return header;
    }

    private static VBox buildStats(Bugemon bugemon) {
        Label title = new Label("Stats");
        title.getStyleClass().add("bugemon-popup-section");

        return new VBox(4,
                title,
                statRow("PV",         bugemon.getHp() + " / " + bugemon.getMaxHp()),
                statRow("Attaque",    String.valueOf(bugemon.getAttack())),
                statRow("Défense",    String.valueOf(bugemon.getDefense())),
                statRow("Initiative", String.valueOf(bugemon.getInitiative()))
        );
    }

    private static VBox buildAttacks(Bugemon bugemon) {
        Label title = new Label("Attaques");
        title.getStyleClass().add("bugemon-popup-section");

        VBox section = new VBox(4, title);
        for (Attack attack : bugemon.getAttackList()) {
            Label row = new Label("• " + attack.name()
                    + "  (" + attack.type() + ")"
                    + "  [" + attack.power() + "]");
            row.getStyleClass().add("bugemon-popup-attack");
            section.getChildren().add(row);
        }
        return section;
    }

    private static HBox statRow(String key, String value) {
        Label keyLabel = new Label(key);
        keyLabel.getStyleClass().add("bugemon-popup-key");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("bugemon-popup-value");

        return new HBox(8, keyLabel, valueLabel);
    }
}
