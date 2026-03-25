-- Query to create an user
-- CreateUser
INSERT INTO users (username) VALUES (?) RETURNING id;

-- Query
-- GetUserByUsername
SELECT id FROM users WHERE username = ?;