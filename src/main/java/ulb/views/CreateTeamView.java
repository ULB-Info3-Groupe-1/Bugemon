package ulb.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.common.dto.BugemonDTO;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.Parser;

/**
 * View for the team creation screen.
 *
 * <p>
 * Holds a reference to the {@link BugemonTeam} model and reads from it directly
 * in {@link #refresh()}. Dispatches user interactions through callbacks; holds
 * no reference to any concrete controller class.
 * </p>
 */
public class CreateTeamView extends View {
    private static final String FXML_PATH = "/fxml/CreateTeam.fxml";

    @FXML private AllBugemonsGridView allBugemonsGridView;
    @FXML private BugemonTeamView bugemonsTeamView;
    @FXML private Button launchAutomaticCombat;
    @FXML private Button launchManualCombat;

    private BugemonTeam bugemonTeam;
    private Consumer<String> onBugemonClicked;
    private Runnable onStartAutoCombat;
    private Runnable onStartManualCombat;

    public CreateTeamView() throws IOException {
        super(FXML_PATH);

        this.allBugemonsGridView.setOnClickCallback(dto -> {
            if (bugemonTeam != null && !bugemonTeam.contains(dto.getId()) && bugemonTeam.isFull()) {
                showAlert("Équipe pleine",
                          "Votre équipe est déjà pleine. Veuillez en retirer un avant "
                                  + "d'en ajouter un nouveau.");
            } else if (onBugemonClicked != null) {
                onBugemonClicked.accept(dto.getId());
            }
        });

        this.launchAutomaticCombat.setOnAction(e -> launchCombat(onStartAutoCombat));
        this.launchManualCombat.setOnAction(e -> launchCombat(onStartManualCombat));
    }

    /** Gives the view a reference to the team model it should read from. */
    public void setModel(BugemonTeam bugemonTeam) {
        this.bugemonTeam = bugemonTeam;
        this.allBugemonsGridView.setSelectionChecker(b -> bugemonTeam.contains(b.getId()));
    }

    public void setOnBugemonClicked(Consumer<String> callback) {
        this.onBugemonClicked = callback;
    }

    public void setOnStartAutoCombat(Runnable callback) {
        this.onStartAutoCombat = callback;
    }

    public void setOnStartManualCombat(Runnable callback) {
        this.onStartManualCombat = callback;
    }

    private void launchCombat(Runnable onStart) {
        if (bugemonTeam != null && bugemonTeam.isEmpty()) {
            showAlert("Équipe incomplète",
                      "Veuillez sélectionner au moins un Bugemon pour démarrer un combat.");
        } else if (onStart != null) {
            onStart.run();
        }
    }

    @Override
    public void refresh() {
        List<BugemonDTO> allBugemons = new ArrayList<>(Parser.getInstance().getBugemons());
        this.allBugemonsGridView.showAll(allBugemons);
        this.bugemonsTeamView.showTeam(new ArrayList<>(bugemonTeam));
    }
}
