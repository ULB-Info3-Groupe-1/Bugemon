package ulb.services;

import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repository.DatabaseRepository;

public class PlayerService {
    // Player's active team
    private BugemonTeam activeTeam;
    // List of all user-created teams
    private List<BugemonTeam> userTeams;

    private Inventory inventory;

    // Repository for database interactions
    private DatabaseRepository databaseRepository;

    // Cache for all default Bugemons to avoid multiple database calls
    private List<Bugemon> allDefaultBugemonsCache;

    /**
     * Contructor for PlayerService.
     */
    public PlayerService() {
        this.databaseRepository = new DatabaseRepository();
    }

    /**
     * Returns the currently active team of Bugemons.
     * @return the active BugemonTeam
     */
    public BugemonTeam getActiveTeam() {
        return activeTeam;
    }

    /**
     * Sets the active team to the given BugemonTeam.
     */
    public void setActiveTeam(BugemonTeam team) {
        this.activeTeam = team;
    }

    /**
     * Clears the active team by removing all Bugemons from it. This method is useful for resetting
     * the player's team between sessions or when starting a new game.
     */
    public void clearActiveTeam() {
        if (this.activeTeam != null) {
            this.activeTeam.clear();
        }
    }

    /**
     * Returns a list of all default Bugemons available in the game. This method caches the result
     * after the first call to minimize database access.
     * @return a list of all default Bugemons
     */
    public List<Bugemon> getAllDefaultBugemons() {
        if (allDefaultBugemonsCache == null) {
            allDefaultBugemonsCache = databaseRepository.getAllDefaultBugemons();
        }
        return allDefaultBugemonsCache;
    }
}
