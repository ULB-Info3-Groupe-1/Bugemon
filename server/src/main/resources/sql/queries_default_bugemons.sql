-- Query
-- GetAllDefaultBugemons
SELECT * FROM bugemons

-- Query
-- GetBugemonByName
SELECT * FROM bugemons WHERE name = ?

-- Query
-- GetDefaultBugemon
SELECT type, sprite, is_starter, attack_1_id, attack_2_id, attack_3_id FROM bugemons WHERE name = ?
