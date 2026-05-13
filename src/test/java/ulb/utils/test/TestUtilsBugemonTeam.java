package ulb.utils.test;

import ulb.models.bugemon_team.Team;

public class TestUtilsBugemonTeam {
    private TestUtilsBugemonTeam() {
    }

    public static Team createDefaultBugemonTeam(boolean isDefeated) {
        Team bugemonTeam = new Team();
        for (int i = 1; i <= 6; i++) {
            bugemonTeam.add(TestUtilsBugemons.createDefaultBugemon(String.valueOf(i)));
        }
        if (isDefeated) {
            bugemonTeam.killAll();
        }
        return bugemonTeam;
    }
}
