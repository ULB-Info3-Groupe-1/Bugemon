package ulb.repository;

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
import ulb.utils.DatabaseHelper;
import ulb.utils.Parser;

public class StaticDataRepository {
    private final DatabaseRepository dbRepository;

    private final DatabaseConnection dbConnection;

    public StaticDataRepository(DatabaseRepository dbRepository, DatabaseConnection dbConnection) {
        this.dbRepository = dbRepository;
        this.dbConnection = dbConnection;
    }

    /**
     * Add the default game data to the database. This method is used during the game initialization
     * to load the static data of the game. It uses a Parser to read the default game data from a
     * source (e.g., JSON files) and then saves this data to the database using helper methods for
     * attacks and Bugemons.
     */
    public void addDefaultGameData() {
        Parser parser = new Parser();
        parser.parse();
        saveGameDataAttacks(parser.getAttacks());
        saveGameDataBugemon(parser.getBugemons());
    }

    /**
     * Save the default attacks to the database. This method is used during the game initialization
     * to load the static data of the game. It takes a map of attack IDs to Attack objects and saves
     * each attack to the database. For each attack, it also saves the associated effects, ensuring
     * that all necessary data is present in the database.
     * @param attacks a map of attack IDs to Attack objects representing the default attacks to be
     *         saved in the database.
     */
    private void saveGameDataAttacks(Map<String, Attack> attacks) {
        for (Attack attack : attacks.values()) {
            try {
                saveAttack(attack);
                saveAttackEffects(attack);
            } catch (SQLException e) {
                throw new IllegalStateException(
                        "Error occurred while saving the default game data for attack: "
                                + attack.id(),
                        e);
            }
        }
    }

    /**
     * Save an attack to the database. This method is used as part of the process to load the static
     * data of the game during initialization. It takes an Attack object and saves its details to
     * the database using a prepared statement.
     * @param attack the Attack object to be saved to the database
     * @throws SQLException if an error occurs while saving the attack to the database
     */
    private void saveAttack(Attack attack) throws SQLException {
        try (PreparedStatement psAttack =
                     dbConnection.prepareStatement(this.dbRepository.getSql("SaveAttack"))) {
            psAttack.setString(1, attack.id());
            psAttack.setString(2, attack.name());
            psAttack.setObject(3, attack.type() != null ? attack.type().name() : null,
                               Types.VARCHAR);
            psAttack.setString(4, attack.description());
            psAttack.setInt(5, attack.power());
            psAttack.executeUpdate();
        }
    }

    /**
     * Save the effects of an attack to the database. This method is used as part of the process to
     * load the static data of the game during initialization. It takes an Attack object and saves
     * its associated effects to the database using a prepared statement.
     * @param attack the Attack object for which to save effects
     * @throws SQLException if an error occurs while saving the attack effects to the database
     */
    private void saveAttackEffects(Attack attack) throws SQLException {
        if (attack.effects() == null || attack.effects().isEmpty()) {
            return;
        }

        try (PreparedStatement psEffect =
                     dbConnection.prepareStatement(this.dbRepository.getSql("SaveEffect"))) {
            for (Effect effect : attack.effects()) {
                psEffect.setString(1, attack.id()); // Foreign key to the attack
                setEffectParameters(psEffect, effect);
                psEffect.addBatch();
            }
            psEffect.executeBatch();
        }
    }

    /**
     * Set the parameters of a prepared statement for an effect based on the type of the effect.
     * This method is used as part of the process to save the effects of an attack to the database
     * during game initialization. It takes a PreparedStatement and an Effect object, and sets the
     * parameters of the PreparedStatement according to the specific type of the Effect (e.g.,
     * EffectStatModifier, EffectHeal, EffectResetMalus).
     * @param psEffect the PreparedStatement for which to set parameters
     * @param effect the Effect object for which to set parameters
     * @throws SQLException if an error occurs while setting the effect parameters
     */
    private void setEffectParameters(PreparedStatement psEffect, Effect effect)
            throws SQLException {
        switch (effect) {
            case EffectStatModifier modifier:
                setStatModifierParameters(psEffect, modifier);
                break;

            case EffectHeal heal:
                setHealParameters(psEffect, heal);
                break;

            case EffectResetMalus malus:
                setResetMalusParameters(psEffect, malus);
                break;

            default:
                break;
        }
    }

