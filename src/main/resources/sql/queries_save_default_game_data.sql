-- Query
-- SaveBugemon
INSERT INTO bugemons (name, type, sprite, base_defense, base_attack, base_initiative, base_max_hp, is_starter, attack_1_id, attack_2_id, attack_3_id)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)

-- Query
-- SaveAttack
INSERT INTO attacks (id, name, type, description, power)
VALUES (?, ?, ?, ?, ?)
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, power = EXCLUDED.power;

-- Query
-- SaveEffect
INSERT INTO effects (attack_id, type, target, stat, modifier, duration, amount)
VALUES (?, ?, ?, ?, ?, ?, ?)
ON CONFLICT DO NOTHING;
