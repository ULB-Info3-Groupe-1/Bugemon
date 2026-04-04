package ulb.views.components;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;

import ulb.models.bugemon.Bugemon;

/** Reusable custom component displaying all the bugemons inside of a scrollable grid. */
public class AllBugemonsView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/AllBugemons.fxml";
    private static final double CELL_WIDTH = 112;
    /** Matches tokens.css {@code size-bugemon-card}. */
    private static final double CARD_HEIGHT = 96;
    /** Matches tokens.css {@code size-bugemon-grid-vgap} and app.css {@code .all-bugemons -fx-vgap}. */
    private static final double GRID_VGAP = 10;
    private static final double ROW_HEIGHT = CARD_HEIGHT + GRID_VGAP;

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
                double step = event.getDeltaY() < 0 ? ROW_HEIGHT : -ROW_HEIGHT;
                double newVal = this.scrollPane.getVvalue() + step / scrollable;
                this.scrollPane.setVvalue(Math.max(0.0, Math.min(1.0, newVal)));
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
