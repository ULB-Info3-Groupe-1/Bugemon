package ulb.repository.dto;

public record PlayerBugemonDTO(int playerId, String bugemonName, int currentDefense, int currentAttackPower,
        int currentInitiative, int currentMaxHp, int currentXp, int currentLevel) {
}
