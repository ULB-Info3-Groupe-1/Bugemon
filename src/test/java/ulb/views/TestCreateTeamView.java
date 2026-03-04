package ulb.views;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import java.util.List;
import java.util.function.Predicate;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextMatchers;
import ulb.controllers.CreateTeamController;
import ulb.models.bugemon.Bugemon;

public class TestCreateTeamView extends ApplicationTest {

    /**
     * Initialization method for the test class.
     */
    @Override
    public void start(Stage stage) throws Exception {
        Bugemon b = new Bugemon.Builder().id("1").build();

        CreateTeamController controller = new CreateTeamController(
            null,
            List.of(b)
        );
        controller.show(stage);
        // --- Stabilize TestFX in Headless mode ---
        stage.setWidth(1920);
        stage.setHeight(1080);

        stage.show();
        stage.toFront();
        stage.requestFocus();

        // Force le moteur JavaFX à calculer tout de suite les coordonnées (bounds) des éléments
        if (stage.getScene() != null && stage.getScene().getRoot() != null) {
            stage.getScene().getRoot().applyCss();
            stage.getScene().getRoot().layout();
        }
    }

    /**
     * Test display of the validate button.
     */
    @Test
    public void testDisplayValidateButton() {
        // On utilise l'ID FXML (#validateButton) pour trouver le composant
        verifyThat("#validateButton", isVisible());
    }

    /**
     * Test that the validate button has the correct text.
     */
    @Test
    public void testTextOnValidateButton() {
        verifyThat("#validateButton", LabeledMatchers.hasText("Valider"));
    }

    /**
     * Test display of the bugemon list pane.
     */
    @Test
    public void testDisplayBugemonListPane() {
        verifyThat("#allBugemonsGridView", isVisible());
    }

    /**
     * Test display of the team pane.
     */
    @Test
    public void testDisplayTeamPane() {
        verifyThat("#bugemonsTeamView", isVisible());
    }

    /**
     * Test that the image of the Bugemon added to the team is the same as the one in the list.
     */
    @Test
    public void testBugemonAddedtoTeam() {
        ImageView firstBugemonInList = lookup("#allBugemonsGridView")
            .lookup((Predicate<Node>) node -> node instanceof ImageView)
            .nth(0)
            .query();

        clickOn(firstBugemonInList);

        ImageView firstBugemonInTeam = lookup("#bugemonsTeamView")
            .lookup((Predicate<Node>) node -> node instanceof ImageView)
            .nth(0)
            .query();

        Assertions.assertThat(firstBugemonInTeam.getImage().getUrl()).contains(
            "unknown.png"
        );
        assert (firstBugemonInList
                .getImage()
                .getUrl()
                .equals(firstBugemonInTeam.getImage().getUrl()));
    }

    /**
     * Test view main text
     */
    @Test
    public void testMainText() {
        Text mainText = lookup(
            (Predicate<Node>) node ->
                node instanceof Text &&
                ((Text) node).getText().equals("Sélection de ton équipe")
        ).query();
        verifyThat(mainText, TextMatchers.hasText("Sélection de ton équipe"));
    }
}