    /**
     * Set the parameters of a prepared statement for an EffectStatModifier effect. This method is
     * used as part of the process to save the effects of an attack to the database during game
     * initialization. It takes a PreparedStatement and an EffectStatModifier object, and sets the
     * parameters of the PreparedStatement according to the properties of the EffectStatModifier
     * (e.g., target, stat, modifier, duration).
     * @param psEffect the PreparedStatement for which to set parameters
     * @param modifier the EffectStatModifier object for which to set parameters
     * @throws SQLException if an error occurs while setting the effect parameters
     */
    private void setStatModifierParameters(PreparedStatement psEffect, EffectStatModifier modifier)
            throws SQLException {
        psEffect.setString(2, modifier.getClass().getSimpleName());
        psEffect.setString(3, modifier.target().name());
        psEffect.setObject(4, modifier.stat() != null ? modifier.stat().name() : null,
                           Types.VARCHAR);
        psEffect.setInt(5, modifier.modifier());
        psEffect.setString(6, modifier.duration().toString());
        psEffect.setNull(7, Types.INTEGER);
    }

    /**
     * Set the parameters of a prepared statement for an EffectHeal effect. This method is used as
     * part of the process to save the effects of an attack to the database during game
     * initialization. It takes a PreparedStatement and an EffectHeal object, and sets the
     * parameters of the PreparedStatement according to the properties of the EffectHeal (e.g.,
     * target, amount).
     * @param psEffect the PreparedStatement for which to set parameters
     * @param heal the EffectHeal object for which to set parameters
     * @throws SQLException if an error occurs while setting the effect parameters
     */
    private void setHealParameters(PreparedStatement psEffect, EffectHeal heal)
            throws SQLException {
        psEffect.setString(2, heal.getClass().getSimpleName());
        psEffect.setString(3, heal.target().name());
        psEffect.setNull(4, Types.VARCHAR);
        psEffect.setNull(5, Types.INTEGER);
        psEffect.setNull(6, Types.VARCHAR);
        psEffect.setInt(7, heal.amount());
    }

    /**
     * Set the parameters of a prepared statement for an EffectResetMalus effect. This method is
     * used as part of the process to save the effects of an attack to the database during game
     * initialization. It takes a PreparedStatement and an EffectResetMalus object, and sets the
     * parameters of the PreparedStatement according to the properties of the EffectResetMalus
     * (e.g., target).
     * @param psEffect the PreparedStatement for which to set parameters
     * @param malus the EffectResetMalus object for which to set parameters
     * @throws SQLException if an error occurs while setting the effect parameters
     */
    private void setResetMalusParameters(PreparedStatement psEffect, EffectResetMalus malus)
            throws SQLException {
        psEffect.setString(2, malus.getClass().getSimpleName());
        psEffect.setString(3, malus.target().name());
        psEffect.setNull(4, Types.VARCHAR);
        psEffect.setNull(5, Types.INTEGER);
        psEffect.setNull(6, Types.VARCHAR);
        psEffect.setNull(7, Types.INTEGER);
    }

