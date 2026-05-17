package ulb.models.level_up;

public record Upgrade(int hp, int attack, int defense, int initiative) {
    @Override
    public String toString() {
        return String.format("+%d HP +%d Attack +%d Defense +%d Initiative", this.hp, this.attack, this.defense,
                this.initiative);
    }
}
