-- Lowest 10 inventory levels
-- (lowest_10_inventory.sql)

SELECT inventoryid, name, inventorynum
FROM inventory
ORDER BY inventorynum ASC
LIMIT 10;