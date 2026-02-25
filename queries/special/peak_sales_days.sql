-- Peak sales days
-- (peak_sales_days.sql)

SELECT
  date_trunc('day', orderdatetime)::date AS day,
  SUM(costTotal) AS total_sales
FROM orders
GROUP BY date_trunc('day', orderdatetime)::date
ORDER BY total_sales DESC
LIMIT 10;