package ulb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.skills.Skill;
import ulb.models.skills.SkillBuilder;
import ulb.models.skills.SkillEffect.RegenPostCombatEffect;
import ulb.repositories.BugemonRepository;
import ulb.repositories.PlayerRepository;
import ulb.repositories.TeamRepository;
import ulb.repositories.exceptions.TeamEmptyException;
import ulb.repositories.exceptions.TeamNotFoundException;
import ulb.services.exceptions.NoActiveTeamException;
import ulb.utils.test.TestUtilsBugemons;

@RunWith(MockitoJUnitRunner.class)
public class TestTeamService {

    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private BugemonRepository bugemonRepository;

    private TeamService teamService;
    private static final String PLAYER_NAME = "Player";

    @Before
    public void setUp() {
        when(this.teamRepository.loadTeams(PLAYER_NAME)).thenReturn(new ArrayList<>());
        this.teamService = new TeamService(this.playerRepository, this.teamRepository, this.bugemonRepository,
                PLAYER_NAME, mock(SkillService.class));
    }

    @Test
    public void shouldSetAndGetActiveTeam_whenTeamExists() throws Exception {
        String teamName = "DreamTeam";
        BugemonTeam team = new BugemonTeam(teamName);

        when(this.teamRepository.loadTeams(PLAYER_NAME)).thenReturn(List.of(team));
        this.teamService = new TeamService(this.playerRepository, this.teamRepository, this.bugemonRepository,
                PLAYER_NAME, mock(SkillService.class));
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam(teamName);

        assertNotNull(this.teamService.getRequiredActiveTeam());
        assertEquals(teamName, this.teamService.getRequiredActiveTeam().getName());
    }

    @Test(expected = TeamNotFoundException.class)
    public void shouldThrowException_whenSettingNonExistentTeam() throws Exception {
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam("Unknown");
    }

    @Test
    public void shouldSaveTeam_whenActiveTeamIsValid() throws Exception {
        Bugemon bugemon = TestUtilsBugemons.createDefaultBugemon("Pikachu");
        this.teamService.addOrRemoveBugemon(bugemon);
        String teamName = "NewTeam";
        this.teamService.saveTeam(teamName);
        this.teamService.setActiveTeam(teamName);

        verify(this.teamRepository).createTeam(PLAYER_NAME, teamName);
        assertEquals(teamName, this.teamService.getRequiredActiveTeam().getName());
        assertTrue(this.teamService.getTeamNames().contains(teamName));
    }

    @Test
    public void shouldDeleteActiveTeam_whenRequested() throws Exception {
        BugemonTeam team = new BugemonTeam();
        team.setName("ToDelete");
        when(this.teamRepository.loadTeams(PLAYER_NAME)).thenReturn(new ArrayList<>(List.of(team)));
        this.teamService = new TeamService(this.playerRepository, this.teamRepository, this.bugemonRepository,
                PLAYER_NAME, mock(SkillService.class));
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam("ToDelete");

        this.teamService.deleteTeam("ToDelete");

        verify(this.teamRepository).deleteTeam(PLAYER_NAME, "ToDelete");
        assertThrows(NoActiveTeamException.class, () -> this.teamService.getRequiredActiveTeam());
        assertFalse(this.teamService.getTeamNames().contains("ToDelete"));
    }

    @Test
    public void shouldReturnTrue_whenActiveTeamIsSaved() throws Exception {
        Bugemon bugemon = TestUtilsBugemons.createDefaultBugemon("Pikachu");

        BugemonTeam team = new BugemonTeam();
        team.setName("TeamA");
        team.addOrRemoveBugemon(bugemon);

        when(this.teamRepository.loadTeams(PLAYER_NAME)).thenReturn(List.of(team));
        this.teamService = new TeamService(this.playerRepository, this.teamRepository, this.bugemonRepository,
                PLAYER_NAME, mock(SkillService.class));
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam("TeamA");

        assertTrue("The team should be considered saved.", this.teamService.isWorkingTeamSaved());
    }

    @Test
    public void shouldCorrectlyRenameTeam() throws Exception {
        String oldName = "Old";
        BugemonTeam team = new BugemonTeam();
        team.setName(oldName);

        when(this.teamRepository.loadTeams(PLAYER_NAME)).thenReturn(new ArrayList<>(List.of(team)));
        this.teamService = new TeamService(this.playerRepository, this.teamRepository, this.bugemonRepository,
                PLAYER_NAME, mock(SkillService.class));
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam(oldName);

        String newName = "New";
        this.teamService.renameActiveTeam(newName);

        verify(this.teamRepository).renameTeam(PLAYER_NAME, oldName, newName);
        assertEquals(newName, this.teamService.getRequiredActiveTeam().getName());
        assertTrue(this.teamService.getTeamNames().contains(newName));
        assertFalse(this.teamService.getTeamNames().contains(oldName));
    }

