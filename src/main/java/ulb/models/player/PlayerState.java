package ulb.models.player;

import java.util.Optional;

import ulb.models.item.Inventory;
import ulb.models.skills.SkillTreeState;
import ulb.models.team.Team;

/**
 * Aggregates all persistent state associated with a single player profile.
 *
 * <p>
 * Holds the player's name, currently selected {@link ulb.models.team.Team}, {@link ulb.models.item.Inventory}, and
 * {@link ulb.models.skills.SkillTreeState}. A call to {@link #clear()} resets everything except the name, allowing a
 * fresh run to begin without recreating the object.
 */
public class PlayerState {

    private final String playerName;

    private Team activeTeam;
    private final Inventory inventory;
    private final SkillTreeState skillTreeState;

    public PlayerState(String playerName, Team activeTeam, Inventory inventory, SkillTreeState skillTreeState) {
        this.playerName = playerName;
        this.activeTeam = activeTeam;
        this.inventory = inventory;
        this.skillTreeState = skillTreeState;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * Returns the currently selected team, or {@link java.util.Optional#empty()} if none has been chosen yet.
     *
     * @return an {@link java.util.Optional} containing the active team, or empty
     */
    public Optional<Team> getActiveTeam() {
        return Optional.ofNullable(this.activeTeam);
    }

    public Optional<String> getActiveTeamName() {
        return this.getActiveTeam().map(Team::getName);
    }

    public void setActiveTeam(Team team) {
        this.activeTeam = team;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public SkillTreeState getSkillTreeState() {
        return this.skillTreeState;
    }

    public void addSkillPoint() {
        this.skillTreeState.addPoint();
    }

    /**
     * Resets the player's run-specific state: clears the active team, empties the inventory, and resets the skill tree
     * state. The player name is preserved.
     */
    public void clear() {
        this.activeTeam = null;
        this.inventory.clear();
        this.skillTreeState.clear();
    }
}
