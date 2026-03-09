package ulb.models.level_up;

public class Choice {
    int bonusHP, bonusAttack, bonusDefense, bonusInitiative;

    public Choice(int bonusHP, int bonusAttack, int bonusDefense, int bonusInitiative){
        this.bonusHP = bonusHP;
        this.bonusAttack = bonusAttack;
        this.bonusDefense = bonusDefense;
        this.bonusInitiative = bonusInitiative;
        
    }

    public int getBonusHP() {
        return bonusHP;
    }

    public int getBonusAttack() {
        return bonusAttack;
    }

    public int getBonusDefense() {
        return bonusDefense;
    }

    public int getBonusInitiative() {
        return bonusInitiative;
    }
}