    @Test
    public void shouldReturnFalse_whenActiveTeamHasUnsavedChanges() throws Exception {
        Bugemon b1 = TestUtilsBugemons.createDefaultBugemon("Pikachu");
        BugemonTeam teamInDb = new BugemonTeam("TeamA");
        teamInDb.add(b1);
        when(this.teamRepository.loadTeams(PLAYER_NAME)).thenReturn(List.of(teamInDb));

        this.teamService = new TeamService(this.playerRepository, this.teamRepository, this.bugemonRepository,
                PLAYER_NAME, mock(SkillService.class));
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam("TeamA");

        Bugemon b2 = TestUtilsBugemons.createDefaultBugemon("Bulbasaur");
        this.teamService.addOrRemoveBugemon(b2);

        assertFalse("The team should not be considered saved after modification.",
                this.teamService.isWorkingTeamSaved());
    }

    @Test
    public void shouldUpdateEveryBugemonInDb_whenSavingState() throws Exception {
        Bugemon b1 = TestUtilsBugemons.createDefaultBugemon("P1");
        Bugemon b2 = TestUtilsBugemons.createDefaultBugemon("P2");
        this.teamService.addOrRemoveBugemon(b1);
        this.teamService.addOrRemoveBugemon(b2);
        this.teamService.saveTeam("Team");

        this.teamService.setActiveTeam("Team");

        this.teamService.saveBugemonStateOfActiveTeam();

        verify(this.bugemonRepository, times(2)).updatePlayerBugemon(org.mockito.ArgumentMatchers.any());
    }

    @Test(expected = TeamEmptyException.class)
    public void shouldThrowException_whenSavingEmptyWorkingTeam() throws Exception {
        this.teamService.saveTeam("EmptyTeam");
    }

    @Test(expected = NoActiveTeamException.class)
    public void shouldThrowException_whenRestoringHpWithoutActiveTeam() throws Exception {
        this.teamService.regenHpActiveTeamPostCombat();
    }

    @Test
    public void regenHpActiveTeamPostCombat_shouldHealEachBugemonByPercentOfMaxHp() throws Exception {
        SkillService skillService = mock(SkillService.class);
        Skill regenSkill = new SkillBuilder().effect(new RegenPostCombatEffect(0.30)).build();
        when(skillService.getSkills(RegenPostCombatEffect.class)).thenReturn(List.of(regenSkill));

        Bugemon damaged = TestUtilsBugemons.createDefaultBugemon("Damaged"); // hp=100, maxHp=100
        damaged.takeDamage(50);
        BugemonTeam team = new BugemonTeam("Squad");
        team.addOrRemoveBugemon(damaged);
        when(this.teamRepository.loadTeams(PLAYER_NAME)).thenReturn(new ArrayList<>(List.of(team)));

        this.teamService = new TeamService(this.playerRepository, this.teamRepository, this.bugemonRepository,
                PLAYER_NAME, skillService);
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam("Squad");

        this.teamService.regenHpActiveTeamPostCombat();

        // active team is a copy; assert via the active team to avoid coupling on
        // internal team identity.
        Bugemon healed = this.teamService.getRequiredActiveTeam().getFirst();
        assertEquals(50 + 30, healed.getHp());
    }

    @Test
    public void regenHpActiveTeamPostCombat_shouldDoNothing_whenNoRegenSkillUnlocked() throws Exception {
        SkillService skillService = mock(SkillService.class);
        when(skillService.getSkills(RegenPostCombatEffect.class)).thenReturn(List.of());

        Bugemon damaged = TestUtilsBugemons.createDefaultBugemon("Damaged");
        damaged.takeDamage(40);
        BugemonTeam team = new BugemonTeam("Squad");
        team.addOrRemoveBugemon(damaged);
        when(this.teamRepository.loadTeams(PLAYER_NAME)).thenReturn(new ArrayList<>(List.of(team)));

        this.teamService = new TeamService(this.playerRepository, this.teamRepository, this.bugemonRepository,
                PLAYER_NAME, skillService);
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam("Squad");

        this.teamService.regenHpActiveTeamPostCombat();

        Bugemon untouched = this.teamService.getRequiredActiveTeam().getFirst();
        assertEquals(60, untouched.getHp());
    }

