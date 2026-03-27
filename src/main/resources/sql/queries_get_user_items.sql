-- Query to get user inventory
-- GetInventory
SELECT item_id, amount FROM item_user WHERE user_id = ?;

-- Query to add to inventory
-- AddItem
INSERT INTO item_user (item_id, user_id, amount) VALUES (?, ?, ?);

-- Query to increase amount
-- Increase
UPDATE item_user SET amount = amount + ? WHERE  user_id = ? AND item_id = ?;