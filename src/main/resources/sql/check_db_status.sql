-- Query
-- IsDataEmpty
SELECT 
    (SELECT COUNT(*) FROM pg_tables WHERE schemaname = 'public') as total_tables,
    (
      (SELECT COUNT(*) FROM bugemons) + 
      (SELECT COUNT(*) FROM attacks) + 
      (SELECT COUNT(*) FROM attack_effects)
    ) as total_rows;

-- Query
-- areTablesPresent
SELECT COUNT(*) as existing_critical_tables
FROM pg_tables
WHERE schemaname = 'public'
AND tablename IN ('bugemons', 'attacks', 'attack_effects', 'players', 'player_bugemons', 'teams', 'team_members', 'items', 'item_effects', 'item_player');
