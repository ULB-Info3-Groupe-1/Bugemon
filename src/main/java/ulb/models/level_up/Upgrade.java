package ulb.models.level_up;

/** Immutable stat-bonus option offered to the player during a {@link LevelUp}. */
public record Upgrade(int hp, int attack, int defense, int initiative) {
    public String toString() {
        return String.format("+%d HP +%d Attack +%d Defense +%d Initiative", this.hp, this.attack, this.defense,
                this.initiative);
    }
}
