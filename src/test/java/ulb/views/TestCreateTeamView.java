package ulb.views;

import javafx.stage.Stage;
import ulb.controllers.CreateTeamController;

import ulb.models.bugemon.Bugemon;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextMatchers;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import java.util.List;
import java.util.function.Predicate;

import javafx.scene.Node;
import javafx.scene.image.ImageView;

import org.assertj.core.api.Assertions;

public class TestCreateTeamView extends ApplicationTest {

    /**
     * Initialization method for the test class. 
     */
    @Override
    public void start(Stage stage) throws Exception {
        Bugemon b = new Bugemon("test", "test", null, "bugzilla.png", null, null, false);
        CreateTeamController controller = new CreateTeamController(null, List.of(b));
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
        verifyThat("#listPane", isVisible());
    }
    
    /**
     * Test display of the team pane.
     */
    @Test
    public void testDisplayTeamPane() {
        verifyThat("#teamPane", isVisible());
    }

    /**
     * Test when Bugemon is clicked in the list, it is added to the team and the unknown image is replaced by the correct one.
     */
    @Test
    public void testUnknownImageIsReplaced() {
        ImageView firstBugemonInList = lookup("#listPane")
                .lookup((Predicate<Node>) node -> node instanceof ImageView)
                .nth(0)
                .query();
                
        ImageView firstBugemonInTeam = lookup("#teamPane")
                .lookup((Predicate<Node>) node -> node instanceof ImageView)
                .nth(0)
                .query();
                
        Assertions.assertThat(firstBugemonInTeam.getImage().getUrl())
                .contains("unknown.png");

        clickOn(firstBugemonInList);
        
        

        Assertions.assertThat(firstBugemonInTeam.getImage().getUrl())
                .doesNotContain("unknown.png");
        Assertions.assertThat(firstBugemonInTeam.getImage().getUrl())
                .contains("bugzilla.png");
    }

    /**
     * Test CSS style applied to the image when clicked.
     */
    @Test
    public void testStyleWhenBugemonIsClicked() {
        ImageView firstBugemonInList = lookup("#listPane")
                .lookup((Predicate<Node>) node -> node instanceof ImageView)
                .nth(0)
                .query();

        clickOn(firstBugemonInList);
        
        Assertions.assertThat(firstBugemonInList.getStyle())
                .contains("-fx-effect: dropshadow(three-pass-box, red, 5, 0.9, 0, 0);");
    }
    
    /**
    * Test that the image of the Bugemon added to the team is the same as the one in the list.
    */
    @Test
    public void testBugemonAddedtoTeam() {
        ImageView firstBugemonInList = lookup("#listPane")
                .lookup((Predicate<Node>) node -> node instanceof ImageView)
                .nth(0)
                .query();

        clickOn(firstBugemonInList);
        
        ImageView firstBugemonInTeam = lookup("#teamPane")
                .lookup((Predicate<Node>) node -> node instanceof ImageView)
                .nth(0)
                .query();
        
        Assertions.assertThat(firstBugemonInTeam.getImage().getUrl())
                .contains("bugzilla.png");
        assert (firstBugemonInList.getImage().getUrl().equals(firstBugemonInTeam.getImage().getUrl()));
    }

    /**
     * Test view main text
     */
    @Test
    public void testMainText() {
        verifyThat("#mainText", TextMatchers.hasText("Sélection de ton équipe"));
    }
}