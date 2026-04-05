package ulb.repository.dto;

public record UserBugemonDTO(int userId, String bugemonName, int currentDefense, int currentAttackPower,
        int currentInitiative, int currentMaxHp, int currentXp, int currentLevel) {
}
