-- Query
-- IsDataEmpty
SELECT 
    (SELECT COUNT(*) FROM pg_tables WHERE schemaname = 'public') as total_tables,
    (
      (SELECT COUNT(*) FROM bugemons) + 
      (SELECT COUNT(*) FROM attacks) + 
      (SELECT COUNT(*) FROM effects)
    ) as total_rows;

-- Query
-- isTablesPresent
SELECT 
    (SELECT COUNT(*) FROM pg_tables WHERE schemaname = 'public') as total_tables,
    (SELECT COUNT(*) 
     FROM information_schema.tables 
     WHERE table_schema = 'public' 
     AND table_name IN ('attacks', 'effects', 'bugemons', 'users', 'user_bugemons', 'teams', 'team_members')
    ) as existing_critical_tables;