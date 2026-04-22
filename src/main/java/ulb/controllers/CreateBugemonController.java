package ulb.controllers;

import java.net.URL;
import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.BugemonType;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.repositories.exceptions.BugemonNameIsEmptyException;
import ulb.services.BugemonService;
import ulb.services.exceptions.BugemonNameAlreadyExistsException;
import ulb.views.CreateBugemonView;
import ulb.views.ViewLoader;

public class CreateBugemonController extends Controller<CreateBugemonView> implements CreateBugemonView.Listener {

    private final BugemonService bugemonService;

    public CreateBugemonController(MetaController metaController, BugemonService bugemonService) {
        super(metaController, ViewLoader.load(CreateBugemonView::new));
        this.bugemonService = bugemonService;
        this.view.setListener(this);
    }

    @Override
    public void onTypeSelected(BugemonType selectedType) {
        List<Attack> attacks = this.bugemonService.getAttacksByType(selectedType);
        this.view.setAvailableAttacks(attacks);
    }

    @Override
    public void onAdd(String bugemonName, double healthValue, double attackValue, double defenseValue,
            double initiativeValue) {
        BugemonType selectedType = this.view.getSelectedType();
        URL spriteUrl = this.view.getSelectedSpriteUrl();

        if (selectedType == null) {
            this.view.showInvalidFormChooseBugemonType();
            return;
        }
        if (spriteUrl == null) {
            this.view.showInvalidFormChooseSprite();
            return;
        }

        int hp = (int) Math.round(healthValue);
        int attack = (int) Math.round(attackValue);
        int defense = (int) Math.round(defenseValue);
        int initiative = (int) Math.round(initiativeValue);
        Attack attack1 = this.view.getSelectedAttack1();
        Attack attack2 = this.view.getSelectedAttack2();
        Attack attack3 = this.view.getSelectedAttack3();

        if (attack1 == null || attack2 == null || attack3 == null) {
            this.view.showInvalidFormChooseAttacks();
            return;
        }

        CreateBugemonDTO bugemonToCreate = new CreateBugemonDTO(bugemonName, selectedType, spriteUrl, defense, attack,
                initiative, hp, false, attack1, attack2, attack3);

        try {
            this.bugemonService.saveNewBugemon(bugemonToCreate);
        } catch (BugemonNameIsEmptyException e) {
            this.view.showBugemonNameEmptyAlert();
        } catch (BugemonNameAlreadyExistsException e) {
            this.view.showBugemonNameAlreadyUsedAlert();
        }
        this.view.showSaveSuccessAlert(bugemonName);
    }

    @Override
    public void onReturnToMainMenu() {
        this.metaController.onReturnToMainMenu();
    }
}
