package ulb.utils;

import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.bugemon.Bugemon;

public class TestUtilsBugemonTeam {

    private TestUtilsBugemonTeam() {
    }

    public static BugemonTeam createDefaultBugemonTeam() {
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
        return bugemonTeam;
    }

}
