package ulb.repositories.postgres;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import ulb.Configuration;
import ulb.common.EffectDuration;
import ulb.models.bugemon.Attack;
import ulb.models.effect.Effect;
import ulb.models.effect.HealEffect;
import ulb.models.effect.ResetMalusEffect;
import ulb.models.effect.StatModifierEffect;
import ulb.models.item.Item;
import ulb.repositories.dto.CreateBugemonDTO;

public class DatabaseInitializer extends AbstractRepository {

    private static final int CRITICAL_TABLES_COUNT = 10;
    private static final String SAVE_ITEM_EFFECT_QUERY = "SaveItemEffect";

    private final List<CreateBugemonDTO> defaultBugemons;
    private final Map<String, Attack> attacks;
    private final List<Item> items;

    public DatabaseInitializer(DatabaseConnection dbConnection, Map<String, String> queries,
            List<CreateBugemonDTO> bugemonData, Map<String, Attack> attackData, List<Item> itemData) {
        super(dbConnection, queries);
        this.defaultBugemons = bugemonData;
        this.attacks = attackData;
        this.items = itemData;
    }

    public void initialize() {
        Integer tableCount = this.executeQuery("areTablesPresent",
                rs -> rs.getInt("existing_critical_tables")).stream().findFirst().orElse(0);

        if (tableCount < CRITICAL_TABLES_COUNT) {
            this.executeUpdate("CreateSchema");
            this.seedGameData();
            return;
        }

        Integer rowCount = this.executeQuery("IsDataEmpty",
                rs -> rs.getInt("total_rows")).stream().findFirst().orElse(0);
        if (rowCount == 0) {
            this.seedGameData();
            return;
        }

        Integer itemCount = this.executeQuery("IsItemsEmpty",
                rs -> rs.getInt("item_count")).stream().findFirst().orElse(0);
        if (itemCount == 0) {
            this.items.forEach(this::saveItem);
        }
    }

    private void seedGameData() {
        this.attacks.values().forEach(this::saveAttack);
        this.defaultBugemons.forEach(this::saveBugemon);
        this.items.forEach(this::saveItem);
    }

    private void saveAttack(Attack attack) {
        this.executeUpdate("SaveAttack", attack.id(), attack.name(),
                attack.type() != null ? attack.type().name() : null,
                attack.description(), attack.power());

        if (attack.effects() == null || attack.effects().isEmpty()) {
            return;
        }
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("SaveEffect"))) {
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

    private void saveBugemon(CreateBugemonDTO bugemon) {
        String fileName = bugemon.name().toLowerCase().replaceAll("[^a-z0-9]", "_") + ".png";
        try {
            this.saveSpriteFile(bugemon.spriteUrl(), fileName);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        this.executeUpdate("SaveBugemon", bugemon.name(), bugemon.type().name(), fileName,
                bugemon.defense(), bugemon.attack(), bugemon.initiative(), bugemon.maxHp(),
                bugemon.isStarter(), bugemon.attack1().id(), bugemon.attack2().id(), bugemon.attack3().id());
    }

    private void saveItem(Item item) {
        this.executeUpdate("CreateItem", item.id(), item.name(), item.description(),
                item.type().name(), item.id() + ".png");
        if (item.effect() != null) {
            this.saveItemEffect(item.id(), item.effect());
        }
    }

    private void saveItemEffect(String itemId, Effect effect) {
        switch (effect) {
            case HealEffect heal ->
                this.executeUpdate(SAVE_ITEM_EFFECT_QUERY, itemId, "EffectHeal",
                        heal.getTarget().name(), heal.getAmount(), null, null, null);
            case StatModifierEffect modifier ->
                this.executeUpdate(SAVE_ITEM_EFFECT_QUERY, itemId, "EffectStatModifier",
                        modifier.getTarget().name(), null,
                        modifier.getStat() != null ? modifier.getStat().name() : null,
                        modifier.getModifier(),
                        modifier.getDuration() == EffectDuration.PERMANENT ? 0 : 1);
            case ResetMalusEffect resetMalus ->
                this.executeUpdate(SAVE_ITEM_EFFECT_QUERY, itemId, "EffectResetMalus",
                        resetMalus.getTarget().name(), null, null, null, null);
            default ->
                throw new IllegalStateException("Unknown effect type: " + effect.getClass().getSimpleName());
        }
    }

    private void setEffectParameters(PreparedStatement ps, Effect effect) throws SQLException {
        switch (effect) {
            case StatModifierEffect modifier -> this.setStatModifierParameters(ps, modifier);
            case HealEffect heal -> this.setHealParameters(ps, heal);
            case ResetMalusEffect resetMalus -> this.setResetMalusParameters(ps, resetMalus);
            default ->
                throw new IllegalStateException("Unknown effect type: " + effect.getClass().getSimpleName());
        }
    }

    private void setStatModifierParameters(PreparedStatement ps, StatModifierEffect modifier) throws SQLException {
        ps.setString(2, modifier.getClass().getSimpleName());
        ps.setString(3, modifier.getTarget().name());
        ps.setObject(4, modifier.getStat() != null ? modifier.getStat().name() : null, Types.VARCHAR);
        ps.setInt(5, modifier.getModifier());
        ps.setString(6, modifier.getDuration().toString());
        ps.setNull(7, Types.INTEGER);
    }

    private void setHealParameters(PreparedStatement ps, HealEffect heal) throws SQLException {
        ps.setString(2, heal.getClass().getSimpleName());
        ps.setString(3, heal.getTarget().name());
        ps.setNull(4, Types.VARCHAR);
        ps.setNull(5, Types.INTEGER);
        ps.setNull(6, Types.VARCHAR);
        ps.setInt(7, heal.getAmount());
    }

    private void setResetMalusParameters(PreparedStatement ps, ResetMalusEffect resetMalus) throws SQLException {
        ps.setString(2, resetMalus.getClass().getSimpleName());
        ps.setString(3, resetMalus.getTarget().name());
        ps.setNull(4, Types.VARCHAR);
        ps.setNull(5, Types.INTEGER);
        ps.setNull(6, Types.VARCHAR);
        ps.setNull(7, Types.INTEGER);
    }

    private void saveSpriteFile(URL spriteUrl, String spriteFileName) throws IOException {
        Path dirDestination = Paths.get(Configuration.Paths.SPRITES);
        if (!Files.exists(dirDestination)) {
            Files.createDirectories(dirDestination);
        }
        Path fileTarget = dirDestination.resolve(spriteFileName);
        try (InputStream in = spriteUrl.openStream()) {
            Files.copy(in, fileTarget, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Impossible to save sprite file: " + fileTarget, e);
        }
    }
}
