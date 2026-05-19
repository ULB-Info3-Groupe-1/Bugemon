package ulb.services;

import ulb.repositories.SkillRepository;
import ulb.repositories.StaticRepository;

public class SkillService {

    private final String playername;

    private final SkillRepository skillRepository;
    private final StaticRepository staticRepository;

    public SkillService(SkillRepository skillRepository, StaticRepository staticRepository, String playername) {
        this.skillRepository = skillRepository;
        this.staticRepository = staticRepository;

        this.playername = playername;
    }

}
