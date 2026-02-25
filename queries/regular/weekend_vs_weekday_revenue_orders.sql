-- Weekend vs weekday revenue and order counts
-- (weekend_vs_weekday_revenue_orders.sql)

SELECT
  CASE
    WHEN EXTRACT(DOW FROM orderdatetime) IN (0,6) THEN 'Weekend'
    ELSE 'Weekday'
  END AS day_type,
  SUM(costtotal) AS total_revenue,
  COUNT(*) AS total_orders
FROM orders
GROUP BY day_type;