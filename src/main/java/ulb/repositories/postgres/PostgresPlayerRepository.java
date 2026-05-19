package ulb.repositories.postgres;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.repositories.InventoryRepository;
import ulb.repositories.PlayerRepository;
import ulb.repositories.exceptions.PlayernameAlreadyExistsException;
import ulb.repositories.DatabaseConnection;

public class PostgresPlayerRepository extends AbstractRepository implements PlayerRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresPlayerRepository.class);

    private final InventoryRepository inventoryRepository;

    public PostgresPlayerRepository(DatabaseConnection dbConnection, Map<String, String> queries,
            InventoryRepository inventoryRepository) {
        super(dbConnection, queries);
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void createPlayer(String playername) throws PlayernameAlreadyExistsException {
        LOG.debug("Creating player with name: {}", playername);
        try {
            this.executeUpdate("CreatePlayer", playername);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("duplicate key")
                    || (e.getCause() != null && e.getCause().getMessage().contains("duplicate key"))) {
                LOG.warn("Player creation failed due to duplicate playername: {}", playername);
                throw new PlayernameAlreadyExistsException("Player name already exists: " + playername);
            }
            throw e;
        }
        this.inventoryRepository.addDefaultInventory(playername);
    }

    @Override
    public boolean playerExists(String playername) {
        LOG.debug("Checking if player exists: {}", playername);
        return !this.executeQuery("GetPlayerByPlayername", rs -> rs.getInt(DatabaseColumns.COL_ID), playername)
                .isEmpty();
    }

    @Override
    public int getPlayerCurrentFloor(String playername) {
        LOG.debug("Getting current floor for playername: {}", playername);
        return this.executeQuery("GetPlayerCurrentTowerFloor",
                rs -> rs.getInt(DatabaseColumns.COL_CURRENT_TOWER_FLOOR), playername).get(0);
    }

    @Override
    public void setPlayerCurrentFloor(String playername, int floorNumber) {
        LOG.debug("Setting current floor {} for playername: {}", floorNumber, playername);
        this.executeUpdate("SetPlayerCurrentTowerFloor", floorNumber, playername);
    }

    @Override
    public void resetPlayerCurrentFloor(String playername) {
        LOG.debug("Resetting current floor for playername: {}", playername);
        this.executeUpdate("ResetPlayerCurrentTowerFloor", playername);
    }
}
