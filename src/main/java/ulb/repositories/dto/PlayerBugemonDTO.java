package ulb.repositories.dto;

public record PlayerBugemonDTO(String playername, String bugemonName, int currentDefense, int currentAttackPower,
        int currentInitiative, int currentMaxHp, int currentXp, int currentLevel) {
}
