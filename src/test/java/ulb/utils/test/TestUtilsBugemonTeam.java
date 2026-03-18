package ulb.utils.test;

import ulb.models.bugemon_team.BugemonTeam;

public class TestUtilsBugemonTeam {
    private TestUtilsBugemonTeam() {}

    public static BugemonTeam createDefaultBugemonTeam(boolean isDefeated) {
        BugemonTeam bugemonTeam = new BugemonTeam();
        for (int i = 1; i <= 6; i++) {
            bugemonTeam.add(TestUtilsBugemons.createDefaultBugemon(String.valueOf(i)));
        }
        if (isDefeated) {
            // TODO: this should obv be moved to a kill method
            bugemonTeam.forEach(bugemon -> bugemon.takeDamage(bugemon.getHp()));
        }
        return bugemonTeam;
    }
}
