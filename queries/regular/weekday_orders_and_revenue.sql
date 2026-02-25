-- Revenue and orders by weekday
-- (weekday_orders_and_revenue.sql)

SELECT
  EXTRACT(DOW FROM orderdatetime) AS weekday_num,
  TO_CHAR(orderdatetime, 'Day') AS weekday_name,
  SUM(costtotal) AS total_revenue,
  COUNT(*) AS total_orders
FROM orders
GROUP BY weekday_num, weekday_name
ORDER BY weekday_num;