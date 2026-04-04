package ulb.services;

import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.repository.DatabaseRepository;
import ulb.repository.dto.CreateBugemonDTO;

public class BugemonService {

    private static final DatabaseRepository DB_REPOSITORY = DatabaseRepository.getInstance();

    // Cache for all default Bugemons to avoid multiple database calls
    private List<Bugemon> allDefaultBugemonsCache;

    /**
     * Get all default Bugemons from the database. Cached after the first call.
     *
     * @return (List<Bugemon>) List of default Bugemons
     */
    public List<Bugemon> getAllDefaultBugemons() {
        if (this.allDefaultBugemonsCache == null) {
            this.allDefaultBugemonsCache = DB_REPOSITORY.getAllDefaultBugemons();
        }
        return this.allDefaultBugemonsCache;
    }

    public void saveBugemon(CreateBugemonDTO bugemon) {
        DB_REPOSITORY.saveBugemon(bugemon);
    }

}
