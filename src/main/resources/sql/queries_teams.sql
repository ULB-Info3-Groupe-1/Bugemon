-- Query
-- CreateTeam
INSERT INTO teams (player_id, name)
VALUES (?, ?)
ON CONFLICT (player_id, name) DO NOTHING;

-- Query
-- DeleteTeamMembers
DELETE FROM team_members WHERE player_id = ? AND team_name = ?;

-- Query
-- DeleteTeam
DELETE FROM teams WHERE player_id = ? AND name = ?;

-- Query
-- GetPlayerTeams
SELECT * FROM teams WHERE player_id = ?;

-- Query
-- AddTeamMember
INSERT INTO team_members (player_id, team_name, bugemon_name, slot_position)
VALUES (?, ?, ?, ?)
ON CONFLICT (player_id, team_name, slot_position) DO NOTHING;

-- Query
-- RemoveTeamMember
DELETE FROM team_members
WHERE player_id = ? AND team_name = ? AND bugemon_name = ?;

-- Query
-- GetTeamMembers
SELECT * FROM team_members WHERE player_id = ? AND team_name = ?;

-- Query
-- RenameTeam
UPDATE teams SET name = ? WHERE player_id = ? AND name = ?;

-- Query
-- RenameTeamMembers
UPDATE team_members SET team_name = ? WHERE player_id = ? AND team_name = ?;

-- Query
-- RemoveTeamComposition
DELETE FROM team_members 
WHERE player_id = ? AND team_name = ?;
