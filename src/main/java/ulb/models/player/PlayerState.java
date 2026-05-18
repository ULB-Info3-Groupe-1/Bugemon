package ulb.models.player;

import java.util.List;
import java.util.Optional;

import ulb.models.item.Inventory;
import ulb.models.skills.Skill;
import ulb.models.team.Team;

public class PlayerState {

    private final String playerName;

    private Team activeTeam;
    private final Inventory inventory;
    private final List<Skill> skill;

    public PlayerState(String playerName, Team activeTeam, Inventory inventory, List<Skill> skill) {
        this.playerName = playerName;
        this.activeTeam = activeTeam;
        this.inventory = inventory;
        this.skill = skill;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public Optional<Team> getActiveTeam() {
        return Optional.ofNullable(this.activeTeam);
    }

    public void setActiveTeam(Team team) {
        this.activeTeam = team;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public List<Skill> getSkills() {
        return this.skill;
    }

    public void setSkills(List<Skill> skill) {
        this.skill.clear();
        this.skill.addAll(skill);
    }

    public void addSkill(Skill skill) {
        this.skill.add(skill);
    }

    public void removeSkill(Skill skill) {
        this.skill.remove(skill);
    }
}
