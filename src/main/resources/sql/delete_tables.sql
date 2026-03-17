-- Query
-- ClearDatabase
TRUNCATE TABLE 
    team_members, 
    teams, 
    user_bugemons, 
    users, 
    bugemons, 
    attacks, 
    effects 
RESTART IDENTITY CASCADE;