    /**
     * Save the default Bugemons to the database. This method is used during the game initialization
     * to load the static data of the game. It takes a list of Bugemon objects and saves them to the
     * database using a batch insert for efficiency. Each Bugemon's attacks are also saved as part
     * of this process, ensuring that all necessary data is present in the database.
     * @param bugemons a list of Bugemon objects representing the default Bugemons to be saved in
     *         the database. Each Bugemon should have its attacks already defined and linked by
     *         their IDs, as this method assumes that the attacks have been saved beforehand.
     */
    private void saveGameDataBugemon(List<Bugemon> bugemons) {
        try (PreparedStatement ps =
                     dbConnection.prepareStatement(this.dbRepository.getSql("SaveBugemon"))) {
            for (Bugemon bugemon : bugemons) {
                ps.setString(1, bugemon.getId());
                ps.setString(2, bugemon.getName());
                ps.setString(3, bugemon.getType().name());
                ps.setString(4, bugemon.getSpriteURL());
                ps.setInt(5, bugemon.getDefense());
                ps.setInt(6, bugemon.getAttack());
                ps.setInt(7, bugemon.getInitiative());
                ps.setInt(8, bugemon.getMaxHp());
                ps.setBoolean(9, bugemon.isStarter());
                // Assuming each Bugemon has exactly 3 attacks, we insert them in the order they
                // appear in the list
                ps.setString(10, bugemon.getListAttacksId().get(0));
                ps.setString(11, bugemon.getListAttacksId().get(1));
                ps.setString(12, bugemon.getListAttacksId().get(2));
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new IllegalStateException("saveGameDataBugemon failed", e);
        }
    }

    // ─── UTILS FOR CLASS USING THIS REPO ──

    /**
     * Retrieve all default Bugemons from the database. This is useful for the game initialization
     * to load the static data of the game.
     * @return a list of Bugemon objects representing all the default Bugemons stored in the
     *         database.
     */
    public List<Bugemon> getAllDefaultBugemons() {
        List<Bugemon> bugemons = new ArrayList<>();
        try (PreparedStatement ps = dbConnection.prepareStatement(
                     this.dbRepository.getSql("GetAllDefaultBugemons"))) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BugemonType type = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_TYPE,
                                                                BugemonType.class);
                Attack attack1 = getAttackById(rs.getString(DatabaseColumns.COL_ATTACK_ID_1));
                Attack attack2 = getAttackById(rs.getString(DatabaseColumns.COL_ATTACK_ID_2));
                Attack attack3 = getAttackById(rs.getString(DatabaseColumns.COL_ATTACK_ID_3));
                BugemonBuilder builder = new BugemonBuilder();
                builder.id(rs.getString(DatabaseColumns.COL_ID))
                        .name(rs.getString(DatabaseColumns.COL_NAME))
                        .type(type)
                        .sprite(rs.getString(DatabaseColumns.COL_SPRITE))
                        .defense(rs.getInt(DatabaseColumns.COL_BASE_DEFENSE))
                        .attack(rs.getInt(DatabaseColumns.COL_BASE_ATTACK_POWER))
                        .initiative(rs.getInt(DatabaseColumns.COL_BASE_INITIATIVE))
                        .hp(rs.getInt(DatabaseColumns.COL_BASE_MAX_HP))
                        .addAttack(attack1)
                        .addAttack(attack2)
                        .addAttack(attack3)
                        .isStarter(rs.getBoolean(DatabaseColumns.COL_IS_STARTER));

                bugemons.add(builder.build());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getAllDefaultBugemons failed", e);
        }
        return bugemons;
    }

    /**
     * Retrieve an attack by its ID from the database. This is useful for loading the details of an
     * attack, including its effects, which are also retrieved as part of this process.
     * @param attackId the ID of the attack to be retrieved from the database. This ID should
     *         correspond to an attack that has been previously saved in the database, either as
     *         part of the default game data or through other means.
     * @return an Attack object representing the attack with the specified ID, including its details
     *         and effects. If no attack is found with the given ID, an IllegalStateException is
     *         thrown.
     */
    public Attack getAttackById(String attackId) {
        try (PreparedStatement ps =
                     dbConnection.prepareStatement(this.dbRepository.getSql("GetAttackById"))) {
            ps.setString(1, attackId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                List<Effect> effects = getEffectByAttackId(attackId);
                BugemonType type = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_TYPE,
                                                                BugemonType.class);
                return new Attack(rs.getString(DatabaseColumns.COL_ID),
                                  rs.getString(DatabaseColumns.COL_NAME), type,
                                  rs.getString(DatabaseColumns.COL_DESCRIPTION),
                                  rs.getInt(DatabaseColumns.COL_POWER), effects);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getAttackById failed for id: " + attackId, e);
        }
        throw new IllegalStateException("Attack not found for id: " + attackId);
    }

    /**
     * Retrieve the effects of an attack by the attack's ID from the database. This is useful for
     * loading the details of an attack, including its effects, which are also retrieved as part of
     * this process.
     * @param attackId the ID of the attack whose effects are to be retrieved from the database.
     *         This ID should correspond to an attack that has been previously saved in the
     *         database, either as part of the default game data or through other means.
     * @return a list of Effect objects representing the effects of the specified attack.
     */
    public List<Effect> getEffectByAttackId(String attackId) {
        List<Effect> effects = new ArrayList<>();
        try (PreparedStatement ps = dbConnection.prepareStatement(
                     this.dbRepository.getSql("GetEffectByAttackId"))) {
            ps.setString(1, attackId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                switch (rs.getString(DatabaseColumns.COL_TYPE)) {
                    case "EffectStatModifier":
                        EffectTarget target = DatabaseHelper.getEnumOrNull(
                                rs, DatabaseColumns.COL_TARGET, EffectTarget.class);
                        EffectStat stat = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_STAT,
                                                                       EffectStat.class);
                        String duration = (rs.getString(DatabaseColumns.COL_DURATION) != null)
                                                  ? rs.getString(DatabaseColumns.COL_DURATION)
                                                  : "0_tour";
                        effects.add(new EffectStatModifier(target, stat,
                                                           rs.getInt(DatabaseColumns.COL_MODIFIER),
                                                           EffectDuration.fromLabel(duration)));
                        break;

                    case "EffectHeal":
                        target = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_TARGET,
                                                              EffectTarget.class);
                        effects.add(new EffectHeal(target, rs.getInt(DatabaseColumns.COL_AMOUNT)));
                        break;

                    case "EffectResetMalus":
                        target = DatabaseHelper.getEnumOrNull(rs, DatabaseColumns.COL_TARGET,
                                                              EffectTarget.class);
                        effects.add(new EffectResetMalus(target));
                        break;

                    default:
                        throw new IllegalStateException("Unknown effect type for attack id: "
                                                        + attackId);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("getEffectByAttackId failed for attack id: " + attackId,
                                            e);
        }
        return effects;
    }
}
