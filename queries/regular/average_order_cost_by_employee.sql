-- Average order cost by employee (descending order)
-- (average_order_cost_by_employee.sql)

SELECT
  employeeid,
  AVG(costtotal) AS avg_order_cost
FROM orders
GROUP BY employeeid
ORDER BY avg_order_cost DESC;