package ulb.models.player;

import java.util.Optional;

import ulb.models.item.Inventory;
import ulb.models.skills.SkillTreeState;
import ulb.models.team.Team;

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

    public void clear() {
        this.activeTeam = null;
        this.inventory.clear();
        this.skillTreeState.clear();
    }
}
