package ulb.factory;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.utils.Parser;

public class TestTeamFactory {
    @Test
    public void testRandomTeamNumber() {
        Parser.getInstance().parse();
        List<Bugemon> teamOfSix =
                TeamFactory.createRandomTeam(Parser.getInstance().getBugemons(), 6);
        assertEquals(6, teamOfSix.size());
    }
}
