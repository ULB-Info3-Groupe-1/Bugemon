-- Query to create the database schema
-- CreateSchema

CREATE TABLE IF NOT EXISTS "attacks" (
  "id" TEXT PRIMARY KEY,
  "name" TEXT,
  "type" TEXT,
  "description" TEXT,
  "power" INTEGER
);

CREATE TABLE IF NOT EXISTS "effects" (
  "id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "attack_id" TEXT REFERENCES "attacks"("id") ON DELETE CASCADE,
  "type" TEXT,
  "target" TEXT,
  "stat" TEXT,
  "modifier" INTEGER,
  "duration" TEXT
);

CREATE TABLE IF NOT EXISTS "bugemons" (
  "id" TEXT PRIMARY KEY,
  "name" TEXT,
  "type" TEXT,
  "sprite" TEXT,
  "base_defense" INTEGER,
  "base_attack_power" INTEGER,
  "base_initiative" INTEGER,
  "base_max_hp" INTEGER,
  "is_starter" INTEGER,
  "attack_1_id" TEXT REFERENCES "attacks"("id"),
  "attack_2_id" TEXT REFERENCES "attacks"("id"),
  "attack_3_id" TEXT REFERENCES "attacks"("id")
);

CREATE TABLE IF NOT EXISTS "users" (
  "id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "username" TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS "user_bugemons" (
  "user_id" INTEGER,
  "bugemon_id" TEXT,
  "current_defense" INTEGER,
  "current_attack_power" INTEGER,
  "current_initiative" INTEGER,
  "current_max_hp" INTEGER,
  "current_xp" INTEGER DEFAULT 0,
  "current_level" INTEGER DEFAULT 1,
  PRIMARY KEY ("user_id", "bugemon_id"),
  FOREIGN KEY ("user_id") REFERENCES "users"("id"),
  FOREIGN KEY ("bugemon_id") REFERENCES "bugemons"("id")
);

CREATE TABLE IF NOT EXISTS "teams" (
  "user_id" INTEGER,
  "name" TEXT,
  PRIMARY KEY ("user_id", "name"),
  FOREIGN KEY ("user_id") REFERENCES "users"("id")
);

CREATE TABLE IF NOT EXISTS "team_members" (
  "user_id" INTEGER,
  "team_name" TEXT,
  "bugemon_id" TEXT,
  "slot_position" INTEGER,
  PRIMARY KEY ("user_id", "team_name", "slot_position"),
  FOREIGN KEY ("user_id", "team_name") REFERENCES "teams"("user_id", "name"),
  FOREIGN KEY ("user_id", "bugemon_id") REFERENCES "user_bugemons"("user_id", "bugemon_id")
);

CREATE UNIQUE INDEX IF NOT EXISTS "idx_team_members_unique" ON "team_members" ("user_id", "team_name", "bugemon_id");
