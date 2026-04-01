package ulb.repository.dto;

public record UserBugemonDTO(int userId, String bugemonId, int currentDefense,
        int currentAttackPower, int currentInitiative, int currentMaxHp, int currentXp,
        int currentLevel) {
}
