-- Query to retrieve an attack by its ID
-- GetAttackById
SELECT * FROM attacks WHERE id = ?;

-- Query to retrieve all attacks with their effects in a single query
-- GetAllAttacksWithEffects
SELECT
    a.id as attack_id,
    a.name as attack_name,
    a.type as attack_type,
    a.description as attack_description,
    a.power as attack_power,
    e.id as effect_id,
    e.type as effect_type,
    e.target as effect_target,
    e.stat as effect_stat,
    e.modifier as effect_modifier,
    e.duration as effect_duration,
    e.amount as effect_amount
FROM attacks a
LEFT JOIN attack_effects e ON a.id = e.attack_id
ORDER BY a.id, e.id;