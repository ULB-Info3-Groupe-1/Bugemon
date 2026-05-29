package bugemon.client.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.client.services.RemoteBugemonService;
import bugemon.client.views.CreateBugemonView;
import bugemon.client.views.ViewLoader;
import bugemon.common.Configuration;
import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.bugemon.ElementType;
import bugemon.common.net.CreateBugemonResponsePacket;

/**
 * Controller for the custom Bugemon creation screen.
 *
 * <p>
 * The static attack catalogue is fetched once and cached for local type-filtering. On submit, the chosen sprite is read
 * into a byte array and the whole request is sent asynchronously through {@link RemoteBugemonService}; the server's
 * status reply is mapped to the appropriate alert on the JavaFX thread.
 */
public class CreateBugemonController extends Controller<CreateBugemonView> implements CreateBugemonView.Listener {

    private static final Logger LOG = LoggerFactory.getLogger(CreateBugemonController.class);

    private final RemoteBugemonService bugemonService;

    private Optional<ElementType> selectedBugemonType = Optional.empty();
    private Optional<File> selectedBugemonSprite = Optional.empty();
    private List<Attack> allAttacks;

    public CreateBugemonController(MetaController metaController, RemoteBugemonService bugemonService) {
        super(metaController, ViewLoader.load(CreateBugemonView::new));
        this.bugemonService = bugemonService;
        this.view.setListener(this);
        this.loadAttacks();
    }

    /** Fetches the static attack catalogue once and refreshes the available-attack list on the JavaFX thread. */
    private void loadAttacks() {
        this.bugemonService.getAttacks().whenComplete((attacks, error) -> Platform.runLater(() -> {
            if (error != null) {
                LOG.error("Failed to load attacks", error);
                return;
            }
            this.allAttacks = attacks;
            this.updateAvailableAttacks();
        }));
    }

    @Override
    public void onTypeSelected(ElementType selectedType) {
        this.selectedBugemonType = Optional.of(selectedType);
        this.updateAvailableAttacks();
    }

    @Override
    public void onSpriteSelected(File selectedSpriteFile) {
        this.selectedBugemonSprite = Optional.of(selectedSpriteFile);
        this.view.setSprite(selectedSpriteFile);
    }

    /**
     * Refreshes the view's attack selection list to show only cached attacks matching the selected element type. Does
     * nothing until both a type is selected and the attack catalogue has loaded.
     */
    public void updateAvailableAttacks() {
        if (this.allAttacks == null) {
            return;
        }
        this.selectedBugemonType.ifPresent(bugemonType -> {
            List<Attack> attacks = this.allAttacks.stream().filter(attack -> attack.type() == bugemonType).toList();
            this.view.setAvailableAttacks(attacks);
        });
    }

    @Override
    public void onAdd(String bugemonName, double healthValue, double attackValue, double defenseValue,
            double initiativeValue, List<Attack> attacks) {
        int hp = (int) Math.round(healthValue);
        int attack = (int) Math.round(attackValue);
        int defense = (int) Math.round(defenseValue);
        int initiative = (int) Math.round(initiativeValue);

        if (attacks.size() != Configuration.Game.NUM_ATTACKS_PER_BUGEMON) {
            this.view.showInvalidFormChooseAttacks();
            return;
        }
        if (this.selectedBugemonSprite.isEmpty()) {
            this.view.showInvalidFormChooseSprite();
            return;
        }
        if (this.selectedBugemonType.isEmpty()) {
            this.view.showInvalidFormChooseBugemonType();
            return;
        }

        byte[] spriteBytes = this.readSpriteBytes();
        if (spriteBytes == null) {
            this.view.showInvalidFormChooseSprite();
            return;
        }
        this.submit(bugemonName, attack, defense, initiative, hp, attacks, spriteBytes);
    }

    private byte[] readSpriteBytes() {
        try {
            return Files.readAllBytes(this.selectedBugemonSprite.get().toPath());
        } catch (IOException e) {
            LOG.error("Failed to read sprite file", e);
            return null;
        }
    }

    private void submit(String name, int attack, int defense, int initiative, int hp, List<Attack> attacks,
            byte[] spriteBytes) {
        this.bugemonService.createBugemon(name, this.selectedBugemonType.get(), attack, defense, initiative, hp,
                attacks, spriteBytes).whenComplete((status, error) -> Platform.runLater(() -> {
                    if (error != null) {
                        LOG.error("Failed to create Bugemon {}", name, error);
                        return;
                    }
                    this.handleCreateStatus(status, name);
                }));
    }

    private void handleCreateStatus(CreateBugemonResponsePacket.Status status, String bugemonName) {
        switch (status) {
            case OK -> this.view.showSaveSuccessAlert(bugemonName);
            case NAME_EMPTY -> this.view.showBugemonNameEmptyAlert();
            case NAME_EXISTS -> this.view.showBugemonNameAlreadyUsedAlert();
            default -> LOG.warn("Unexpected create status: {}", status);
        }
    }

    @Override
    public void onReturnToMainMenu() {
        this.metaController.onMainMenu();
    }
}
