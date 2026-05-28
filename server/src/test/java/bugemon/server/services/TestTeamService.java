package bugemon.server.services;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import bugemon.common.dto.persistence.TeamDTO;
import bugemon.common.models.team.Team;
import bugemon.server.repositories.BugemonRepository;
import bugemon.server.repositories.TeamRepository;
import bugemon.server.services.exceptions.TeamNameEmptyException;
import bugemon.server.services.exceptions.TeamNotFoundException;

public class TestTeamService {

    private static final String PLAYER = "testPlayer";

    private TeamRepository teamRepo;
    private BugemonRepository bugemonRepo;
    private TeamService service;

    @Before
    public void setUp() {
        this.teamRepo = mock(TeamRepository.class);
        this.bugemonRepo = mock(BugemonRepository.class);
        this.service = new TeamService(this.teamRepo, this.bugemonRepo, PLAYER);
    }

    private Team namedTeam(String name) {
        Team team = new Team();
        team.setName(name);
        return team;
    }

    @Test
    public void shouldThrowTeamNotFoundException_whenDeletingNullTeamName() {
        assertThrows(TeamNotFoundException.class, () -> this.service.deleteTeam(null));
    }

    @Test
    public void shouldThrowTeamNotFoundException_whenDeletingEmptyTeamName() {
        assertThrows(TeamNotFoundException.class, () -> this.service.deleteTeam(""));
    }

    @Test
    public void shouldThrowTeamNotFoundException_whenDeletingNonExistentTeam() {
        when(this.teamRepo.findByName(eq(PLAYER), anyString())).thenReturn(Optional.empty());

        assertThrows(TeamNotFoundException.class, () -> this.service.deleteTeam("Ghost Team"));
    }

    @Test
    public void shouldThrowTeamNotFoundException_whenRenamingNonExistentTeam() {
        when(this.teamRepo.findByName(eq(PLAYER), anyString())).thenReturn(Optional.empty());

        Team team = this.namedTeam("Ghost Team");

        assertThrows(TeamNotFoundException.class, () -> this.service.renameTeam(team, "New Name"));
    }

    @Test
    public void shouldThrowTeamNameEmptyException_whenNewNameIsNull() {
        Team team = this.namedTeam("My Team");

        assertThrows(TeamNameEmptyException.class, () -> this.service.renameTeam(team, null));
    }

    @Test
    public void shouldThrowTeamNameEmptyException_whenNewNameIsEmpty() {
        Team team = this.namedTeam("My Team");

        assertThrows(TeamNameEmptyException.class, () -> this.service.renameTeam(team, ""));
    }

    @Test
    public void shouldRenameTeam_whenTeamExistsAndNewNameIsValid()
            throws TeamNameEmptyException, TeamNotFoundException {
        String oldName = "Old Team";
        String newName = "New Team";

        TeamDTO existingDTO = new TeamDTO(PLAYER, oldName, List.of());
        when(this.teamRepo.findByName(PLAYER, oldName)).thenReturn(Optional.of(existingDTO));

        Team team = this.namedTeam(oldName);

        this.service.renameTeam(team, newName);

        verify(this.teamRepo).delete(PLAYER, oldName);
        verify(this.teamRepo).save(PLAYER, new TeamDTO(PLAYER, newName, List.of()));
    }

    @Test
    public void shouldDeleteTeam_whenTeamExists() throws TeamNotFoundException {
        String teamName = "Existing Team";
        TeamDTO dto = new TeamDTO(PLAYER, teamName, List.of());
        when(this.teamRepo.findByName(PLAYER, teamName)).thenReturn(Optional.of(dto));

        this.service.deleteTeam(teamName);

        verify(this.teamRepo).delete(PLAYER, teamName);
    }

    @Test
    public void shouldReturnFalse_whenTeamDoesNotExist() {
        when(this.teamRepo.findByName(eq(PLAYER), anyString())).thenReturn(Optional.empty());

        org.junit.Assert.assertFalse(this.service.teamExists("NoTeam"));
    }

    @Test
    public void shouldReturnTrue_whenTeamExists() {
        String teamName = "Present Team";
        TeamDTO dto = new TeamDTO(PLAYER, teamName, List.of());
        when(this.teamRepo.findByName(PLAYER, teamName)).thenReturn(Optional.of(dto));

        org.junit.Assert.assertTrue(this.service.teamExists(teamName));
    }
}
