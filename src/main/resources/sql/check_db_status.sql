-- Query to check if the data is present in the tables
-- IsDataEmpty
SELECT 
    (SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name NOT LIKE 'sqlite_%') as total_tables,
    (
      (SELECT COUNT(*) FROM bugemons) + 
      (SELECT COUNT(*) FROM attacks) + 
      (SELECT COUNT(*) FROM effects)
    ) as total_rows;


-- Query to check if the critical tables are present in the database
-- isTablesPresent
SELECT 
    (SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name NOT LIKE 'sqlite_%') as total_tables,
    (SELECT COUNT(*) 
     FROM sqlite_master 
     WHERE type = 'table' 
     AND name IN ('attacks', 'effects', 'bugemons', 'users', 'user_bugemons', 'teams', 'team_members')
    ) as existing_critical_tables;
