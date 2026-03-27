-- Query to add item
-- AddItem
INSERT INTO items (item_id, name, description, category, sprite) VALUES (?, ?, ?, ?, ?);

INSERT INTO item_effect(item_id, type, target, value, stat, modifier, duration) VALUES (?, ?, ?, ?, ?, ?, ?);