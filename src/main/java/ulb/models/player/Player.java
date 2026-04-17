package ulb.models.player;

import java.util.Optional;
import java.util.function.Supplier;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.player.exceptions.NoActiveTeamException;

public class Player {

    private final int id;
    private Optional<BugemonTeam> activeTeam;

    public Player(int id) {
        this.id = id;
        this.activeTeam = Optional.empty();
    }

    // --- Getters ---

    public int getId() {
        return this.id;
    }

    public Optional<BugemonTeam> getActiveTeam() {
        return this.activeTeam;
    }

    public String getActiveTeamName() throws NoActiveTeamException {
        return this.activeTeam.orElseThrow(this.noActiveTeamException()).getName();
    }

    // --- Setters ---

    public void setActiveTeamName(String teamName) throws NoActiveTeamException {
        this.activeTeam.orElseThrow(this.noActiveTeamException()).setName(teamName);
    }

    public void setActiveTeam(BugemonTeam activeTeam) {
        this.activeTeam = Optional.of(new BugemonTeam(activeTeam));
    }

    // --- Give information ---

    public boolean isActiveTeamEmpty() {
        return this.activeTeam.map(BugemonTeam::isEmpty).orElse(true);
    }

    // --- Actions ---

    public void clearActiveTeam() {
        this.activeTeam = Optional.empty();
    }

    public void addOrRemoveBugemonOfActiveTeam(Bugemon bugemon) throws NoActiveTeamException {
        this.activeTeam.orElseThrow(this.noActiveTeamException()).addOrRemoveBugemon(bugemon);
    }

    public void createNewTeam() {
        this.activeTeam = Optional.of(new BugemonTeam());
    }

    public void updateBugemonOfActiveTeam(Bugemon bugemon) throws NoActiveTeamException {
        this.activeTeam.orElseThrow(this.noActiveTeamException()).updateBugemon(bugemon);
    }

    // --- Utils ---

    public Supplier<NoActiveTeamException> noActiveTeamException() {
        return () -> new NoActiveTeamException("No active team");
    }

}
