-- Average order cost overall
-- (average_order_cost.sql)

SELECT AVG(costtotal) AS avg_order_cost
FROM orders;