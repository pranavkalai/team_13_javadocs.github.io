-- Most popular menu items by total quantity ordered
-- (popular_menu_items.sql)

SELECT
  m.name AS menu_item,
  SUM(oi.quantity) AS total_quantity
FROM order_items oi
JOIN menu m
  ON oi.menuID = m.menuID
GROUP BY m.name
ORDER BY total_quantity DESC;