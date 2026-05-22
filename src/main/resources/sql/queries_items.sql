-- Query
-- CreateItem
INSERT INTO items (item_id, name, description, category, sprite)
VALUES (?, ?, ?, ?, ?);

-- Query to retrieve the player inventory with their amount and item effect
-- GetPlayerInventory
SELECT
  i.item_id,
  i.name,
  i.description,
  i.category,
  i.sprite,
  ip.amount,
  ie.type AS effect_type,
  ie.target AS effect_target,
  ie.value AS effect_value,
  ie.stat AS effect_stat,
  ie.modifier AS effect_modifier,
  ie.duration AS effect_duration
FROM items i
INNER JOIN item_player ip ON i.item_id = ip.item_id
LEFT JOIN item_effects ie ON i.item_id = ie.item_id
WHERE ip.playername = ?
ORDER BY i.name;

-- Query
-- SaveItemEffect
INSERT INTO item_effects (item_id, type, target, value, stat, modifier, duration)
VALUES (?, ?, ?, ?, ?, ?, ?);

-- Query
-- SaveItemForPlayer
INSERT INTO item_player (playername, item_id, amount)
VALUES (?, ?, ?)
ON CONFLICT (playername, item_id) DO UPDATE SET amount = item_player.amount + EXCLUDED.amount;

-- Query
-- RemoveItemsOfPlayer
DELETE FROM item_player WHERE playername = ?;

-- Query
-- GetAllItems
SELECT
  i.item_id,
  i.name,
  i.description,
  i.category,
  i.sprite,
  ie.type AS effect_type,
  ie.target AS effect_target,
  ie.value AS effect_value,
  ie.stat AS effect_stat,
  ie.modifier AS effect_modifier,
  ie.duration AS effect_duration
FROM items i
LEFT JOIN item_effects ie ON i.item_id = ie.item_id;
