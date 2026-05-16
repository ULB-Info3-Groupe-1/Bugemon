package ulb.models.combat.snapshot;

import java.util.List;

public record TeamSnapshot(List<CombatBugemonSnapshot> bugemons, CombatBugemonSnapshot active) {
    public int size() {
        return this.bugemons.size();
    }
}
