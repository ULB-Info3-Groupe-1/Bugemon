package bugemon.server.services;

import java.util.Optional;

import bugemon.common.dto.display.FloorDisplayDTO;
import bugemon.common.dto.persistence.TowerDTO;
import bugemon.common.models.team.Team;
import bugemon.common.models.tower.FloorMap;
import bugemon.common.models.tower.TowerEngine;
import bugemon.common.models.tower.TowerState;
import bugemon.server.repositories.TowerRepository;

/**
 * Service that manages the lifecycle of a tower run.
 *
 * <p>
 * All pure run logic (creation, floor generation, display-DTO building, restore, and DTO conversion) is delegated to
 * {@link TowerEngine}, which is shared with the client. This service adds the persistence layer
 * ({@link TowerRepository}) and tracks the server-side active run.
 */
public class TowerService {

    private final String playerName;
    private final TowerRepository towerRepository;
    private TowerState activeTower = null;

    /**
     * Constructs a {@code TowerService} bound to the given player.
     *
     * @param towerRepository
     *            repository for persisting tower run state
     * @param playerName
     *            the name of the player whose tower data this service manages
     */
    public TowerService(TowerRepository towerRepository, String playerName) {
        this.playerName = playerName;
        this.towerRepository = towerRepository;
    }

    /**
     * Creates a fresh tower run for {@code activeTeam} and stores it as the active tower.
     *
     * @param activeTeam
     *            the player's selected team for this run
     * @return the newly created {@link TowerState}
     */
    public TowerState createTower(Team activeTeam) {
        this.activeTower = TowerEngine.createTower(activeTeam);
        return this.activeTower;
    }

    public void setActiveTower(TowerState towerState) {
        this.activeTower = towerState;
    }

    public Optional<TowerState> getActiveTower() {
        return Optional.ofNullable(this.activeTower);
    }

    /**
     * Generates the floor map for the given seed and floor.
     *
     * @param seed
     *            the deterministic seed
     * @param floor
     *            the floor number
     * @return the generated {@link FloorMap}
     */
    public FloorMap generateFloor(int seed, int floor) {
        return TowerEngine.generateFloor(seed, floor);
    }

    public void save() {
        if (this.activeTower != null) {
            this.towerRepository.save(this.playerName, TowerEngine.toDTO(this.playerName, this.activeTower));
        }
    }

    public void delete() {
        this.towerRepository.delete(this.playerName);
        this.activeTower = null;
    }

    /**
     * Builds the floor display DTO for the active run.
     *
     * @return a display DTO for the current floor
     * @throws IllegalStateException
     *             if no tower run is active
     */
    public FloorDisplayDTO buildFloorDisplayDTO() {
        if (this.activeTower == null) {
            throw new IllegalStateException("No active tower run");
        }
        return TowerEngine.buildFloorDisplayDTO(this.activeTower);
    }

    public Optional<String> getSavedTeamName() {
        return this.towerRepository.find(this.playerName).map(dto -> dto.team().teamName());
    }

    /**
     * Loads a previously saved tower run and reconstructs it using {@code team}.
     *
     * @param team
     *            the team to use for the restored run (should match the saved team name)
     * @return the restored {@link TowerState}, or empty if no save exists
     */
    public Optional<TowerState> loadSaved(Team team) {
        return this.towerRepository.find(this.playerName).map(dto -> {
            this.activeTower = TowerEngine.restore(dto, team);
            return this.activeTower;
        });
    }

    /**
     * Returns the player's persisted tower snapshot, if any. Used by the network layer to ship the run to the client,
     * which owns the live run during play.
     *
     * @return the saved {@link TowerDTO}, or empty if none
     */
    public Optional<TowerDTO> findSavedTower() {
        return this.towerRepository.find(this.playerName);
    }

    /**
     * Persists the given tower snapshot for the player (client-supplied, since the client owns the live run).
     *
     * @param dto
     *            the run snapshot to persist
     */
    public void saveTower(TowerDTO dto) {
        this.towerRepository.save(this.playerName, dto);
    }
}
