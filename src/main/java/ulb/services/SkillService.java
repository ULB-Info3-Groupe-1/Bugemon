package ulb.services;

import java.util.List;

import ulb.models.skills.Skill;
import ulb.models.skills.SkillEffect;

public class SkillService {
    private final List<Skill> allSkills;

    public SkillService(List<Skill> allSkills) {
        this.allSkills = allSkills;
    }

    public List<Skill> getAllSkills() {
        return this.allSkills;
    }

    public List<Skill> getUnlockedSkills() {
        return this.allSkills.stream().filter(Skill::isUnlocked).toList();
    }

    public <T extends SkillEffect> List<Skill> getSkills(Class<T> effectType) {
        return this.allSkills.stream().filter(Skill::isUnlocked).filter(s -> effectType.isInstance(s.getEffect()))
                .toList();
    }
}
