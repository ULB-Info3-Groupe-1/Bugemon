package ulb.controllers;

import ulb.repositories.exceptions.IdentifierNotFoundException;
import ulb.repositories.exceptions.PlayernameAlreadyExistsException;
import ulb.services.LoginService;
import ulb.views.LoginView;
import ulb.views.ViewLoader;

public class LoginController extends Controller<LoginView> implements LoginView.Listener {

    private final LoginService loginService;

    public LoginController(MetaController metaController, LoginService loginService) {
        super(metaController, ViewLoader.load(LoginView::new));
        this.view.setListener(this);
        this.loginService = loginService;
    }

    @Override
    public void onLogin(String playerName, String password) {
        if (playerName.isBlank() || password.isBlank()) {
            this.view.showEmptyFieldsAlert();
            return;
        }

        try {
            if (this.loginService.login(playerName, password)) {
                this.metaController.onLogged(playerName);
            } else {
                this.view.showInvalidCredentialsAlert();
            }
        } catch (IdentifierNotFoundException e) {
            this.view.showIdentifierNotFoundAlert();
        }
    }

    @Override
    public void onCreateAccount(String playerName, String password) {
        if (playerName.isBlank() || password.isBlank()) {
            this.view.showEmptyFieldsAlert();
            return;
        }

        try {
            this.loginService.createAccount(playerName, password);
            this.metaController.onAccountCreated(playerName);
        } catch (PlayernameAlreadyExistsException e) {
            this.view.showPlayernameAlreadyExistsAlert();
        }
    }

    @Override
    public void onQuit() {
        javafx.application.Platform.exit();
    }
}
