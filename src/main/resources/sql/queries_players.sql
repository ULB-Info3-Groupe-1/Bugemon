-- Query to create a player
-- CreatePlayer
INSERT INTO players (playername) VALUES (?)
ON CONFLICT (playername) DO NOTHING;

-- Query
-- GetPlayerByPlayername
SELECT id FROM players WHERE playername = ?;
