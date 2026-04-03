-- Query
-- SaveUserBugemon
INSERT INTO user_bugemons
  (user_id, bugemon_id, current_defense, current_attack,
   current_initiative, current_max_hp, current_xp, current_level)
VALUES (?, ?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (user_id, bugemon_id) DO NOTHING;

-- Query
-- UpdateUserBugemon
UPDATE user_bugemons
SET current_defense = ?, current_attack = ?,
    current_initiative = ?, current_max_hp = ?,
    current_xp = ?, current_level = ?
WHERE user_id = ? AND bugemon_id = ?;

-- Query
-- GetUserBugemons
SELECT * FROM user_bugemons WHERE user_id = ?;
