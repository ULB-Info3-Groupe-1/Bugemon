-- Query to create a player
-- CreatePlayer
INSERT INTO players (playername) VALUES (?)
ON CONFLICT (playername) DO NOTHING;

-- Query
-- GetPlayerByPlayername
SELECT id FROM players WHERE playername = ?;

-- Query
-- SetPlayerCurrentTeam
UPDATE players 
SET current_team = ? 
WHERE playername = ?;

-- Query
-- UnsetPlayerCurrentTeam
UPDATE players 
SET current_team = NULL 
WHERE playername = ?;

-- Query
-- GetPlayerCurrentTeamName
SELECT current_team FROM players WHERE playername = ?;

-- Query
-- GetPlayerCurrentTeam
SELECT 
    p.current_team AS team_name,
    tm.slot_position,
    pb.bugemon_name,
    pb.current_level,
    pb.current_xp,
    pb.current_max_hp,
    pb.current_attack,
    pb.current_defense,
    pb.current_initiative,
    b.type,
    b.sprite
FROM players p
JOIN team_members tm ON p.playername = tm.playername AND p.current_team = tm.team_name
JOIN player_bugemons pb ON tm.playername = pb.playername AND tm.bugemon_name = pb.bugemon_name
JOIN bugemons b ON pb.bugemon_name = b.name
WHERE p.playername = ?
ORDER BY tm.slot_position ASC;
