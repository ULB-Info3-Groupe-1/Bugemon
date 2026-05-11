package ulb.repositories;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import ulb.Configuration;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectDuration;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectResetMalus;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectStatModifier;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.utils.DatabaseHelper;
import ulb.utils.Parser;

public class StaticDataRepository extends AbstractRepository {
    private static final int CRITICAL_TABLES_COUNT = 10;
    private Inventory defaultInventory;
    private Map<String, Attack> allAttacks;
    private List<Item> allItems;

    public StaticDataRepository(DatabaseConnection dbConnection, Map<String, String> queries) {
        super(dbConnection, queries);
        Parser parser = new Parser();
        parser.parse();
        this.defaultInventory = parser.getInventory();
        this.allAttacks = parser.getAttacks();
        this.allItems = parser.getItems();

        this.prepareDatabase(parser);
    }

    // --- DATABASE INIT ---

    private void prepareDatabase(Parser parser) {
        Integer tableCount = executeQuery("areTablesPresent", rs -> rs.getInt("existing_critical_tables")).stream()
                .findFirst().orElse(0);

        if (tableCount < CRITICAL_TABLES_COUNT) {
            executeUpdate("CreateSchema");
            this.addDefaultGameData(parser);
            return;
        }

        Integer rowCount = executeQuery("IsDataEmpty", rs -> rs.getInt("total_rows")).stream().findFirst().orElse(0);
        if (rowCount == 0) {
            this.addDefaultGameData(parser);
            return;
        }

        Integer itemCount = executeQuery("IsItemsEmpty", rs -> rs.getInt("item_count")).stream().findFirst().orElse(0);
        if (itemCount == 0) {
            parser.getItems().forEach(this::saveItem);
        }
    }

    private void addDefaultGameData(Parser parser) {
        parser.getAttacks().values().forEach(this::saveAttackFull);
        parser.getBugemons().forEach(this::saveBugemon);
        parser.getItems().forEach(this::saveItem);
    }

