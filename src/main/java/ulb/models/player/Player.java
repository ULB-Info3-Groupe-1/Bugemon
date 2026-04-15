package ulb.models.player;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.player.exceptions.NoActiveTeamException;

public class Player {

    private final int id;
    private final List<BugemonTeam> teams;
    private final Inventory inventory;
    private Optional<BugemonTeam> activeTeam;

    public Player(int id, List<BugemonTeam> teams, Inventory inventory) {
        this.id = id;
        this.teams = teams;
        this.inventory = inventory;
        this.activeTeam = Optional.empty();
    }

    // --- Getters ---

    public int getId() {
        return this.id;
    }

    public Optional<BugemonTeam> getActiveTeam() {
        return this.activeTeam;
    }

    public List<BugemonTeam> getTeams() {
        return this.teams;
    }

    public Inventory getInventory() {
        return this.inventory;
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

    public void addActiveTeamToCache() throws NoActiveTeamException {
        this.teams.add(this.activeTeam.map(BugemonTeam::new).orElseThrow(this.noActiveTeamException()));
    }

    /**
     * Updates the cache with the active team by deleting then adding the active team to the cache.
     *
     * @throws NoActiveTeamException
     *             if the player does not have an active team (optionnal.isEmpty())
     */
    public void updateCacheWithActiveTeam() throws NoActiveTeamException {
        this.deleteActiveTeamFromCache();
        this.teams.add(new BugemonTeam(this.activeTeam.orElseThrow(this.noActiveTeamException())));
    }

    // --- Give information ---

    public boolean isActiveTeamEmpty() {
        return this.activeTeam.map(BugemonTeam::isEmpty).orElse(true);
    }

    // --- Actions ---

    public void clearActiveTeam() {
        this.activeTeam = Optional.empty();
    }

    public void restoreHp() throws NoActiveTeamException {
        this.activeTeam.orElseThrow(this.noActiveTeamException()).restoreHp();
    }

    public void addOrRemoveBugemonOfActiveTeam(Bugemon bugemon) throws NoActiveTeamException {
        this.activeTeam.orElseThrow(this.noActiveTeamException()).addOrRemoveBugemon(bugemon);
    }

    public void createNewTeam() {
        this.activeTeam = Optional.of(new BugemonTeam());
    }

    public void renameTeamInCache(String oldName, String newName) {
        this.teams.stream().filter(t -> t.getName().equals(oldName)).forEach(t -> t.setName(newName));
    }

    public void deleteActiveTeamFromCache() throws NoActiveTeamException {
        this.teams.removeIf(t -> t.equals(this.activeTeam.orElseThrow(this.noActiveTeamException())));
    }

    /**
     * Checks if the active team of the player has been saved (check if it is in the cache). If the active team is
     * empty, it is considered as saved because there is nothing to save (we cannot save an empty team).
     *
     * @return (boolean) true if the active team has been saved, false otherwise
     */
    public boolean isActiveTeamSaved() {
        return this.activeTeam.map(this.teams::contains).orElse(true);
    }

    // --- Utils ---

    public Supplier<NoActiveTeamException> noActiveTeamException() {
        return () -> new NoActiveTeamException("No active team");
    }

}
