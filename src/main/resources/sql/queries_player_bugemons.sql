-- Query
-- SavePlayerBugemon
INSERT INTO player_bugemons
  (playername, bugemon_name, current_defense, current_attack,
   current_initiative, current_max_hp, current_xp, current_level)
VALUES (?, ?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (playername, bugemon_name) DO NOTHING;

-- Query
-- UpdatePlayerBugemon
UPDATE player_bugemons
SET current_defense = ?, current_attack = ?,
    current_initiative = ?, current_max_hp = ?,
    current_xp = ?, current_level = ?
WHERE playername = ? AND bugemon_name = ?;

-- Query
-- GetPlayerBugemons
SELECT * FROM player_bugemons WHERE playername = ?;

-- Query
-- RemoveAllPlayerBugemons
DELETE FROM player_bugemons WHERE playername = ?;   
