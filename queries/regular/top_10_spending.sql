-- Top 10 customers by total spending
-- (top_10_spending.sql)

SELECT customername, SUM(costtotal) AS total_spent
FROM orders
GROUP BY customername
ORDER BY total_spent DESC
LIMIT 10;