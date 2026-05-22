-- Query
-- MigratePlayerBugemonsAttackColumns
ALTER TABLE "player_bugemons" ADD COLUMN IF NOT EXISTS "attack_1_id" varchar;
ALTER TABLE "player_bugemons" ADD COLUMN IF NOT EXISTS "attack_2_id" varchar;
ALTER TABLE "player_bugemons" ADD COLUMN IF NOT EXISTS "attack_3_id" varchar;

-- Query
-- SavePlayerBugemon
INSERT INTO player_bugemons
  (playername, bugemon_name, current_defense, current_attack,
   current_initiative, current_max_hp, current_xp, current_level,
   attack_1_id, attack_2_id, attack_3_id
  )
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (playername, bugemon_name) DO NOTHING;

-- Query
-- UpdatePlayerBugemon
UPDATE player_bugemons
SET current_defense = ?, current_attack = ?,
    current_initiative = ?, current_max_hp = ?,
    current_xp = ?, current_level = ?,
    attack_1_id = ?, attack_2_id = ?,
    attack_3_id = ?
WHERE playername = ? AND bugemon_name = ?;

-- Query
-- GetPlayerBugemons
SELECT * FROM player_bugemons WHERE playername = ?;

-- Query
-- GetPlayerBugemonByName
SELECT * FROM player_bugemons WHERE playername = ? AND bugemon_name = ?;

-- Query
-- RemoveAllPlayerBugemons
DELETE FROM player_bugemons WHERE playername = ?;

-- Query
-- DeletePlayerBugemon
DELETE FROM player_bugemons WHERE playername = ? AND bugemon_name = ?;
