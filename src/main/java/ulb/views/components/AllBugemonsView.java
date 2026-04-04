package ulb.views.components;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

import ulb.models.bugemon.Bugemon;

/** Reusable custom component displaying all the bugemons inside of a scrollable grid. */
public class AllBugemonsView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/AllBugemons.fxml";
    private static final double CELL_WIDTH = 112;

    @FXML
    private FlowPane flowPane;

    private Function<Bugemon, Boolean> selectionChecker;
    private Consumer<Bugemon> onBugemonClicked;

    public AllBugemonsView() {
        super(FXML_PATH);
    }

    /** Sets the predicate used to determine whether a given Bugemon should appear selected. */
    public void setSelectionChecker(Function<Bugemon, Boolean> checker) {
        this.selectionChecker = checker;
    }

    /** Sets the callback invoked when the user clicks a Bugemon card. */
    public void setOnClick(Consumer<Bugemon> callback) {
        this.onBugemonClicked = callback;
    }

    /** Clears and repopulates the grid with the given list of Bugemons. */
    public void showAll(List<Bugemon> bugemonList) {
        this.flowPane.getChildren().clear();

        if (bugemonList.isEmpty()) {
            return;
        }

        for (Bugemon bugemon : bugemonList) {
            this.flowPane.getChildren().add(this.createBugemonCard(bugemon));
        }
    }

    private BugemonCardView createBugemonCard(Bugemon bugemon) {
        BugemonCardView card = new BugemonCardView(bugemon);
        card.setPrefWidth(CELL_WIDTH);

        if (this.selectionChecker != null) {
            card.setSelected(this.selectionChecker.apply(bugemon));
        }

        if (this.onBugemonClicked != null) {
            card.setOnClick(this.onBugemonClicked);
        }

        return card;
    }
}
