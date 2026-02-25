-- Orders and revenue per month
-- (monthly_orders_and_revenue.sql)

SELECT
  date_trunc('month', orderdatetime)::date AS month_start,
  COUNT(*) AS monthly_orders,
  SUM(costtotal) AS monthly_revenue
FROM orders
GROUP BY date_trunc('month', orderdatetime)::date
ORDER BY month_start;