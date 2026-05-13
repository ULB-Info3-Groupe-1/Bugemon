package ulb.models.combat;

import java.util.List;

import ulb.models.run.RunTeam;

public class CombatTeam {
    private final List<CombatBugemon> members;
    private CombatBugemon active;

    public CombatTeam(List<CombatBugemon> members) {
        if (members.isEmpty()) {
            throw new IllegalArgumentException("A CombatTeam must contain at least one bugemon.");
        }
        this.members = List.copyOf(members);
        this.setActive(members.getFirst());
    }

    public static CombatTeam fromRunTeam(RunTeam runTeam) {
        List<CombatBugemon> combatMembers = runTeam.getMembers().stream().map(CombatBugemon::new).toList();
        return new CombatTeam(combatMembers);
    }

    public CombatBugemon getActive() {
        return this.active;
    }

    public void setActive(CombatBugemon target) {
        if (!this.members.contains(target) || target.isKo()) {
            throw new IllegalArgumentException("Invalid target for switch.");
        }
        this.active = target;
        this.active.markAsParticipated();
    }

    public List<CombatBugemon> getParticipants() {
        return this.members.stream().filter(CombatBugemon::hasParticipated).toList();
    }

    public int size() {
        return this.members.size();
    }

    public void syncToRunTeam() {
        this.members.forEach(CombatBugemon::syncToRunBugemon);
    }
}
