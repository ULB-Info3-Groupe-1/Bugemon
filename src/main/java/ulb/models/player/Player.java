package ulb.models.player;

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
