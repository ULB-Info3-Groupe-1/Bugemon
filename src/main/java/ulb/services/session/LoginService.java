package ulb.services.session;

import ulb.common.dto.persistence.DefaultInventoryDTO;
import ulb.repositories.PlayerRepository;
import ulb.repositories.exceptions.IdentifierNotFoundException;
import ulb.repositories.exceptions.PlayernameAlreadyExistsException;

public class LoginService {

    private final PlayerRepository playerRepository;
    private final DefaultInventoryDTO defaultInventory;

    public LoginService(PlayerRepository playerRepository, DefaultInventoryDTO defaultInventory) {
        this.playerRepository = playerRepository;
        this.defaultInventory = defaultInventory;
    }

    public boolean login(String playerName, String password) throws IdentifierNotFoundException {
        return this.playerRepository.verifyPlayerPassword(playerName, password);
    }

    public void createAccount(String playerName, String password) throws PlayernameAlreadyExistsException {
        this.playerRepository.createPlayer(playerName, password, this.defaultInventory);
    }
}
