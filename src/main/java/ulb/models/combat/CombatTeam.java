package ulb.models.combat;

import java.util.List;

import ulb.models.combat.snapshot.CombatBugemonSnapshot;
import ulb.models.combat.snapshot.TeamSnapshot;
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

    public boolean isDefeated() {
        return this.members.stream().allMatch(CombatBugemon::isKo);
    }

    public List<CombatBugemon> getAlive() {
        return this.members.stream().filter(b -> !b.isKo()).toList();
    }

    public List<CombatBugemon> getAvailable() {
        return this.getAlive().stream().filter(b -> b != this.active).toList();
    }

    public boolean hasAvailable() {
        return this.getAvailable().size() > 0;
    }

    public void syncToRunTeam() {
        this.members.forEach(CombatBugemon::syncToRunBugemon);
    }

    public TeamSnapshot getSnapshot() {
        List<CombatBugemonSnapshot> bugemonSnapshots = this.members.stream()
                .map(b -> new CombatBugemonSnapshot(b.getCurrentHp(), b.getAttacks(), b.getMaxHp(),
                        b.getEffectiveAttack(), b.getEffectiveDefense(), b.getEffectiveInitiative()))
                .toList();
        CombatBugemonSnapshot activeSnapshot = new CombatBugemonSnapshot(this.active.getCurrentHp(),
                this.active.getAttacks(), this.active.getMaxHp(), this.active.getEffectiveAttack(),
                this.active.getEffectiveDefense(), this.active.getEffectiveInitiative());
        return new TeamSnapshot(bugemonSnapshots, activeSnapshot);
    }
}
