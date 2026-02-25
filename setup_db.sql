DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS menu;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS menu_items;

CREATE TABLE inventory (
    inventoryID INT PRIMARY KEY, 
    name VARCHAR(100), 
    cost DECIMAL(10,2), 
    inventoryNum INT, 
    useAverage INT
);

CREATE TABLE menu (
    menuID INT PRIMARY KEY, 
    name VARCHAR(100), 
    cost DECIMAL(10,2), 
    salesNum INT
);

CREATE TABLE employees (
    employeeID INT PRIMARY KEY, 
    name VARCHAR(100), 
    pay DECIMAL(10,2), 
    job VARCHAR(50), 
    orderNum INT
);

CREATE TABLE orders (
    orderID INT PRIMARY KEY, 
    customerName VARCHAR(100), 
    costTotal DECIMAL(10,2), 
    employeeID INT, 
    orderDateTime TIMESTAMP
);

CREATE TABLE order_items (
    ID INT PRIMARY KEY,
    menuID INT,
    orderID INT,
    quantity INT,
    iceLevel VARCHAR(20),
    sugarLevel VARCHAR(20),
    topping VARCHAR(50),
    cost DECIMAL(10,2)
);

CREATE TABLE menu_items (
    ID INT PRIMARY KEY,
    inventoryID INT,
    menuID INT,
    itemQuantity INT
);