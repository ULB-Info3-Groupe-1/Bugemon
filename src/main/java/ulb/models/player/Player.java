package ulb.models.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;

public class Player {
    // ── Active team ───────────────────────────────────────────────────────
    private List<Bugemon> activeTeam;

    // ── Inventory ─────────────────────────────────────────────────────────
    private final Inventory inventory;

    // ── Constructor ───────────────────────────────────────────────────────
    public Player(Inventory inventory) {
        this.activeTeam = new ArrayList<>();
        this.inventory = inventory;
    }

    // ── Active team ───────────────────────────────────────────────────────
    public void setActiveTeam(List<Bugemon> team) {
        if (team.isEmpty() || team.size() > 6)
            throw new IllegalArgumentException("L'équipe doit avoir entre 1 et 6 Bugémons.");
        Set<String> seen = new HashSet<>();
        for (Bugemon b : team) {
            if (!seen.add(b.getId()))
                throw new IllegalArgumentException("Doublons non autorisés : " + b.getName());
        }
        this.activeTeam = team;
    }

    public void resetActiveTeam() {
        this.activeTeam.clear();
    }

    public List<Bugemon> getActiveTeam() {
        return this.activeTeam;
    }

    // ── Inventory ─────────────────────────────────────────────────────────
    public Inventory getInventory() {
        return this.inventory;
    }
}