    private void saveAttackFull(Attack attack) {
        executeUpdate("SaveAttack", attack.id(), attack.name(), (attack.type() != null ? attack.type().name() : null),
                attack.description(), attack.power());

        if (attack.effects() != null && !attack.effects().isEmpty()) {
            try (PreparedStatement ps = dbConnection.prepareStatement(getSql("SaveEffect"))) {
                for (Effect effect : attack.effects()) {
                    ps.setString(1, attack.id());
                    this.setEffectParameters(ps, effect);
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (SQLException e) {
                throw new IllegalStateException("Error saving effects for " + attack.id(), e);
            }
        }
    }

    private void saveItem(Item item) {
        executeUpdate("CreateItem", item.id(), item.name(), item.description(), item.type().name(), item.id() + ".png");
        if (item.effect() != null) {
            this.saveItemEffect(item.id(), item.effect());
        }
    }

    private void saveItemEffect(String itemId, Effect effect) {
        switch (effect) {
            case EffectHeal heal -> executeUpdate("SaveItemEffect", itemId, "EffectHeal", heal.target().name(),
                    heal.amount(), null, null, null);
            case EffectStatModifier modifier -> executeUpdate("SaveItemEffect", itemId, "EffectStatModifier",
                    modifier.target().name(), null, modifier.stat() != null ? modifier.stat().name() : null,
                    modifier.modifier(), modifier.duration() == EffectDuration.PERMANENT ? 0 : 1);
            case EffectResetMalus resetMalus -> executeUpdate("SaveItemEffect", itemId, "EffectResetMalus",
                    resetMalus.target().name(), null, null, null, null);
            default -> throw new IllegalStateException("Unknown effect type: " + effect.getClass().getSimpleName());
        }
    }

    // --- DATABASE QUERIES ---

    private Bugemon mapBugemon(ResultSet rs, Map<String, Attack> attackMap) throws SQLException {
        BugemonType type = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_TYPE, BugemonType.class);
        return new BugemonBuilder().name(rs.getString(DatabaseColumns.COL_NAME)).type(type)
                .sprite(rs.getString(DatabaseColumns.COL_SPRITE)).defense(rs.getInt(DatabaseColumns.COL_BASE_DEFENSE))
                .attack(rs.getInt(DatabaseColumns.COL_BASE_ATTACK))
                .initiative(rs.getInt(DatabaseColumns.COL_BASE_INITIATIVE))
                .hp(rs.getInt(DatabaseColumns.COL_BASE_MAX_HP))
                .attackList(List.of(attackMap.get(rs.getString(DatabaseColumns.COL_ATTACK_ID_1)),
                        attackMap.get(rs.getString(DatabaseColumns.COL_ATTACK_ID_2)),
                        attackMap.get(rs.getString(DatabaseColumns.COL_ATTACK_ID_3))))
                .isStarter(rs.getBoolean(DatabaseColumns.COL_IS_STARTER)).build();
    }

    private void setEffectParameters(PreparedStatement psEffect, Effect effect) throws SQLException {
        switch (effect) {
            case EffectStatModifier modifier :
                this.setStatModifierParameters(psEffect, modifier);
                break;

            case EffectHeal heal :
                this.setHealParameters(psEffect, heal);
                break;

            case EffectResetMalus resetMalus :
                this.setResetMalusParameters(psEffect, resetMalus);
                break;

            default :
                break;
        }
    }

    private void setStatModifierParameters(PreparedStatement psEffect, EffectStatModifier modifier)
            throws SQLException {
        psEffect.setString(2, modifier.getClass().getSimpleName());
        psEffect.setString(3, modifier.target().name());
        psEffect.setObject(4, modifier.stat() != null ? modifier.stat().name() : null, Types.VARCHAR);
        psEffect.setInt(5, modifier.modifier());
        psEffect.setString(6, modifier.duration().toString());
        psEffect.setNull(7, Types.INTEGER);
    }

    private void setHealParameters(PreparedStatement psEffect, EffectHeal heal) throws SQLException {
        psEffect.setString(2, heal.getClass().getSimpleName());
        psEffect.setString(3, heal.target().name());
        psEffect.setNull(4, Types.VARCHAR);
        psEffect.setNull(5, Types.INTEGER);
        psEffect.setNull(6, Types.VARCHAR);
        psEffect.setInt(7, heal.amount());
    }

    private void setResetMalusParameters(PreparedStatement psEffect, EffectResetMalus resetMalus) throws SQLException {
        psEffect.setString(2, resetMalus.getClass().getSimpleName());
        psEffect.setString(3, resetMalus.target().name());
        psEffect.setNull(4, Types.VARCHAR);
        psEffect.setNull(5, Types.INTEGER);
        psEffect.setNull(6, Types.VARCHAR);
        psEffect.setNull(7, Types.INTEGER);
    }

    // --- UTILS FOR CLASS USING THIS REPO --

    /**
     * Returns the default starting inventory as defined in objets.json.
     */
    public Inventory getDefaultInventory() {
        return this.defaultInventory;
    }

    /**
     * Returns all the attacks that have been parsed.
     *
     * @return Map<String, Attack>
     */
    public Map<String, Attack> getAllAttacks() {
        return this.allAttacks;
    }

    /**
     * Returns all the items that have been parsed.
     *
     * @return List<Item>
     */
    public List<Item> getAllItems() {
        return this.allItems;
    }

    /**
     * Retrieves all default Bugemons.
     *
     * @return (List<Bugemon>) List of default Bugemons of the game
     */
    public List<Bugemon> getAllDefaultBugemons() {
        Map<String, Attack> attackMap = this.getAllAttacks();
        return executeQuery("GetAllDefaultBugemons", rs -> this.mapBugemon(rs, attackMap));
    }

    /**
     * Saves a Bugemon to the database.
     *
     * @param b
     *            (CreateBugemonDTO) the bugemon to be saved
     */
    public void saveBugemon(CreateBugemonDTO b) {
        String fileName = b.name().toLowerCase().replaceAll("[^a-z0-9]", "_") + ".png";
        try {
            this.saveSpriteFile(b.spriteUrl(), fileName);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        executeUpdate("SaveBugemon", b.name(), b.type().name(), fileName, b.defense(), b.attack(), b.initiative(),
                b.maxHp(), b.isStarter(), b.attack1().id(), b.attack2().id(), b.attack3().id());
    }

    private void saveSpriteFile(URL currentSpriteUrl, String spriteFileName) throws IOException {
        Path dirDestination = Paths.get(Configuration.Paths.SPRITES);

        if (!Files.exists(dirDestination)) {
            Files.createDirectories(dirDestination);
        }

        Path fileTarget = dirDestination.resolve(spriteFileName);
        try (InputStream in = currentSpriteUrl.openStream()) {
            Files.copy(in, fileTarget, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Impossible to save sprite file: " + fileTarget, e);
        }
    }
}
