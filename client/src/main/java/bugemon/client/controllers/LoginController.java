package bugemon.client.controllers;

import javafx.application.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.client.net.NetworkManager;
import bugemon.client.views.LoginView;
import bugemon.client.views.ViewLoader;
import bugemon.common.net.ConnectionRequestPacket;
import bugemon.common.net.ConnectionResponsePacket;

/**
 * Login screen controller. Sends {@link ConnectionRequestPacket}s to the authoritative server through the
 * {@link NetworkManager} and reacts to the {@link ConnectionResponsePacket} reply, updating the UI on the JavaFX thread
 * via {@code Platform.runLater}. It has no knowledge of how authentication is performed server-side.
 */
public class LoginController extends Controller<LoginView> implements LoginView.Listener {

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    private final NetworkManager network;

    public LoginController(MetaController metaController, NetworkManager network) {
        super(metaController, ViewLoader.load(LoginView::new));
        this.view.setListener(this);
        this.network = network;
    }

    @Override
    public void onLogin(String playerName, String password) {
        if (playerName.isBlank() || password.isBlank()) {
            this.view.showEmptyFieldsAlert();
            return;
        }
        this.authenticate(new ConnectionRequestPacket(playerName, password, ConnectionRequestPacket.Mode.LOGIN));
    }

    @Override
    public void onCreateAccount(String playerName, String password) {
        if (playerName.isBlank() || password.isBlank()) {
            this.view.showEmptyFieldsAlert();
            return;
        }
        this.authenticate(
                new ConnectionRequestPacket(playerName, password, ConnectionRequestPacket.Mode.CREATE_ACCOUNT));
    }

    @Override
    public void onQuit() {
        Platform.exit();
    }

    private void authenticate(ConnectionRequestPacket request) {
        this.network.sendAsync(request, ConnectionResponsePacket.class).whenComplete(
                (response, error) -> Platform.runLater(() -> this.onAuthCompleted(request, response, error)));
    }

    private void onAuthCompleted(ConnectionRequestPacket request, ConnectionResponsePacket response, Throwable error) {
        if (error != null) {
            LOG.warn("Authentication request failed", error);
            this.view.showServerErrorAlert();
            return;
        }
        if (response.success()) {
            if (request.mode() == ConnectionRequestPacket.Mode.LOGIN) {
                this.metaController.onLogged(response.playerName());
            } else {
                this.metaController.onAccountCreated(response.playerName());
            }
            return;
        }
        this.showFailure(response.status());
    }

    private void showFailure(ConnectionResponsePacket.Status status) {
        switch (status) {
            case INVALID_CREDENTIALS -> this.view.showInvalidCredentialsAlert();
            case IDENTIFIER_NOT_FOUND -> this.view.showIdentifierNotFoundAlert();
            case USERNAME_ALREADY_EXISTS -> this.view.showPlayernameAlreadyExistsAlert();
            default -> this.view.showServerErrorAlert();
        }
    }
}
