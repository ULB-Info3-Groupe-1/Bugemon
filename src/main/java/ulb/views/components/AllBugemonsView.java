package ulb.views.components;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;

import ulb.models.bugemon.Bugemon;

/** Reusable custom component displaying all the bugemons inside of a scrollable grid. */
public class AllBugemonsView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/AllBugemons.fxml";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private FlowPane flowPane;

    private Function<Bugemon, Boolean> selectionChecker;
    private Consumer<Bugemon> onBugemonClicked;

    public AllBugemonsView() {
        super(FXML_PATH);
    }

    @FXML
    private void initialize() {
        this.scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            double contentH = this.scrollPane.getContent().getBoundsInLocal().getHeight();
            double viewportH = this.scrollPane.getViewportBounds().getHeight();
            double scrollable = contentH - viewportH;
            if (scrollable > 0) {
                double rowH = this.computeRowHeight();
                if (rowH > 0) {
                    double step = event.getDeltaY() < 0 ? rowH : -rowH;
                    double newVal = this.scrollPane.getVvalue() + step / scrollable;
                    this.scrollPane.setVvalue(Math.max(0.0, Math.min(1.0, newVal)));
                }
            }
            event.consume();
        });
    }

    public void setSelectionChecker(Function<Bugemon, Boolean> checker) {
        this.selectionChecker = checker;
    }

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

    /**
     * Returns the height of one row measured from the actual laid-out card positions. Computes the distance between the
     * first card on row 1 and the first card on row 2; falls back to the single card height when all cards fit on one
     * row.
     */
    private double computeRowHeight() {
        if (this.flowPane.getChildren().isEmpty()) {
            return 0;
        }
        double firstMinY = this.flowPane.getChildren().get(0).getBoundsInParent().getMinY();
        for (Node child : this.flowPane.getChildren()) {
            double y = child.getBoundsInParent().getMinY();
            if (y > firstMinY) {
                return y - firstMinY;
            }
        }
        return this.flowPane.getChildren().get(0).getBoundsInParent().getHeight();
    }

    private BugemonCardView createBugemonCard(Bugemon bugemon) {
        BugemonCardView card = new BugemonCardView(bugemon);

        if (this.selectionChecker != null) {
            card.setSelected(this.selectionChecker.apply(bugemon));
        }

        if (this.onBugemonClicked != null) {
            card.setOnClick(this.onBugemonClicked);
        }

        return card;
    }
}
