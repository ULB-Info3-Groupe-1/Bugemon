package ulb.repository;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

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
import ulb.utils.DatabaseHelper;
import ulb.utils.Parser;

public class StaticDataRepository {

    private static final String SPRITE_RESOURCE_PATH = "/png/";

    private final DatabaseRepository dbRepository;
    private final DatabaseConnection dbConnection;

    public StaticDataRepository(DatabaseRepository dbRepository, DatabaseConnection dbConnection) {
        this.dbRepository = dbRepository;
        this.dbConnection = dbConnection;
    }

    public void addDefaultGameData() {
        Parser parser = new Parser();
        parser.parse();
        this.saveGameDataAttacks(parser.getAttacks());
        this.saveGameDataBugemon(parser.getBugemons());
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
        try (PreparedStatement psAttack = this.dbConnection.prepareStatement(this.dbRepository.getSql("SaveAttack"))) {
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

        try (PreparedStatement psEffect = this.dbConnection.prepareStatement(this.dbRepository.getSql("SaveEffect"))) {
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

    private void saveGameDataBugemon(List<Bugemon> bugemons) {
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("SaveBugemon"))) {
            for (CreateBugemonDTO bugemon : bugemons) {
                ps.setString(1, bugemon.name());
                ps.setString(2, bugemon.type().name());
                ps.setString(3, bugemon.spriteUrl());
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
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new IllegalStateException("saveGameDataBugemon failed", e);
        }
    }

    // ─── UTILS FOR CLASS USING THIS REPO ──

    public List<Bugemon> getAllDefaultBugemons() {
        List<Bugemon> bugemons = new ArrayList<>();
        try (PreparedStatement ps = this.dbConnection
                .prepareStatement(this.dbRepository.getSql("GetAllDefaultBugemons"))) {
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
                        .attack(rs.getInt(DatabaseColumns.COL_BASE_ATTACK_POWER))
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
        try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("GetAttackById"))) {
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
        try (PreparedStatement ps = this.dbConnection
                .prepareStatement(this.dbRepository.getSql("GetEffectByAttackId"))) {
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
     * Save a new Bugemon to the database.
     *
     * @param request
     *            the BugemonCreateRequest containing the details of the Bugemon to be saved.
     */
    public void saveBugemon(CreateBugemonDTO request) {
        //
        // try (PreparedStatement ps = this.dbConnection.prepareStatement(this.dbRepository.getSql("SaveBugemon"))) {
        // ps.setString(1, request.name());
        // ps.setString(2, request.type().name());
        // ps.setString(3, request.spriteURL());
        // ps.setInt(4, request.defense());
        // ps.setInt(5, request.attack());
        // ps.setInt(6, request.initiative());
        // ps.setInt(7, request.maxHp());
        // ps.setBoolean(8, request.isStarter());
        // // Assuming the request contains exactly 3 attack IDs, we insert them in the order they appear in the list
        // ps.setString(9, request.attackIds().get(0));
        // ps.setString(10, request.attackIds().get(1));
        // ps.setString(11, request.attackIds().get(2));
        // ps.executeUpdate();
        // } catch (SQLException e) {
        // throw new IllegalStateException("saveBugemon failed for name: " + request.name(), e);
        // }
    }

    /**
     * Save the sprite file for a Bugemon.
     *
     * @param spriteFileName
     *            the name of the sprite file to be saved, it should include the file extension (e.g.,
     *            "my_bugemon.png").
     * @throws IOException
     *             if an error occurs while saving the sprite file, such as if the resource path is not found or if
     *             there is an issue with file I/O operations.
     */
    private void saveSpriteFile(BufferedImage spriteImage, String spriteFileName) throws IOException {
        URL url = getClass().getResource(SPRITE_RESOURCE_PATH);

        if (url == null) {
            throw new IOException("The sprite resource path " + SPRITE_RESOURCE_PATH + " is not found.");
        }

        try {
            Path dirDestination = Paths.get(url.toURI());
            Path fileTarget = dirDestination.resolve(spriteFileName);

            String formatName = spriteFileName.substring(spriteFileName.lastIndexOf('.') + 1);

            boolean success = ImageIO.write(spriteImage, formatName, fileTarget.toFile());

            if (!success) {
                throw new IOException("No appropriate writer found for format: " + formatName);
            }
        } catch (URISyntaxException e) {
            throw new IOException("Error resolving URI", e);
        }
    }
}
