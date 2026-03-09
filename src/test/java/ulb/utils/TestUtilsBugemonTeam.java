package ulb.utils;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;

public class TestUtilsBugemonTeam {

    private TestUtilsBugemonTeam() {}

    public static BugemonTeam createDefaultBugemonTeam(boolean isDefeated) {
        Bugemon expectedBugemon1 = TestUtilsBugemons.createDefaultBugemon("1"),
            expectedBugemon2 = TestUtilsBugemons.createDefaultBugemon("2"),
            expectedBugemon3 = TestUtilsBugemons.createDefaultBugemon("3"),
            expectedBugemon4 = TestUtilsBugemons.createDefaultBugemon("4"),
            expectedBugemon5 = TestUtilsBugemons.createDefaultBugemon("5"),
            expectedBugemon6 = TestUtilsBugemons.createDefaultBugemon("6");
        BugemonTeam bugemonTeam = new BugemonTeam();
        bugemonTeam.addBugemon(expectedBugemon1);
        bugemonTeam.addBugemon(expectedBugemon2);
        bugemonTeam.addBugemon(expectedBugemon3);
        bugemonTeam.addBugemon(expectedBugemon4);
        bugemonTeam.addBugemon(expectedBugemon5);
        bugemonTeam.addBugemon(expectedBugemon6);
        if (isDefeated) {
            for (Integer i = 1; i <= 6; i++) {
                Bugemon bugemon = bugemonTeam.getBugemon(i.toString()).get();
                bugemon.takeDamage(bugemon.getHp());
            }
        }
        return bugemonTeam;
    }
}
