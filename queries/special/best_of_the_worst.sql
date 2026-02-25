-- Best of the worst
-- (best_of_the_worst.sql)

WITH daily_sales AS (
  SELECT
    date_trunc('week', o.orderdatetime)::date AS week_start,
    o.orderdatetime::date AS day,
    SUM(o.costtotal) AS day_sales
  FROM orders o
  GROUP BY date_trunc('week', o.orderdatetime)::date, o.orderdatetime::date
),
worst_day AS (
  SELECT
    ds.week_start,
    ds.day,
    ds.day_sales
  FROM daily_sales ds
  JOIN (
    SELECT week_start, MIN(day_sales) AS min_sales
    FROM daily_sales
    GROUP BY week_start
  ) mins
    ON mins.week_start = ds.week_start
   AND mins.min_sales  = ds.day_sales
),
top_item AS (
  SELECT
    wd.week_start,
    wd.day,
    wd.day_sales,
    oi.menuid,
    SUM(oi.quantity) AS units_sold
  FROM worst_day wd
  JOIN orders o
    ON o.orderdatetime::date = wd.day
  JOIN order_items oi
    ON oi.orderid = o.orderid
  GROUP BY wd.week_start, wd.day, wd.day_sales, oi.menuid
),
ranked AS (
  SELECT
    t.*,
    ROW_NUMBER() OVER (PARTITION BY t.week_start ORDER BY t.units_sold DESC, t.menuid) AS rn
  FROM top_item t
)
SELECT
  r.week_start,
  r.day AS worst_day,
  r.day_sales AS worst_day_sales,
  m.name AS top_seller,
  r.units_sold
FROM ranked r
JOIN menu m
  ON m.menuid = r.menuid
WHERE r.rn = 1
ORDER BY r.week_start;
