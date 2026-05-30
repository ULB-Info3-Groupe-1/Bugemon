package ulb.views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import ulb.Configuration;

public class LoginView extends View {

    @FXML
    private Label titleLabel;
    @FXML
    private VBox playerNameContainer;
    @FXML
    private TextField playerNameField;
    @FXML
    private VBox passwordContainer;
    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;
    @FXML
    private Button createAccountButton;
    @FXML
    private Button confirmCreateButton;
    @FXML
    private Button backToLoginButton;
    @FXML
    private Button forgotPasswordButton;

    private Listener listener;

    private enum Mode {
        LOGIN,
        REGISTER,
        FORGOT_PASSWORD
    }

    @FXML
    private void initialize() {
        this.setMode(Mode.LOGIN);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void setMode(Mode mode) {
        boolean isLogin = mode == Mode.LOGIN;
        boolean isRegister = mode == Mode.REGISTER;

        this.titleLabel
                .setText(isLogin ? "Connexion" : (isRegister ? "Créer un compte" : "Réinitialiser le mot de passe"));

        this.playerNameContainer.setVisible(isLogin || isRegister);
        this.playerNameContainer.setManaged(isLogin || isRegister);

        this.passwordContainer.setVisible(isLogin || isRegister);
        this.passwordContainer.setManaged(isLogin || isRegister);

        this.loginButton.setVisible(isLogin);
        this.loginButton.setManaged(isLogin);
        this.createAccountButton.setVisible(isLogin);
        this.createAccountButton.setManaged(isLogin);

        this.confirmCreateButton.setVisible(isRegister);
        this.confirmCreateButton.setManaged(isRegister);

        boolean isForgot = mode == Mode.FORGOT_PASSWORD;
        this.backToLoginButton.setVisible(isRegister || isForgot);
        this.backToLoginButton.setManaged(isRegister || isForgot);
    }

    @FXML
    private void onLoginClicked() {
        this.listener.onLogin(this.playerNameField.getText(), this.passwordField.getText());
    }

    @FXML
    private void onQuitClicked() {
        this.listener.onQuit();
    }

    @FXML
    private void onCreateAccountClicked() {
        this.setMode(Mode.REGISTER);
    }

    @FXML
    private void onConfirmCreateClicked() {
        this.listener.onCreateAccount(this.playerNameField.getText(), this.passwordField.getText());
    }

    @FXML
    private void onForgotPasswordClicked() {
        this.setMode(Mode.FORGOT_PASSWORD);
    }

    @FXML
    private void onBackToLoginClicked() {
        this.setMode(Mode.LOGIN);
    }

    @Override
    public void refresh() {
        // Les champs pourraient être vidés ici si nécessaire
    }

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.LOGIN_VIEW;
    }

    public void showEmptyFieldsAlert() {
        this.showWarningAlert("Champs manquants", "Veuillez remplir tous les champs obligatoires.");
    }

    public void showResetPasswordSuccessAlert() {
        this.showInfoAlert("Mot de passe", "Si cette adresse email existe, un lien de réinitialisation a été envoyé.");
    }

    public void showInvalidCredentialsAlert() {
        this.showErrorAlert("Échec de la connexion", "Mot de passe incorrect.");
    }

    public void showIdentifierNotFoundAlert() {
        this.showErrorAlert("Identifiant non trouvé", "Le nom d'utilisateur ou l'adresse email spécifié n'existe pas.");
    }

    public void showPlayernameAlreadyExistsAlert() {
        this.showErrorAlert("Nom d'utilisateur déjà pris",
                "Le nom d'utilisateur que vous avez choisi est déjà utilisé. Veuillez en choisir un autre.");
    }

    public interface Listener {
        void onLogin(String playerName, String password);

        void onCreateAccount(String playerName, String password);

        void onQuit();
    }
}
