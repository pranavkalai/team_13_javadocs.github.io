-- Menu item inventory
-- (menu_item_inventory.sql)

SELECT
  m.menuid,
  m.name,
  COUNT(DISTINCT mi.inventoryid) AS ingredients_used
FROM menu m
JOIN menu_items mi
  ON mi.menuid = m.menuid
GROUP BY m.menuid, m.name
ORDER BY m.menuid;