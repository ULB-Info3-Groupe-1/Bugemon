package ulb.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.factories.BugemonFactory;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.Item.ItemType;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectDuration;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectResetMalus;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectStatModifier;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.StaticBugemonDataDTO;
import ulb.repositories.dto.TeamDTO;
import ulb.repositories.dto.TeamMemberDTO;
import ulb.repositories.exceptions.PlayernameIsEmptyException;
import ulb.repositories.exceptions.TeamEmptyException;
import ulb.repositories.exceptions.TeamNameAlreadyExistsException;
import ulb.repositories.exceptions.TeamNameEmptyException;
import ulb.repositories.exceptions.TeamNotFoundException;

public class PlayerRepository extends AbstractRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerRepository.class);
    private final StaticDataRepository staticDataRepository;

    public PlayerRepository(DatabaseConnection dbConnection, StaticDataRepository staticDataRepository,
            Map<String, String> queries) {
        super(dbConnection, queries);
        this.staticDataRepository = staticDataRepository;
    }

    // --- PLAYERS ---

    /**
     * Returns the player id or creates a new player and returns its id
     *
     * @param playername
     *            the player name to create or retrieve
     * @return (int) the player id
     * @throws PlayernameIsEmptyException
     *             if the playername is null or empty
     */
    public int getPlayerIdOrCreatePlayer(String playername) throws PlayernameIsEmptyException {
        if (playername == null || playername.isEmpty()) {
            throw new PlayernameIsEmptyException("Playername cannot be null or empty");
        }
        Optional<Integer> playerId = executeQuery("GetPlayerByPlayername", rs -> rs.getInt(DatabaseColumns.COL_ID),
                playername).stream().findFirst();
        if (playerId.isEmpty()) {
            return this.createPlayer(playername);
        }
        return playerId.get();
    }

    private int createPlayer(String playername) {
        int playerId = executeQuery("CreatePlayer", rs -> rs.getInt(DatabaseColumns.COL_ID), playername).stream()
                .findFirst().orElseThrow(() -> new IllegalStateException("No ID returned"));
        this.addDefaultInventory(playerId);
        return playerId;
    }

    // --- BUGEMONS ---

    public void savePlayerBugemon(PlayerBugemonDTO d) {
        LOG.debug("Saving player bugemon: {}", d);
        executeUpdate("SavePlayerBugemon", d.playerId(), d.bugemonName(), d.currentDefense(), d.currentAttackPower(),
                d.currentInitiative(), d.currentMaxHp(), d.currentXp(), d.currentLevel());
    }

    public void updatePlayerBugemon(PlayerBugemonDTO d) {
        LOG.debug("Updating player bugemon: {}", d);
        executeUpdate("UpdatePlayerBugemon", d.currentDefense(), d.currentAttackPower(), d.currentInitiative(),
                d.currentMaxHp(), d.currentXp(), d.currentLevel(), d.playerId(), d.bugemonName());
    }

    public List<PlayerBugemonDTO> getPlayerBugemons(int playerId) {
        LOG.debug("Getting bugemons for playerId: {}", playerId);
        return executeQuery("GetPlayerBugemons", rs -> new PlayerBugemonDTO(rs.getInt(DatabaseColumns.COL_PLAYER_ID),
                rs.getString(DatabaseColumns.COL_BUGEMON_NAME), rs.getInt(DatabaseColumns.COL_CURRENT_DEFENSE),
                rs.getInt(DatabaseColumns.COL_CURRENT_ATTACK), rs.getInt(DatabaseColumns.COL_CURRENT_INITIATIVE),
                rs.getInt(DatabaseColumns.COL_CURRENT_MAX_HP), rs.getInt(DatabaseColumns.COL_CURRENT_XP),
                rs.getInt(DatabaseColumns.COL_CURRENT_LEVEL)), playerId);
    }

    // --- TEAMS ---

    public void createTeam(int playerId, String teamName)
            throws TeamNameAlreadyExistsException, TeamNameEmptyException {
        this.checkValidName(teamName);
        if (this.teamNameAlreadyExists(playerId, teamName)) {
            throw new TeamNameAlreadyExistsException(" Team name already exists: " + teamName);
        }
        LOG.debug("Creating team '{}' for playerId: {}", teamName, playerId);
        executeUpdate("CreateTeam", playerId, teamName);
    }

    /**
     * Delete a team and its members
     *
     * @param playerId
     *            the player's ID who owns the team
     * @param teamName
     *            the team's name to delete
     */
    public void deleteTeam(int playerId, String teamName) throws TeamNotFoundException, TeamNameEmptyException {
        this.checkValidName(teamName);
        this.checkTeamExists(playerId, teamName);
        LOG.debug("Deleting team '{}' for playerId: {}", teamName, playerId);
        executeUpdate("DeleteTeamMembers", playerId, teamName);
        executeUpdate("DeleteTeam", playerId, teamName);
    }

    /**
     * Rename a team
     *
     * @param playerId
     *            the player's ID who owns the team to rename
     * @param oldTeamName
     *            the team's name to rename
     * @param newTeamName
     *            the team's new name
     * @throws TeamNameAlreadyExistsException
     *             if the new team name already exists
     * @throws TeamNotFoundException
     *             if the old team name does not exist
     */
    public void renameTeam(int playerId, String oldTeamName, String newTeamName)
            throws TeamNameAlreadyExistsException, TeamNotFoundException, TeamNameEmptyException {
        if (this.teamNameAlreadyExists(playerId, newTeamName)) {
            throw new TeamNameAlreadyExistsException(" Team name already exists: " + newTeamName);
        }
        this.checkTeamExists(playerId, oldTeamName);
        this.checkValidName(newTeamName);
        LOG.debug("Renaming team for playerId: {} from '{}' to '{}'", playerId, oldTeamName, newTeamName);
        executeUpdate("RenameTeam", newTeamName, playerId, oldTeamName);
    }

    public void modifyTeam(int playerId, String teamName, List<TeamMemberDTO> members)
            throws TeamEmptyException, TeamNotFoundException {
        if (members.isEmpty()) {
            throw new TeamEmptyException("Team is empty");
        }
        this.checkTeamExists(playerId, teamName);
        executeUpdate("RemoveTeamComposition", playerId, teamName);
        members.forEach(this::addTeamMember);
    }

    /**
     * Load all teams for a player
     *
     * @param playerId
     *            the player's ID who owns the teams
     * @return (List<BugemonTeam>) the teams of the player to be loaded
     * @throws TeamNotFoundException
     *             if the player has no teams
     */
    public List<BugemonTeam> loadTeams(int playerId) {
        List<BugemonTeam> playerTeams = new ArrayList<>();

        List<PlayerBugemonDTO> allPlayerBugemons = this.getPlayerBugemons(playerId);

        Map<String, Bugemon> defaultBugemonsMap = this.staticDataRepository.getAllDefaultBugemons().stream()
                .collect(Collectors.toMap(Bugemon::getName, b -> b));

        for (TeamDTO teamDto : this.getPlayerTeams(playerId)) {
            BugemonTeam team = new BugemonTeam();
            team.setName(teamDto.teamName());

            this.getTeamMembers(playerId, teamDto.teamName()).forEach(member -> allPlayerBugemons.stream()
                    .filter(pb -> pb.bugemonName().equals(member.bugemonName())).findFirst().ifPresent(pb -> {
                        Bugemon base = defaultBugemonsMap.get(pb.bugemonName());
                        if (base != null) {
                            StaticBugemonDataDTO staticDto = new StaticBugemonDataDTO(base.getName(),
                                    base.getType().name(), base.getSpriteURL(), base.getAttackList(), base.isStarter());
                            team.add(BugemonFactory.createBugemon(staticDto, pb));
                        }
                    }));
            playerTeams.add(team);
        }
        return playerTeams;
    }

    private List<TeamDTO> getPlayerTeams(int playerId) {
        LOG.debug("Getting teams for playerId: {}", playerId);
        return executeQuery("GetPlayerTeams",
                rs -> new TeamDTO(rs.getInt(DatabaseColumns.COL_PLAYER_ID), rs.getString(DatabaseColumns.COL_NAME)),
                playerId);
    }

    // --- TEAM MEMBERS ---

    public void addTeamMember(TeamMemberDTO dto) {
        executeUpdate("AddTeamMember", dto.playerId(), dto.teamName(), dto.bugemonName(), dto.slotPosition());
    }

    public void removeTeamMember(int playerId, String teamName, String bugemonName) throws TeamNotFoundException {
        this.checkTeamExists(playerId, teamName);
        executeUpdate("RemoveTeamMember", playerId, teamName, bugemonName);
    }

    public List<TeamMemberDTO> getTeamMembers(int playerId, String teamName) {
        return executeQuery("GetTeamMembers",
                rs -> new TeamMemberDTO(rs.getInt(DatabaseColumns.COL_PLAYER_ID),
                        rs.getString(DatabaseColumns.COL_TEAM_NAME), rs.getString(DatabaseColumns.COL_BUGEMON_NAME),
                        rs.getInt(DatabaseColumns.COL_SLOT_POSITION)),
                playerId, teamName);
    }

    // --- Items/Inventory ---

    public Inventory getPlayerInventory(int playerId) {
        LOG.debug("Getting inventory for playerId: {}", playerId);
        Inventory inventory = new Inventory();
        executeQuery("GetPlayerInventory", rs -> {
            String effectType = rs.getString("effect_type");
            Effect effect = effectType != null ? this.buildItemEffect(rs, effectType) : null;
            Item item = new Item(rs.getString(DatabaseColumns.COL_ITEM_ID), rs.getString(DatabaseColumns.COL_NAME),
                    rs.getString(DatabaseColumns.COL_DESCRIPTION),
                    ItemType.valueOf(rs.getString(DatabaseColumns.COL_CATEGORY)), effect);
            inventory.addItem(item, rs.getInt(DatabaseColumns.COL_AMOUNT));
            return null;
        }, playerId);
        return inventory;
    }

    private Effect buildItemEffect(java.sql.ResultSet rs, String effectType) throws java.sql.SQLException {
        EffectTarget target = EffectTarget.valueOf(rs.getString("effect_target"));
        return switch (effectType) {
            case "EffectHeal" -> new EffectHeal(target, rs.getInt("effect_value"));
            case "EffectStatModifier" -> new EffectStatModifier(target, EffectStat.valueOf(rs.getString("effect_stat")),
                    rs.getInt("effect_modifier"),
                    rs.getInt("effect_duration") == 0 ? EffectDuration.PERMANENT : EffectDuration.ONE_TURN);
            case "EffectResetMalus" -> new EffectResetMalus(target);
            default -> throw new IllegalStateException("Unknown item effect type: " + effectType);
        };
    }

    public void addItemToPlayer(int playerId, String itemId, int quantity) {
        LOG.debug("Adding {}x {} to playerId: {}", quantity, itemId, playerId);
        executeUpdate("CreateItemPlayer", playerId, itemId, quantity);
    }

    public void updateItemAmount(int playerId, String itemId, int newAmount) {
        LOG.debug("Updating item {} amount to {} for playerId: {}", itemId, newAmount, playerId);
        executeUpdate("UpdateItemAmount", newAmount, playerId, itemId);
    }

    private void addDefaultInventory(int playerId) {
        Inventory defaultInventory = this.staticDataRepository.getDefaultInventory();
        defaultInventory.getMap()
                .forEach((item, quantity) -> executeUpdate("CreateItemPlayer", playerId, item.id(), quantity));
    }

    // --- Utils ---

    private boolean teamNameAlreadyExists(int playerId, String teamName) {
        return !executeQuery("TeamNameAlreadyExists", rs -> true, playerId, teamName).isEmpty();
    }

    private void checkTeamExists(int playerId, String teamName) throws TeamNotFoundException {
        if (this.getPlayerTeams(playerId).stream().noneMatch(team -> team.teamName().equals(teamName))) {
            throw new TeamNotFoundException(" Team name does not exist: " + teamName);
        }
    }

    private void checkValidName(String name) throws TeamNameEmptyException {
        if (name == null || name.isBlank()) {
            throw new TeamNameEmptyException(name);
        }
    }
}
