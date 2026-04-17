package ulb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.player.Player;
import ulb.models.player.exceptions.NoActiveTeamException;
import ulb.repositories.PlayerRepository;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.TeamMemberDTO;
import ulb.repositories.exceptions.TeamEmptyException;
import ulb.repositories.exceptions.TeamNameAlreadyExistsException;
import ulb.repositories.exceptions.TeamNameEmptyException;
import ulb.repositories.exceptions.TeamNotFoundException;

public class PlayerService {

    private final Player player;
    private final PlayerRepository playerRepository;
    private final List<BugemonTeam> teams;

    public PlayerService(PlayerRepository playerRepository, Player player) {
        this.playerRepository = playerRepository;
        this.player = player;
        this.teams = new ArrayList<>(playerRepository.loadTeams(this.player.getId()));
    }

    // --- Getters ---

    public Optional<BugemonTeam> getActiveTeam() {
        return this.player.getActiveTeam();
    }

    public List<String> getTeamNames() {
        return this.teams.stream().map(BugemonTeam::getName).toList();
    }

    public int getPlayerId() {
        return this.player.getId();
    }

    // --- Team Management ---

    /**
     * Sets the active team for the player.
     *
     * @param teamName
     *            the name of the team to be set
     * @throws TeamNotFoundException
     *             if the team does not exist
     */
    public void setActiveTeam(String teamName) throws TeamNotFoundException {
        BugemonTeam team = this.teams.stream().filter(t -> t.getName().equals(teamName)).findFirst()
                .orElseThrow(() -> new TeamNotFoundException("Team not found: " + teamName));
        this.player.setActiveTeam(new BugemonTeam(team));
    }

    /**
     * Saves the active team to the database.
     *
     * @param teamName
     *            the name of the team to be saved
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     * @throws TeamNameAlreadyExistsException
     *             if the team name is already taken
     * @throws TeamEmptyException
     *             if the active team is empty
     */
    public void saveTeam(String teamName)
            throws NoActiveTeamException, TeamNameAlreadyExistsException, TeamEmptyException, TeamNameEmptyException {

        if (this.player.getActiveTeam().isEmpty()) {
            throw new NoActiveTeamException("Player has no active team.");
        }

        if (this.player.isActiveTeamEmpty()) {
            throw new TeamEmptyException("Active team is empty.");
        }

        this.player.setActiveTeamName(teamName);
        this.playerRepository.createTeam(this.player.getId(), teamName);
        this.persistActiveTeamMembers(this.player.getActiveTeam().orElseThrow(() -> new NoActiveTeamException(
                "The player team to save doesn't exist. The active team should exist now ")));
        this.teams.add(new BugemonTeam(this.player.getActiveTeam().orElseThrow(this.player.noActiveTeamException())));
    }

    /**
     * Modifies the active team in the database.
     *
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     * @throws TeamEmptyException
     *             if the active team is empty
     * @throws TeamNotFoundException
     *             if the team does not exist
     */
    public void modifyActiveTeam() throws NoActiveTeamException, TeamEmptyException, TeamNotFoundException {
        List<TeamMemberDTO> members = new ArrayList<>();
        BugemonTeam team = this.player.getActiveTeam().orElseThrow(this.player.noActiveTeamException());
        team.forEach(b -> members.add(new TeamMemberDTO(this.player.getId(), this.player.getActiveTeamName(),
                b.getName(), team.getSlotPosition(b))));
        this.persistActiveTeamMembers(team);
        this.playerRepository.modifyTeam(this.player.getId(), this.player.getActiveTeamName(), members);
        this.teams.remove(this.player.getActiveTeam().orElseThrow(this.player.noActiveTeamException()));
        this.teams.add(new BugemonTeam(this.player.getActiveTeam().orElseThrow(this.player.noActiveTeamException())));
    }

    /**
     * Renames a team
     *
     * @param oldName
     *            the old team name
     * @param newName
     *            the new team name
     * @throws TeamNotFoundException
     *             if the team does not exist
     * @throws TeamNameAlreadyExistsException
     *             if the team name is already taken
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     */
    public void renameTeam(String oldName, String newName) throws TeamNotFoundException, TeamNameAlreadyExistsException,
            NoActiveTeamException, TeamNameEmptyException {
        this.playerRepository.renameTeam(this.player.getId(), oldName, newName);
        this.teams.stream().filter(t -> t.getName().equals(oldName)).findFirst().ifPresent(t -> t.setName(newName));
        this.player.setActiveTeamName(newName);
    }

    public boolean isActiveTeamEmpty() {
        return this.player.isActiveTeamEmpty();
    }

    public void clearActiveTeam() {
        this.player.clearActiveTeam();
    }

    public boolean isActiveTeamSaved() {
        return this.player.getActiveTeam().map(this.teams::contains).orElse(false);
    }

    public void addOrRemoveBugemonOfActiveTeam(Bugemon bugemon) {
        if (this.player.isActiveTeamEmpty()) {
            this.player.createNewTeam();
        }
        this.player.addOrRemoveBugemonOfActiveTeam(bugemon);
    }

    /**
     * Deletes the active team from the database and clears the active team.
     *
     * @throws NoActiveTeamException
     *             if the player does not have an active team
     * @throws TeamNotFoundException
     *             if the team does not exist
     */
    public void deleteActiveTeam() throws NoActiveTeamException, TeamNotFoundException, TeamNameEmptyException {
        this.playerRepository.deleteTeam(this.player.getId(), this.player.getActiveTeamName());
        this.teams.remove(this.player.getActiveTeam().orElseThrow(this.player.noActiveTeamException()));
        this.player.clearActiveTeam();
    }

    // --- Private Helpers ---

    private void persistActiveTeamMembers(BugemonTeam team) {
        List<PlayerBugemonDTO> owned = this.playerRepository.getPlayerBugemons(this.player.getId());
        for (Bugemon b : team) {
            if (owned.stream().noneMatch(dto -> dto.bugemonName().equals(b.getName()))) {
                this.playerRepository.savePlayerBugemon(this.toDTO(b));
            }
            this.playerRepository.addTeamMember(
                    new TeamMemberDTO(this.player.getId(), team.getName(), b.getName(), team.getSlotPosition(b)));
        }
    }

    private PlayerBugemonDTO toDTO(Bugemon b) {
        return new PlayerBugemonDTO(this.player.getId(), b.getName(), b.getDefense(), b.getAttack(), b.getInitiative(),
                b.getMaxHp(), b.getXp(), b.getLevel());
    }
}
