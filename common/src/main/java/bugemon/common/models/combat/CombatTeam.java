package bugemon.common.models.combat;

import java.util.List;

import bugemon.common.models.combat.snapshot.CombatBugemonSnapshot;
import bugemon.common.models.combat.snapshot.TeamSnapshot;
import bugemon.common.models.run.RunTeam;

/**
 * Manages the combat-side roster of {@link CombatBugemon}s for one participant.
 *
 * <p>
 * Every team has exactly one <em>active</em> Bugemon — the one currently fighting. When that Bugemon is KO'd, the
 * controller triggers a forced switch via {@link Combat#requestForcedSwitch}. The team is considered defeated when all
 * its members are KO ({@link #isDefeated()}).
 */
public class CombatTeam {
    private final List<CombatBugemon> members;
    private CombatBugemon active;

    /**
     * Constructs a {@code CombatTeam} from the given list of Bugemon, automatically setting the first non-KO member as
     * the active fighter.
     *
     * @param members
     *            the list of combat Bugemon; must be non-empty and contain at least one alive member
     * @throws IllegalArgumentException
     *             if {@code members} is empty or all members are KO
     */
    public CombatTeam(List<CombatBugemon> members) {
        if (members.isEmpty()) {
            throw new IllegalArgumentException("A CombatTeam must contain at least one bugemon.");
        }
        this.members = List.copyOf(members);
        CombatBugemon firstAlive = members.stream().filter(b -> !b.isKo()).findFirst().orElseThrow(
                () -> new IllegalArgumentException("A CombatTeam must contain at least one alive bugemon."));
        this.setActive(firstAlive);
    }

    /**
     * Creates a {@code CombatTeam} by wrapping each member of a {@link RunTeam} in a {@link CombatBugemon}.
     *
     * @param runTeam
     *            the run-level team to convert
     * @return a new {@code CombatTeam} mirroring the run team's composition
     */
    public static CombatTeam fromRunTeam(RunTeam runTeam) {
        List<CombatBugemon> combatMembers = runTeam.getMembers().stream().map(CombatBugemon::new).toList();
        return new CombatTeam(combatMembers);
    }

    /**
     * Returns the currently active (fighting) Bugemon.
     *
     * @return the active {@link CombatBugemon}
     */
    public CombatBugemon getActive() {
        return this.active;
    }

    /**
     * Switches the active fighter to {@code target} and marks it as having participated.
     *
     * @param target
     *            the Bugemon to send into battle; must be a team member and must not be KO
     * @throws IllegalArgumentException
     *             if {@code target} is not in the team or is KO
     */
    public void setActive(CombatBugemon target) {
        if (!this.members.contains(target) || target.isKo()) {
            throw new IllegalArgumentException("Invalid target for switch.");
        }
        this.active = target;
        this.active.markAsParticipated();
    }

    /**
     * Returns the subset of team members that have been sent into battle at least once. Used to determine XP
     * eligibility at the end of combat.
     *
     * @return an unmodifiable view of participating Bugemon
     */
    public List<CombatBugemon> getParticipants() {
        return this.members.stream().filter(CombatBugemon::hasParticipated).toList();
    }

    /**
     * Returns the total number of Bugemon on this team, including fainted members.
     *
     * @return total team size
     */
    public int size() {
        return this.members.size();
    }

    /**
     * Returns {@code true} if every Bugemon on this team has been KO'd.
     *
     * @return {@code true} when the team has no surviving members
     */
    public boolean isDefeated() {
        return this.members.stream().allMatch(CombatBugemon::isKo);
    }

    /**
     * Returns all Bugemon on this team that have not yet been KO'd.
     *
     * @return list of alive {@link CombatBugemon}s; may be empty if the team is defeated
     */
    public List<CombatBugemon> getAlive() {
        return this.members.stream().filter(b -> !b.isKo()).toList();
    }

    /**
     * Returns the alive team members that are not currently active, i.e. the Bugemon the player or AI may switch to.
     *
     * @return a list of switchable Bugemon; empty if no switch is possible
     */
    public List<CombatBugemon> getAvailable() {
        return this.getAlive().stream().filter(b -> b != this.active).toList();
    }

    /**
     * Returns {@code true} if at least one Bugemon other than the active one is still alive and can be switched in.
     *
     * @return {@code true} when a voluntary switch is possible
     */
    public boolean hasAvailable() {
        return !this.getAvailable().isEmpty();
    }

    /**
     * Persists all Bugemon HP values back to their underlying {@link bugemon.common.models.run.RunBugemon} instances, making the
     * combat outcome durable for the current run.
     */
    public void syncToRunTeam() {
        this.members.forEach(CombatBugemon::syncToRunBugemon);
    }

    /**
     * Returns an immutable snapshot of the current team state, capturing HP values and effective stats for use by AI
     * strategies or the UI.
     *
     * @return a {@link bugemon.common.models.combat.snapshot.TeamSnapshot} reflecting the current state
     */
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
