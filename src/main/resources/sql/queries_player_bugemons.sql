-- Query
-- SavePlayerBugemon
INSERT INTO player_bugemons
  (player_id, bugemon_name, current_defense, current_attack,
   current_initiative, current_max_hp, current_xp, current_level)
VALUES (?, ?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (player_id, bugemon_name) DO NOTHING;

-- Query
-- UpdatePlayerBugemon
UPDATE player_bugemons
SET current_defense = ?, current_attack = ?,
    current_initiative = ?, current_max_hp = ?,
    current_xp = ?, current_level = ?
WHERE player_id = ? AND bugemon_name = ?;

-- Query
-- GetPlayerBugemons
SELECT * FROM player_bugemons WHERE player_id = ?;
