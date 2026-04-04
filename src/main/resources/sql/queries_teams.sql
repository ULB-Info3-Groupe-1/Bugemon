-- Query
-- CreateTeam
INSERT INTO teams (user_id, name)
VALUES (?, ?)
ON CONFLICT (user_id, name) DO NOTHING;

-- Query
-- DeleteTeamMembers
DELETE FROM team_members WHERE user_id = ? AND team_name = ?;

-- Query
-- DeleteTeam
DELETE FROM teams WHERE user_id = ? AND name = ?;

-- Query
-- GetUserTeams
SELECT * FROM teams WHERE user_id = ?;

-- Query
-- AddTeamMember
INSERT INTO team_members (user_id, team_name, bugemon_name, slot_position)
VALUES (?, ?, ?, ?)
ON CONFLICT (user_id, team_name, slot_position) DO NOTHING;

-- Query
-- RemoveTeamMember
DELETE FROM team_members
WHERE user_id = ? AND team_name = ? AND bugemon_name = ?;

-- Query
-- GetTeamMembers
SELECT * FROM team_members WHERE user_id = ? AND team_name = ?;

-- Query
-- RenameTeam
UPDATE teams SET name = ? WHERE user_id = ? AND name = ?;

-- Query
-- RenameTeamMembers
UPDATE team_members SET team_name = ? WHERE user_id = ? AND team_name = ?;
