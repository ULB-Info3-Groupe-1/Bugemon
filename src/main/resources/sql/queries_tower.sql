-- Query
-- UpsertTowerRun
INSERT INTO tower_runs (playername, seed, team_name, current_floor, current_row, current_col)
VALUES (?, ?, ?, ?, ?, ?)
ON CONFLICT (playername) DO UPDATE
  SET seed = EXCLUDED.seed,
      team_name = EXCLUDED.team_name,
      current_floor = EXCLUDED.current_floor,
      current_row = EXCLUDED.current_row,
      current_col = EXCLUDED.current_col;

-- Query
-- DeleteTowerVisitedRooms
DELETE FROM tower_visited_rooms WHERE playername = ?;

-- Query
-- InsertTowerVisitedRoom
INSERT INTO tower_visited_rooms (playername, row, col) VALUES (?, ?, ?);

-- Query
-- UpsertTowerMemberHp
INSERT INTO tower_run_team_hp (playername, bugemon_name, slot_position, current_hp)
VALUES (?, ?, ?, ?)
ON CONFLICT (playername, slot_position) DO UPDATE
  SET bugemon_name = EXCLUDED.bugemon_name,
      current_hp = EXCLUDED.current_hp;

-- Query
-- GetTowerRun
SELECT seed, team_name, current_floor, current_row, current_col FROM tower_runs WHERE playername = ?;

-- Query
-- GetTowerVisitedRooms
SELECT row, col FROM tower_visited_rooms WHERE playername = ?;

-- Query
-- GetTowerTeamHp
SELECT bugemon_name, slot_position, current_hp FROM tower_run_team_hp
WHERE playername = ? ORDER BY slot_position;

-- Query
-- DeleteTowerRun
DELETE FROM tower_runs WHERE playername = ?;