    @Test
    public void shouldSaveNewBugemonInDb_whenNotAlreadyOwned() throws Exception {
        Bugemon newBugemon = TestUtilsBugemons.createDefaultBugemon("NotOwned");
        this.teamService.addOrRemoveBugemon(newBugemon);

        when(this.bugemonRepository.getPlayerBugemons(PLAYER_NAME)).thenReturn(new ArrayList<>());

        this.teamService.saveTeam("LegendaryTeam");

        verify(this.bugemonRepository).savePlayerBugemon(org.mockito.ArgumentMatchers.any());
        verify(this.teamRepository).addTeamMember(org.mockito.ArgumentMatchers.any());
    }

    @Test
    public void shouldUpdateDbAndActiveTeam_whenModifyingTeam() throws Exception {
        Bugemon b1 = TestUtilsBugemons.createDefaultBugemon("Pikachu");
        this.teamService.addOrRemoveBugemon(b1);
        this.teamService.saveTeam("Original");
        this.teamService.setActiveTeam("Original");

        this.teamService.setWorkingTeamAsActiveTeam();

        Bugemon b2 = TestUtilsBugemons.createDefaultBugemon("Charmander");
        this.teamService.addOrRemoveBugemon(b2);
        this.teamService.modifyActiveTeam();

        verify(this.teamRepository).modifyTeam(org.mockito.ArgumentMatchers.eq(PLAYER_NAME),
                org.mockito.ArgumentMatchers.eq("Original"), org.mockito.ArgumentMatchers.anyList());
        assertEquals(2, this.teamService.getRequiredActiveTeam().size());
    }

    @Test
    public void shouldClearWorkingTeam_afterSaving() throws Exception {
        Bugemon bugemon = TestUtilsBugemons.createDefaultBugemon("Pikachu");
        this.teamService.addOrRemoveBugemon(bugemon);

        this.teamService.saveTeam("SomeTeam");

        assertTrue("The workingTeam should be empty after saving", this.teamService.getWorkingTeam().isEmpty());
    }

    @Test
    public void shouldSyncWorkingTeamWithActiveTeam() throws Exception {
        Bugemon bugemon = TestUtilsBugemons.createDefaultBugemon("Pikachu");
        BugemonTeam team = new BugemonTeam("Active");
        team.addOrRemoveBugemon(bugemon);

        this.teamService.addOrRemoveBugemon(bugemon);
        this.teamService.saveTeam("Active");
        this.teamService.setActiveTeam("Active");

        this.teamService.clearWorkingTeam();

        this.teamService.setWorkingTeamAsActiveTeam();

        assertEquals(team, this.teamService.getWorkingTeam());
    }

    @Test
    public void shouldEraseAllBugemonTeamsAndClearActiveTeam_whenclearTeamsAndActiveTeam() throws Exception {
        BugemonTeam team1 = new BugemonTeam("Team1");
        BugemonTeam team2 = new BugemonTeam("Team2");

        when(this.teamRepository.loadTeams(PLAYER_NAME)).thenReturn(new ArrayList<>(List.of(team1, team2)));
        this.teamService = new TeamService(this.playerRepository, this.teamRepository, this.bugemonRepository,
                PLAYER_NAME, mock(SkillService.class));
        this.teamService.loadTeamsAndActiveTeam();
        this.teamService.setActiveTeam("Team1");

        this.teamService.clearTeamsAndActiveTeam();

        verify(this.teamRepository).clearTeams(PLAYER_NAME);
        assertThrows(NoActiveTeamException.class, () -> this.teamService.getRequiredActiveTeam());
        assertTrue(this.teamService.getTeamNames().isEmpty());
    }

    @Test
    public void shouldLoadTeamsAndActiveTeam_whenLoadTeamsAndActiveTeam() {
        BugemonTeam team1 = new BugemonTeam("Team1");
        BugemonTeam team2 = new BugemonTeam("Team2");

        when(this.teamRepository.loadTeams(PLAYER_NAME)).thenReturn(List.of(team1, team2));
        when(this.teamRepository.loadCurrentTeam(PLAYER_NAME)).thenReturn(Optional.ofNullable(team1));

        this.teamService.loadTeamsAndActiveTeam();

        assertNotNull(this.teamService.getRequiredActiveTeam());
        assertEquals("Team1", this.teamService.getRequiredActiveTeam().getName());
        assertTrue(this.teamService.getTeamNames().contains("Team1"));
        assertTrue(this.teamService.getTeamNames().contains("Team2"));
    }
}
