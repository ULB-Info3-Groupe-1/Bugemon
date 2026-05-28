package bugemon.server.services;

import bugemon.common.dto.persistence.DefaultInventoryDTO;
import bugemon.server.repositories.PlayerRepository;
import bugemon.server.repositories.exceptions.IdentifierNotFoundException;
import bugemon.server.repositories.exceptions.PlayernameAlreadyExistsException;

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
