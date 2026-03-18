package ulb.factory;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.Parser;

public class TestTeamFactory {
    @Test
    public void testRandomTeamNumber() {
        Parser.getInstance().parse();
        BugemonTeam teamOfSix = TeamFactory.createRandomTeam(6);
        assertEquals(6, teamOfSix.size());
    }
}
