package ulb.services;

import java.util.List;

import ulb.factory.BugemonFactory;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException;
import ulb.repository.DatabaseRepository;
import ulb.repository.dto.CreateBugemonDTO;
import ulb.services.exceptions.BugemonNameIsEmptyException;

public class BugemonService {

    private final DatabaseRepository dbRepository;

    // Cache for all default Bugemons to avoid multiple database calls
    private List<Bugemon> allDefaultBugemonsCache;

    public BugemonService() {
        this.dbRepository = DatabaseRepository.getInstance();
    }

    /**
     * Get all default Bugemons from the database. Cached after the first call.
     *
     * @return (List<Bugemon>) List of default Bugemons
     */
    public List<Bugemon> getAllDefaultBugemons() {
        if (this.allDefaultBugemonsCache == null) {
            this.allDefaultBugemonsCache = this.dbRepository.getAllDefaultBugemons();
        }
        return this.allDefaultBugemonsCache;
    }

    /**
     * Save a new bugemon in the database.
     *
     * @param bugemon
     *            (CreateBugemonDTO) the bugemon to be saved
     * @throws BugemonNameIsEmptyException
     *             if the name of the bugemon is empty
     */
    public void saveBugemon(CreateBugemonDTO bugemon)
            throws BugemonNameIsEmptyException, BugemonAlreadyExistsException {
        if (bugemon.name().isBlank()) {
            throw new BugemonNameIsEmptyException("The name cannot be blank");
        }
        if (this.getAllDefaultBugemons().stream().anyMatch(b -> b.getName().equals(bugemon.name()))) {
            throw new BugemonAlreadyExistsException("There cannot be multiple bugemons with the same name");
        }
        this.dbRepository.saveBugemon(bugemon);
        this.allDefaultBugemonsCache.add(BugemonFactory.create(bugemon));
    }

}
