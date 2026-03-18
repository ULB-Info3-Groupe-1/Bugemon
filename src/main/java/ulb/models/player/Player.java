package ulb.models.player;

import java.util.HashSet;
import java.util.Set;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon_team.BugemonTeam;

public class Player {
    // ── Active team ───────────────────────────────────────────────────────
    private BugemonTeam activeTeam;

    // ── Inventory ─────────────────────────────────────────────────────────
    private final Inventory inventory;

    // ── Constructor ───────────────────────────────────────────────────────
    public Player(Inventory inventory) {
        this.activeTeam = new BugemonTeam();
        this.inventory = inventory;
    }

    // ── Active team ───────────────────────────────────────────────────────
    public void setActiveTeam(BugemonTeam team) {
        // TODO: catch exceptiosn from BugemonTeam instead of handling teamsize here

        if (team.isEmpty() || team.size() > 6)
            throw new IllegalArgumentException("L'équipe doit avoir entre 1 et 6 Bugémons.");
        Set<String> seen = new HashSet<>();
        for (Bugemon b : team) {
            if (!seen.add(b.getId()))
                throw new IllegalArgumentException("Doublons non autorisés : " + b.getName());
        }
        this.activeTeam = team;
    }

    public void clearActiveTeam() {
        this.activeTeam.clear();
    }

    public BugemonTeam getActiveTeam() {
        return this.activeTeam;
    }

    // ── Inventory ─────────────────────────────────────────────────────────
    public Inventory getInventory() {
        return this.inventory;
    }
}
