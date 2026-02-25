-- Average number of items per order
-- (average_items_per_order.sql)

SELECT
  AVG(items_in_order) AS avg_menu_items_per_order
FROM (
  SELECT
    oi.orderid,
    SUM(oi.quantity) AS items_in_order
  FROM order_items oi
  GROUP BY oi.orderid
) t;