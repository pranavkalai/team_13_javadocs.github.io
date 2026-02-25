-- Ingredients used for Taro Milk Tea (ingredient + amount)
-- (taro_milk_tea_ingredients.sql)

SELECT
  m.name AS drink,
  i.name AS ingredient,
  mi."itemquantity" AS amount
FROM menu m
JOIN menu_items mi
  ON m."menuid" = mi."menuid"
JOIN inventory i
  ON mi."inventoryid" = i."inventoryid"
WHERE m.name = 'Taro Milk Tea';