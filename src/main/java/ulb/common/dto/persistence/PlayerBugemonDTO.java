package ulb.common.dto.persistence;

public record PlayerBugemonDTO(String playername, String bugemonName, int bonusDefense, int bonusAttackPower,
        int bonusInitiative, int bonusMaxHp, int xp, int level) {
}
