-- Orders processed per employee
-- (orders_per_employee.sql)

SELECT
  e.employeeid,
  e.name,
  COUNT(o.orderid) AS orders_processed
FROM employees e
LEFT JOIN orders o
  ON o.employeeid = e.employeeid
GROUP BY e.employeeid, e.name
ORDER BY orders_processed DESC;