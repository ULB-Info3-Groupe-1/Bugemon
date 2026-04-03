package ulb.repository.dto;

public record UserBugemonDTO(int userId, int bugemonId, int currentDefense, int currentAttackPower,
        int currentInitiative, int currentMaxHp, int currentXp, int currentLevel) {
}
