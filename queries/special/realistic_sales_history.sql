-- Realistic sales history
-- (realistic_sales_history.sql)

SELECT
  EXTRACT(HOUR FROM orderdatetime)::int AS hour_of_day,
  COUNT(*) AS orders_count,
  SUM(costTotal) AS total_sales
FROM orders
GROUP BY EXTRACT(HOUR FROM orderdatetime)::int
ORDER BY hour_of_day;