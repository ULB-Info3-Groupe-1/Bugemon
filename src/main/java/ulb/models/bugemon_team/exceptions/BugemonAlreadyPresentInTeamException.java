package ulb.models.bugemon_team.exceptions;

/** Thrown when trying to add a Bugemon already present in the team. */
public class BugemonAlreadyPresentInTeamException extends RuntimeException {
    /**
     * Constructs a new BugemonAlreadyPresentInTeamException.
     *
     * @param message
     *            The error message
     */
    public BugemonAlreadyPresentInTeamException(String message) {
        super(message);
    }
}
