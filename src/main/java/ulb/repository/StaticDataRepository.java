package ulb.repository;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectDuration;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectResetMalus;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectStatModifier;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.repository.dto.CreateBugemonDTO;
import ulb.repository.dto.StaticBugemonDataDTO;
import ulb.utils.DatabaseHelper;
import ulb.utils.Parser;

public class StaticDataRepository extends AbstractRepository {
    private static final int CRITICAL_TABLES_COUNT = 7;
    private static final String SPRITE_DIRECTORY_PATH = "assets/sprites";

    private final DatabaseConnection dbConnection;

    public StaticDataRepository(DatabaseConnection dbConnection, Map<String, String> queries) {
        super(queries);
        this.dbConnection = dbConnection;
        this.prepareDatabase();
    }

    private void prepareDatabase() {
        // Verify if the critical tables exist in the database. If not, we create the schema and add
        // the default game data
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("isTablesPresent"))) {
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt("existing_critical_tables") < CRITICAL_TABLES_COUNT) {
                this.createSchema();
                this.addDefaultGameData();
                return;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("isTablesPresent failed", e);
        }

        // If the tables exist, we check if they contain the static game data. If not, we add the
        // static game data
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("IsDataEmpty"))) {
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt("total_rows") == 0) {
                this.addDefaultGameData();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("IsDataEmpty failed", e);
        }
    }

    private void createSchema() {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("CreateSchema"))) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("createSchema failed", e);
        }
    }

    public void addDefaultGameData() {
        Parser parser = new Parser();
        parser.parse();
        this.saveGameDataAttacks(parser.getAttacks());
        for (CreateBugemonDTO bugemon : parser.getBugemons()) {
            this.saveBugemon(bugemon);
        }
    }

    private void saveGameDataAttacks(Map<String, Attack> attacks) {
        for (Attack attack : attacks.values()) {
            try {
                this.saveAttack(attack);
                this.saveAttackEffects(attack);
            } catch (SQLException e) {
                throw new IllegalStateException(
                        "Error occurred while saving the default game data for attack: " + attack.id(), e);
            }
        }
    }

    private void saveAttack(Attack attack) throws SQLException {
        try (PreparedStatement psAttack = this.dbConnection.prepareStatement(this.getSql("SaveAttack"))) {
            psAttack.setString(1, attack.id());
            psAttack.setString(2, attack.name());
            psAttack.setObject(3, attack.type() != null ? attack.type().name() : null, Types.VARCHAR);
            psAttack.setString(4, attack.description());
            psAttack.setInt(5, attack.power());
            psAttack.executeUpdate();
        }
    }

    private void saveAttackEffects(Attack attack) throws SQLException {
        if (attack.effects() == null || attack.effects().isEmpty()) {
            return;
        }

        try (PreparedStatement psEffect = this.dbConnection.prepareStatement(this.getSql("SaveEffect"))) {
            for (Effect effect : attack.effects()) {
                psEffect.setString(1, attack.id()); // Foreign key to the attack
                this.setEffectParameters(psEffect, effect);
                psEffect.addBatch();
            }
            psEffect.executeBatch();
        }
    }

    private void setEffectParameters(PreparedStatement psEffect, Effect effect) throws SQLException {
        switch (effect) {
            case EffectStatModifier modifier :
                this.setStatModifierParameters(psEffect, modifier);
                break;

            case EffectHeal heal :
                this.setHealParameters(psEffect, heal);
                break;

            case EffectResetMalus malus :
                this.setResetMalusParameters(psEffect, malus);
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

    private void setResetMalusParameters(PreparedStatement psEffect, EffectResetMalus malus) throws SQLException {
        psEffect.setString(2, malus.getClass().getSimpleName());
        psEffect.setString(3, malus.target().name());
        psEffect.setNull(4, Types.VARCHAR);
        psEffect.setNull(5, Types.INTEGER);
        psEffect.setNull(6, Types.VARCHAR);
        psEffect.setNull(7, Types.INTEGER);
    }

    // ─── UTILS FOR CLASS USING THIS REPO ──

    public List<Bugemon> getAllDefaultBugemons() {
        List<Bugemon> bugemons = new ArrayList<>();
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("GetAllDefaultBugemons"))) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BugemonType type = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_TYPE, BugemonType.class);
                Attack attack1 = this.getAttackById(rs.getString(DatabaseColumns.COL_ATTACK_ID_1));
                Attack attack2 = this.getAttackById(rs.getString(DatabaseColumns.COL_ATTACK_ID_2));
                Attack attack3 = this.getAttackById(rs.getString(DatabaseColumns.COL_ATTACK_ID_3));
                BugemonBuilder builder = new BugemonBuilder();
                builder.name(rs.getString(DatabaseColumns.COL_NAME)).type(type)
                        .sprite(rs.getString(DatabaseColumns.COL_SPRITE))
                        .defense(rs.getInt(DatabaseColumns.COL_BASE_DEFENSE))
                        .attack(rs.getInt(DatabaseColumns.COL_BASE_ATTACK))
                        .initiative(rs.getInt(DatabaseColumns.COL_BASE_INITIATIVE))
                        .hp(rs.getInt(DatabaseColumns.COL_BASE_MAX_HP)).addAttack(attack1).addAttack(attack2)
                        .addAttack(attack3).isStarter(rs.getBoolean(DatabaseColumns.COL_IS_STARTER));

                bugemons.add(builder.build());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getAllDefaultBugemons failed", e);
        }
        return bugemons;
    }

    public Attack getAttackById(String attackId) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("GetAttackById"))) {
            ps.setString(1, attackId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                List<Effect> effects = this.getEffectByAttackId(attackId);
                BugemonType type = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_TYPE, BugemonType.class);
                return new Attack(rs.getString(DatabaseColumns.COL_ID), rs.getString(DatabaseColumns.COL_NAME), type,
                        rs.getString(DatabaseColumns.COL_DESCRIPTION), rs.getInt(DatabaseColumns.COL_POWER), effects);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getAttackById failed for id: " + attackId, e);
        }
        throw new IllegalStateException("Attack not found for id: " + attackId);
    }

    public List<Effect> getEffectByAttackId(String attackId) {
        List<Effect> effects = new ArrayList<>();
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("GetEffectByAttackId"))) {
            ps.setString(1, attackId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                switch (rs.getString(DatabaseColumns.COL_TYPE)) {
                    case "EffectStatModifier" :
                        EffectTarget target = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_TARGET,
                                EffectTarget.class);
                        EffectStat stat = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_STAT, EffectStat.class);
                        String duration = (rs.getString(DatabaseColumns.COL_DURATION) != null)
                                ? rs.getString(DatabaseColumns.COL_DURATION)
                                : "0_tour";
                        effects.add(new EffectStatModifier(target, stat, rs.getInt(DatabaseColumns.COL_MODIFIER),
                                EffectDuration.fromLabel(duration)));
                        break;

                    case "EffectHeal" :
                        target = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_TARGET, EffectTarget.class);
                        effects.add(new EffectHeal(target, rs.getInt(DatabaseColumns.COL_AMOUNT)));
                        break;

                    case "EffectResetMalus" :
                        target = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_TARGET, EffectTarget.class);
                        effects.add(new EffectResetMalus(target));
                        break;

                    default :
                        throw new IllegalStateException("Unknown effect type for attack id: " + attackId);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getEffectByAttackId failed for attack id: " + attackId, e);
        }
        return effects;
    }

    /**
     * Save a Bugemon to the database. It also saves the sprite file for the Bugemon. It set the sprite file name to the
     * name of the Bugemon in lowercase and replacing non-alphanumeric characters with underscores.
     *
     * @param bugemon
     *            (CreateBugemonDTO) the Bugemon to be saved
     */
    public void saveBugemon(CreateBugemonDTO bugemon) {
        String fileName = bugemon.name().toLowerCase().replaceAll("[^a-z0-9]", "_") + ".png";

        try {
            this.saveSpriteFile(bugemon.spriteUrl(), fileName);
        } catch (IOException e) {
            throw new UncheckedIOException("Error occurred while saving the sprite for bugemon: " + bugemon.name(), e);
        }

        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("SaveBugemon"))) {
            ps.setString(1, bugemon.name());
            ps.setString(2, bugemon.type().name());
            ps.setString(3, fileName);
            ps.setInt(4, bugemon.defense());
            ps.setInt(5, bugemon.attack());
            ps.setInt(6, bugemon.initiative());
            ps.setInt(7, bugemon.maxHp());
            ps.setBoolean(8, bugemon.isStarter());
            // Assuming each Bugemon has exactly 3 attacks, we insert them in the order they
            // appear in the list
            ps.setString(9, bugemon.attack1().id());
            ps.setString(10, bugemon.attack2().id());
            ps.setString(11, bugemon.attack3().id());
            ps.addBatch();
            ps.executeBatch();
        } catch (SQLException e) {
            throw new IllegalStateException("saveGameDataBugemon failed", e);
        }
    }

    /**
     * Save the sprite file for a Bugemon.
     *
     * @param currentSpriteUrl
     *            the URL of the sprite file to be saved (to get access to the file)
     * @param spriteFileName
     *            the name of the sprite file to be saved
     * @throws IOException
     *             if the sprite file cannot be saved
     */
    private void saveSpriteFile(URL currentSpriteUrl, String spriteFileName) throws IOException {
        Path dirDestination = Paths.get(SPRITE_DIRECTORY_PATH);

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

    /**
     * Get a Bugemon by its name. It return a StaticBugemonDataDTO with just the static data info of the bugemon.
     *
     * @param name
     *            (String) the name of the bugemon
     * @return (StaticBugemonDataDTO) the bugemon
     */
    public StaticBugemonDataDTO getBugemonByName(String name) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.getSql("GetBugemonByName"))) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return this.getBugemonFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getBugemonByName failed for name: " + name, e);
        }
        return null;
    }

    /**
     * Get a Bugemon from a ResultSet.
     *
     * @param rs
     *            (ResultSet) the ResultSet
     * @return (StaticBugemonDataDTO) the Bugemon
     * @throws SQLException
     *             if the ResultSet is not valid
     */
    private StaticBugemonDataDTO getBugemonFromResultSet(ResultSet rs) throws SQLException {
        String name = rs.getString(DatabaseColumns.COL_NAME);
        String type = rs.getString(DatabaseColumns.COL_TYPE);
        String spriteFileName = rs.getString(DatabaseColumns.COL_SPRITE);
        boolean isStarter = rs.getBoolean(DatabaseColumns.COL_IS_STARTER);

        Attack attack1 = this.getAttackById(rs.getString(DatabaseColumns.COL_ATTACK_ID_1));
        Attack attack2 = this.getAttackById(rs.getString(DatabaseColumns.COL_ATTACK_ID_2));
        Attack attack3 = this.getAttackById(rs.getString(DatabaseColumns.COL_ATTACK_ID_3));
        List<Attack> attacks = new ArrayList<>();
        attacks.add(attack1);
        attacks.add(attack2);
        attacks.add(attack3);

        return new StaticBugemonDataDTO(name, type, spriteFileName, attacks, isStarter);
    }
}
