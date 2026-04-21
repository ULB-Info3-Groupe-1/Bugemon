-- Query
-- CreateTeam
INSERT INTO teams (playername, name)
VALUES (?, ?)
ON CONFLICT (playername, name) DO NOTHING;

-- Query
-- DeleteTeamMembers
DELETE FROM team_members WHERE playername = ? AND team_name = ?;

-- Query
-- DeleteTeam
DELETE FROM teams WHERE playername = ? AND name = ?;

-- Query
-- GetPlayerTeams
SELECT * FROM teams WHERE playername = ?;

-- Query
-- AddTeamMember
INSERT INTO team_members (playername, team_name, bugemon_name, slot_position)
VALUES (?, ?, ?, ?)
ON CONFLICT (playername, team_name, slot_position) DO NOTHING;

-- Query
-- RemoveTeamMember
DELETE FROM team_members
WHERE playername = ? AND team_name = ? AND bugemon_name = ?;

-- Query
-- GetTeamMembers
SELECT * FROM team_members WHERE playername = ? AND team_name = ? ORDER BY slot_position;

-- Query
-- RenameTeam
UPDATE teams SET name = ? WHERE playername = ? AND name = ?;

-- Query
-- RenameTeamMembers
UPDATE team_members SET team_name = ? WHERE playername = ? AND team_name = ?;

-- Query
-- RemoveTeamComposition
DELETE FROM team_members 
WHERE playername = ? AND team_name = ?;

-- Query
-- TeamNameAlreadyExists
SELECT * FROM teams WHERE playername = ? AND name = ?;
