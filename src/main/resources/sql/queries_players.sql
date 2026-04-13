-- Query to create a player
-- CreatePlayer
INSERT INTO players (playername) VALUES (?) RETURNING id;

-- Query
-- GetPlayerByPlayername
SELECT id FROM players WHERE playername = ?;
