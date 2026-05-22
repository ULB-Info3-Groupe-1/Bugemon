package ulb.repositories.postgres;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.common.dto.persistence.DefaultInventoryDTO;
import ulb.common.dto.persistence.InventoryDTO;
import ulb.repositories.DatabaseConnection;
import ulb.repositories.InventoryRepository;
import ulb.repositories.PlayerRepository;
import ulb.repositories.exceptions.PlayernameAlreadyExistsException;

public class PostgresPlayerRepository extends AbstractRepository implements PlayerRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresPlayerRepository.class);

    private final InventoryRepository inventoryRepository;

    public PostgresPlayerRepository(DatabaseConnection dbConnection, Map<String, String> queries,
            InventoryRepository inventoryRepository) {
        super(dbConnection, queries);
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void createPlayer(String playerName, DefaultInventoryDTO defaulInventory)
            throws PlayernameAlreadyExistsException {
        LOG.debug("Creating player with name: {}", playerName);
        try {
            this.executeUpdate("CreatePlayer", playerName);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("duplicate key")
                    || (e.getCause() != null && e.getCause().getMessage().contains("duplicate key"))) {
                LOG.warn("Player creation failed due to duplicate playerName: {} (expected on startup)", playerName);
                throw new PlayernameAlreadyExistsException("Player name already exists: " + playerName);
            }
            throw e;
        }
        InventoryDTO playerInventory = new InventoryDTO(playerName, defaulInventory.items());
        this.inventoryRepository.save(playerInventory);
    }

    @Override
    public boolean playerExists(String playerName) {
        LOG.debug("Checking if player exists: {}", playerName);
        return !this.executeQuery("GetPlayerByPlayername", rs -> rs.getInt(DatabaseColumns.COL_ID), playerName)
                .isEmpty();
    }

    @Override
    public int getPlayerCurrentFloor(String playerName) {
        LOG.debug("Getting current floor for playerName: {}", playerName);
        return this.executeQuery("GetPlayerCurrentTowerFloor", rs -> rs.getInt(DatabaseColumns.COL_CURRENT_TOWER_FLOOR),
                playerName).get(0);
    }

    @Override
    public void setPlayerCurrentFloor(String playerName, int floorNumber) {
        LOG.debug("Setting current floor {} for playerName: {}", floorNumber, playerName);
        this.executeUpdate("SetPlayerCurrentTowerFloor", floorNumber, playerName);
    }

    @Override
    public void resetPlayerCurrentFloor(String playerName) {
        LOG.debug("Resetting current floor for playerName: {}", playerName);
        this.executeUpdate("ResetPlayerCurrentTowerFloor", playerName);
    }
}
