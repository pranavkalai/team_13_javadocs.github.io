-- Weekly sales history (orders per week)
-- (weekly_sales_history.sql)

SELECT
  date_trunc('week', orderdatetime)::date AS week_start,
  COUNT(*) AS orders_count
FROM orders
GROUP BY date_trunc('week', orderdatetime)::date
ORDER BY week_start;