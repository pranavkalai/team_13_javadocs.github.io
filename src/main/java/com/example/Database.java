
package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Handles all database operations for the Boba Shop POS system.
 * This includes managing products, inventory, employees, and orders.
 */
public class Database {
    /** The Dotenv instance for loading environment variables. */
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    /** The database connection URL. */
    private static final String URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_13_db";
    /** The database username. */
    private static final String USER = dotenv.get("DB_USER");
    /** The database password. */
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    /**
     * Represents a row in the weekly orders report.
     */
    public static class WeeklyOrdersRow {
        /** The starting date of the week. */
        private final LocalDate weekStart;
        /** The number of orders in that week. */
        private final int ordersCount;

        /**
         * Constructs a WeeklyOrdersRow.
         *
         * @param weekStart   The starting date of the week.
         * @param ordersCount The number of orders in that week.
         */
        public WeeklyOrdersRow(LocalDate weekStart, int ordersCount) {
            this.weekStart = weekStart;
            this.ordersCount = ordersCount;
        }

        /**
         * Gets the week start date.
         *
         * @return The week start date.
         */
        public LocalDate getWeekStart() {
            return weekStart;
        }

        /**
         * Gets the order count for the week.
         *
         * @return The order count.
         */
        public int getOrdersCount() {
            return ordersCount;
        }
    }

    /**
     * Represents a row in the popular items report.
     */
    public static class PopularItemRow {
        /** The name of the menu item. */
        private final String menuItem;
        /** The total quantity sold. */
        private final int totalQuantity;

        /**
         * Constructs a PopularItemRow.
         *
         * @param menuItem      The name of the menu item.
         * @param totalQuantity The total quantity sold.
         */
        public PopularItemRow(String menuItem, int totalQuantity) {
            this.menuItem = menuItem;
            this.totalQuantity = totalQuantity;
        }

        /**
         * Gets the menu item name.
         *
         * @return The menu item name.
         */
        public String getMenuItem() {
            return menuItem;
        }

        /**
         * Gets the total quantity sold.
         *
         * @return The total quantity.
         */
        public int getTotalQuantity() {
            return totalQuantity;
        }
    }

    /**
     * Represents a row in the stock level report.
     */
    public static class StockLevelRow {
        /** The name of the inventory item. */
        private final String name;
        /** The current stock level. */
        private final int stock;

        /**
         * Constructs a StockLevelRow.
         *
         * @param name  The name of the inventory item.
         * @param stock The current stock level.
         */
        public StockLevelRow(String name, int stock) {
            this.name = name;
            this.stock = stock;
        }

        /**
         * Gets the name of the inventory item.
         *
         * @return The inventory item name.
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the current stock level.
         *
         * @return The stock level.
         */
        public int getStock() {
            return stock;
        }
    }

    /**
     * Represents a row in the inventory usage report.
     */
    public static class InventoryUsageRow {
        /** The name of the inventory item. */
        private final String inventoryItem;
        /** The number of units used. */
        private final int unitsUsed;

        /**
         * Constructs an InventoryUsageRow.
         *
         * @param inventoryItem The name of the inventory item.
         * @param unitsUsed     The number of units used.
         */
        public InventoryUsageRow(String inventoryItem, int unitsUsed) {
            this.inventoryItem = inventoryItem;
            this.unitsUsed = unitsUsed;
        }

        /**
         * Gets the name of the inventory item.
         *
         * @return The inventory item name.
         */
        public String getInventoryItem() { return inventoryItem; }

        /**
         * Gets the number of units used.
         *
         * @return The units used.
         */
        public int getUnitsUsed() { return unitsUsed; }
    }

    /**
     * Represents a row in the sales report.
     */
    public static class SalesReportRow {
        /** The name of the menu item. */
        private final String menuItem;
        /** The total quantity sold. */
        private final int totalQuantity;
        /** The total revenue generated. */
        private final double totalRevenue;

        /**
         * Constructs a SalesReportRow.
         *
         * @param menuItem      The name of the menu item.
         * @param totalQuantity The total quantity sold.
         * @param totalRevenue  The total revenue generated.
         */
        public SalesReportRow(String menuItem, int totalQuantity, double totalRevenue) {
            this.menuItem = menuItem;
            this.totalQuantity = totalQuantity;
            this.totalRevenue = totalRevenue;
        }

        /**
         * Gets the menu item name.
         *
         * @return The menu item name.
         */
        public String getMenuItem() { return menuItem; }

        /**
         * Gets the total quantity sold.
         *
         * @return The total quantity.
         */
        public int getTotalQuantity() { return totalQuantity; }

        /**
         * Gets the total revenue generated.
         *
         * @return The total revenue.
         */
        public double getTotalRevenue() { return totalRevenue; }
    }

    /**
     * Represents a row in the Z-Report (employee orders summary).
     */
    public static class ZReportRow {
        /** The name of the employee. */
        private final String employeeName;
        /** The number of orders processed by the employee today. */
        private final int dailyOrders;

        /**
         * Constructs a ZReportRow.
         *
         * @param employeeName The name of the employee.
         * @param dailyOrders  The number of orders processed by the employee today.
         */
        public ZReportRow(String employeeName, int dailyOrders) {
            this.employeeName = employeeName;
            this.dailyOrders = dailyOrders;
        }

        /**
         * Gets the employee name.
         *
         * @return The employee name.
         */
        public String getEmployeeName() { return employeeName; }

        /**
         * Gets the number of daily orders.
         *
         * @return The daily orders.
         */
        public int getDailyOrders() { return dailyOrders; }
    }

    /**
     * Represents the data for a full Z-Report.
     */
    public static class ZReportData {
        /** The total sales for the day. */
        private final double totalSales;
        /** A list of order summaries per employee. */
        private final List<ZReportRow> employeeOrders;

        /**
         * Constructs a ZReportData object.
         *
         * @param totalSales     The total sales for the day.
         * @param employeeOrders A list of order summaries per employee.
         */
        public ZReportData(double totalSales, List<ZReportRow> employeeOrders) {
            this.totalSales = totalSales;
            this.employeeOrders = employeeOrders;
        }

        /**
         * Gets the total sales for the day.
         *
         * @return The total sales.
         */
        public double getTotalSales() { return totalSales; }

        /**
         * Gets the list of employee order summaries.
         *
         * @return The employee orders.
         */
        public List<ZReportRow> getEmployeeOrders() { return employeeOrders; }
    }

    /**
     * Represents an ingredient for a menu item.
     */
    public static class MenuIngredientRow {
        /** The name of the ingredient. */
        private final String ingredientName;
        /** The quantity required for the menu item. */
        private final int quantity;

        /**
         * Constructs a MenuIngredientRow.
         *
         * @param ingredientName The name of the ingredient.
         * @param quantity       The quantity required for the menu item.
         */
        public MenuIngredientRow(String ingredientName, int quantity) {
            this.ingredientName = ingredientName;
            this.quantity = quantity;
        }

        /**
         * Gets the ingredient name.
         *
         * @return The ingredient name.
         */
        public String getIngredientName() {
            return ingredientName;
        }

        /**
         * Gets the quantity required.
         *
         * @return The quantity.
         */
        public int getQuantity() {
            return quantity;
        }
    }

    /**
     * Retrieves a sales report for a specific date range.
     *
     * @param startDateInclusive The start date of the report (inclusive).
     * @param endDateInclusive   The end date of the report (inclusive).
     * @return A list of SalesReportRow objects.
     */
    public static List<SalesReportRow> getSalesReport(LocalDate startDateInclusive, LocalDate endDateInclusive) {
        List<SalesReportRow> rows = new ArrayList<>();

        LocalDateTime start = startDateInclusive.atStartOfDay();
        LocalDateTime endExclusive = endDateInclusive.plusDays(1).atStartOfDay();

        String sql = """
            SELECT
              m.name AS menu_item,
              SUM(oi.quantity)::int AS total_quantity,
              SUM(oi.cost)::double precision AS total_revenue
            FROM orders o
            JOIN order_items oi ON o.orderID = oi.orderID
            JOIN menu m ON oi.menuID = m.menuID
            WHERE o.orderDateTime >= ?
              AND o.orderDateTime < ?
            GROUP BY m.name
            ORDER BY total_revenue DESC;
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(endExclusive));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new SalesReportRow(
                            rs.getString("menu_item"),
                            rs.getInt("total_quantity"),
                            rs.getDouble("total_revenue")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }

    /**
     * Establishes a connection to the PostgreSQL database.
     *
     * @return A Connection object.
     * @throws SQLException If database credentials are not set or connection fails.
     */
    public static Connection getConnection() throws SQLException {
        if (USER == null || PASSWORD == null) {
            throw new SQLException("Database credentials not set in environment variables (DB_USER, DB_PASSWORD)");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Retrieves all products from the menu table.
     *
     * @return A list of Product objects.
     */
    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM menu ORDER BY name";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("menuID"),
                        rs.getString("name"),
                        rs.getDouble("cost"),
                        rs.getInt("salesNum")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Retrieves ingredients for a specific menu item.
     *
     * @param menuID The ID of the menu item.
     * @return A list of MenuIngredientRow objects.
     */
    public static List<MenuIngredientRow> getMenuItemIngredients(int menuID) {
        List<MenuIngredientRow> ingredients = new ArrayList<>();
        String sql = """
            SELECT i.name, mi.itemQuantity
            FROM menu_items mi
            JOIN inventory i ON i.inventoryID = mi.inventoryID
            WHERE mi.menuID = ?
            ORDER BY i.name;
            """;
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, menuID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(new MenuIngredientRow(
                            rs.getString("name"),
                            rs.getInt("itemQuantity")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    /**
     * Adds a new menu item to the database.
     *
     * @param name The name of the menu item.
     * @param cost The cost of the menu item.
     * @return true if successful, false otherwise.
     */
    public static boolean addMenuItem(String name, double cost) {
        String sql = "INSERT INTO menu (menuID, name, cost, salesNum) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            int menuID = getNextID(conn, "menu", "menuID");
            ps.setInt(1, menuID);
            ps.setString(2, name);
            ps.setDouble(3, cost);
            ps.setInt(4, 0);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a new menu item along with its ingredients to the database.
     *
     * @param name                           The name of the menu item.
     * @param cost                           The cost of the menu item.
     * @param ingredientQtyByInventoryId A map of inventory IDs to their quantities.
     * @return true if successful, false otherwise.
     */
    public static boolean addMenuItemWithIngredients(String name, double cost, Map<Integer, Integer> ingredientQtyByInventoryId) {
        String menuSql = "INSERT INTO menu (menuID, name, cost, salesNum) VALUES (?, ?, ?, ?)";
        String recipeSql = "INSERT INTO menu_items (ID, inventoryID, menuID, itemQuantity) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            int menuID = getNextID(conn, "menu", "menuID");

            try (PreparedStatement ps = conn.prepareStatement(menuSql)) {
                ps.setInt(1, menuID);
                ps.setString(2, name);
                ps.setDouble(3, cost);
                ps.setInt(4, 0);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(recipeSql)) {
                for (Map.Entry<Integer, Integer> entry : ingredientQtyByInventoryId.entrySet()) {
                    int recipeID = getNextID(conn, "menu_items", "ID");
                    ps.setInt(1, recipeID);
                    ps.setInt(2, entry.getKey());
                    ps.setInt(3, menuID);
                    ps.setInt(4, entry.getValue());
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    /**
     * Updates an existing menu item's details.
     *
     * @param menuID The ID of the menu item to update.
     * @param name   The new name.
     * @param cost   The new cost.
     * @return true if successful, false otherwise.
     */
    public static boolean updateMenuItem(int menuID, String name, double cost) {
        String sql = "UPDATE menu SET name = ?, cost = ? WHERE menuID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, cost);
            ps.setInt(3, menuID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a menu item from the database.
     *
     * @param menuID The ID of the menu item to delete.
     * @return true if successful, false otherwise.
     */
    public static boolean deleteMenuItem(int menuID) {
        String sql = "DELETE FROM menu WHERE menuID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, menuID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all items from the inventory.
     *
     * @return A list of InventoryItem objects.
     */
    public static List<InventoryItem> getAllInventory() {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory ORDER BY name";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(new InventoryItem(
                        rs.getInt("inventoryID"),
                        rs.getString("name"),
                        rs.getDouble("cost"),
                        rs.getInt("inventoryNum"),
                        rs.getInt("useAverage")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Updates an existing inventory item's details.
     *
     * @param id   The ID of the inventory item to update.
     * @param name The new name.
     * @param cost The new cost per unit.
     * @param qty  The new quantity in stock.
     * @param avg  The new usage average.
     * @return true if successful, false otherwise.
     */
    public static boolean updateInventoryItem(int id, String name, double cost, int qty, int avg) {
        String sql = "UPDATE inventory SET name = ?, cost = ?, inventoryNum = ?, useAverage = ? WHERE inventoryID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, cost);
            ps.setInt(3, qty);
            ps.setInt(4, avg);
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a new inventory item to the database.
     *
     * @param name The name of the inventory item.
     * @param cost The cost per unit.
     * @param qty  The initial quantity.
     * @param avg  The usage average.
     * @return true if successful, false otherwise.
     */
    public static boolean addInventoryItem(String name, double cost, int qty, int avg) {
        String sql = "INSERT INTO inventory (inventoryID, name, cost, inventoryNum, useAverage) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            int inventoryID = getNextID(conn, "inventory", "inventoryID");
            ps.setInt(1, inventoryID);
            ps.setString(2, name);
            ps.setDouble(3, cost);
            ps.setInt(4, qty);
            ps.setInt(5, avg);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an inventory item from the database.
     *
     * @param inventoryID The ID of the inventory item to delete.
     * @return true if successful, false otherwise.
     */
    public static boolean deleteInventoryItem(int inventoryID) {
        String sql = "DELETE FROM inventory WHERE inventoryID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, inventoryID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all employees from the database.
     *
     * @return A list of Employee objects.
     */
    public static List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY name";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                employees.add(new Employee(
                        rs.getInt("employeeID"),
                        rs.getString("name"),
                        rs.getDouble("pay"),
                        rs.getString("job"),
                        rs.getInt("orderNum")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    /**
     * Adds a new employee to the database.
     *
     * @param name The name of the employee.
     * @param job  The job title of the employee.
     * @param pay  The pay rate of the employee.
     */
    public static void addEmployee(String name, String job, double pay) {
        String sql = "INSERT INTO employees (employeeID, name, pay, job, orderNum) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection()) {
            int empID = getNextID(conn, "employees", "employeeID");
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, empID);
                ps.setString(2, name);
                ps.setDouble(3, pay);
                ps.setString(4, job);
                ps.setInt(5, 0); // initial order count
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing employee's details.
     *
     * @param empID The ID of the employee to update.
     * @param name  The new name.
     * @param job   The new job title.
     * @param pay   The new pay rate.
     */
    public static void updateEmployee(int empID, String name, String job, double pay) {
        String sql = "UPDATE employees SET name = ?, pay = ?, job = ? WHERE employeeID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, pay);
            ps.setString(3, job);
            ps.setInt(4, empID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes an employee from the database.
     *
     * @param empID The ID of the employee to delete.
     */
    public static void deleteEmployee(int empID) {
        String sql = "DELETE FROM employees WHERE employeeID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves weekly order counts.
     *
     * @return A list of WeeklyOrdersRow objects.
     */
    public static List<WeeklyOrdersRow> getWeeklyOrders() {
        List<WeeklyOrdersRow> rows = new ArrayList<>();
        String sql = """
                SELECT
                  date_trunc('week', orderdatetime)::date AS week_start,
                  COUNT(*) AS orders_count
                FROM orders
                GROUP BY date_trunc('week', orderdatetime)::date
                ORDER BY week_start;
                """;

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new WeeklyOrdersRow(
                        rs.getDate("week_start").toLocalDate(),
                        rs.getInt("orders_count")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    /**
     * Retrieves the most popular menu items.
     *
     * @param limit The maximum number of items to retrieve.
     * @return A list of PopularItemRow objects.
     */
    public static List<PopularItemRow> getPopularItems(int limit) {
        List<PopularItemRow> rows = new ArrayList<>();
        String sql = """
                SELECT
                  m.name AS menu_item,
                  SUM(oi.quantity)::int AS total_quantity
                FROM order_items oi
                JOIN menu m
                  ON oi.menuID = m.menuID
                GROUP BY m.name
                ORDER BY total_quantity DESC
                LIMIT ?;
                """;

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new PopularItemRow(
                            rs.getString("menu_item"),
                            rs.getInt("total_quantity")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }

    /**
     * Retrieves inventory usage for a specific date range.
     *
     * @param startDateInclusive The start date (inclusive).
     * @param endDateInclusive   The end date (inclusive).
     * @return A list of InventoryUsageRow objects.
     */
    public static List<InventoryUsageRow> getInventoryUsage(LocalDate startDateInclusive, LocalDate endDateInclusive) {
        List<InventoryUsageRow> rows = new ArrayList<>();

        LocalDateTime start = startDateInclusive.atStartOfDay();
        LocalDateTime endExclusive = endDateInclusive.plusDays(1).atStartOfDay();

        String sql = """
            SELECT
              i.name AS inventory_item,
              SUM(oi.quantity * mi.itemQuantity)::int AS units_used
            FROM orders o
            JOIN order_items oi ON oi.orderID = o.orderID
            JOIN menu_items mi ON mi.menuID = oi.menuID
            JOIN inventory i ON i.inventoryID = mi.inventoryID
            WHERE o.orderdatetime >= ?
              AND o.orderdatetime < ?
            GROUP BY i.name
            ORDER BY units_used DESC;
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(endExclusive));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new InventoryUsageRow(
                            rs.getString("inventory_item"),
                            rs.getInt("units_used")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }

    /**
     * Retrieves inventory items with the lowest stock levels.
     *
     * @param limit The maximum number of items to retrieve.
     * @return A list of StockLevelRow objects.
     */
    public static List<StockLevelRow> getLowestStock(int limit) {
        List<StockLevelRow> rows = new ArrayList<>();
        String sql = "SELECT name, inventoryNum FROM inventory ORDER BY inventoryNum ASC LIMIT ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new StockLevelRow(rs.getString("name"), rs.getInt("inventoryNum")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    /**
     * Checks if the order can be fulfilled based on current inventory.
     *
     * @param cartItems The items in the order.
     * @return true if there is sufficient stock for all items, false otherwise.
     */
    public static boolean canFulfillOrder(List<CartItem> cartItems) {
        Map<Integer, Integer> requiredIngredients = new HashMap<>();
        String recipeSql = "SELECT inventoryID, itemQuantity FROM menu_items WHERE menuID = ?";

        try (Connection conn = getConnection()) {
            for (CartItem item : cartItems) {
                try (PreparedStatement ps = conn.prepareStatement(recipeSql)) {
                    ps.setInt(1, item.getMenuID());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int invID = rs.getInt("inventoryID");
                            int qty = rs.getInt("itemQuantity");
                            requiredIngredients.put(invID, requiredIngredients.getOrDefault(invID, 0) + qty);
                        }
                    }
                }
            }

            // Check against inventory
            String invSql = "SELECT inventoryNum FROM inventory WHERE inventoryID = ?";
            for (Map.Entry<Integer, Integer> entry : requiredIngredients.entrySet()) {
                try (PreparedStatement ps = conn.prepareStatement(invSql)) {
                    ps.setInt(1, entry.getKey());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int stock = rs.getInt("inventoryNum");
                            if (stock < entry.getValue()) {
                                return false; // Not enough stock
                            }
                        } else {
                            return false; // Ingredient missing from inventory table
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Submits an order and updates inventory, menu sales, and employee order counts.
     *
     * @param customer  The name of the customer.
     * @param empID     The ID of the employee.
     * @param cartItems The items being ordered.
     */
    public static void submitOrder(String customer, int empID, List<CartItem> cartItems) {
        String ordSql = "INSERT INTO orders (orderID, customerName, costTotal, employeeID, orderDateTime) VALUES (?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_items (ID, menuID, orderID, quantity, iceLevel, sugarLevel, topping, cost) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String updateEmpSql = "UPDATE employees SET orderNum = orderNum + 1 WHERE employeeID = ?";
        String updateMenuSql = "UPDATE menu SET salesNum = salesNum + 1 WHERE menuID = ?";
        String recipeSql = "SELECT inventoryID, itemQuantity FROM menu_items WHERE menuID = ?";
        String deductInvSql = "UPDATE inventory SET inventoryNum = inventoryNum - ? WHERE inventoryID = ?";
        String updateTodaySql = "INSERT INTO orders_today (id, sales) VALUES (1, ?) ON CONFLICT (id) DO UPDATE SET sales = orders_today.sales + EXCLUDED.sales";

        Connection conn = null;
        int oID = -1;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            oID = getNextID(conn, "orders", "orderID");
            double total = cartItems.stream().mapToDouble(CartItem::getCost).sum();

            // Insert into orders table
            try (PreparedStatement ps = conn.prepareStatement(ordSql)) {
                ps.setInt(1, oID);
                ps.setString(2, customer);
                ps.setDouble(3, total);
                ps.setInt(4, empID);
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
            }

            // Update employee order count
            try (PreparedStatement ps = conn.prepareStatement(updateEmpSql)) {
                ps.setInt(1, empID);
                ps.executeUpdate();
            }

            // Update row 1 of orders_today
            try (PreparedStatement ps = conn.prepareStatement(updateTodaySql)) {
                ps.setDouble(1, total);
                ps.executeUpdate();
            }

            // Prepare statements outside loop for performance
            try (PreparedStatement psItem = conn.prepareStatement(itemSql);
                 PreparedStatement psMenu = conn.prepareStatement(updateMenuSql);
                 PreparedStatement psRecipe = conn.prepareStatement(recipeSql);
                 PreparedStatement psDeduct = conn.prepareStatement(deductInvSql)) {

                for (CartItem item : cartItems) {
                    int itemID = getNextID(conn, "order_items", "ID");

                    // Insert into order_items table
                    psItem.setInt(1, itemID);
                    psItem.setInt(2, item.getMenuID());
                    psItem.setInt(3, oID);
                    psItem.setInt(4, 1); // quantity
                    psItem.setString(5, item.getIce());
                    psItem.setString(6, item.getSugar());
                    psItem.setString(7, item.getTopping());
                    psItem.setDouble(8, item.getCost());
                    psItem.executeUpdate();

                    // Update menu sales count
                    psMenu.setInt(1, item.getMenuID());
                    psMenu.executeUpdate();

                    // Inventory deduction based on recipe
                    psRecipe.setInt(1, item.getMenuID());
                    try (ResultSet rs = psRecipe.executeQuery()) {
                        while (rs.next()) {
                            int invID = rs.getInt("inventoryID");
                            int qtyToDeduct = rs.getInt("itemQuantity");
                            psDeduct.setInt(1, qtyToDeduct);
                            psDeduct.setInt(2, invID);
                            psDeduct.executeUpdate();
                        }
                    }
                }
            }

            conn.commit();
            System.out.println("[LOG] Order #" + oID + " finalized and inventory updated.");
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("[ERROR] Transaction rolled back for Order #" + (oID != -1 ? oID : "unknown"));
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    /**
     * Restocks a specific inventory item.
     *
     * @param itemName The name of the item.
     * @param quantity The quantity to add to the current stock.
     */
    public static void restockItem(String itemName, int quantity) {
        String sql = "UPDATE inventory SET inventoryNum = inventoryNum + ? WHERE name = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setString(2, itemName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves inventory items with stock levels below a threshold.
     *
     * @param threshold The threshold below which an item is considered low stock.
     * @return A list of InventoryItem objects.
     */
    public static List<InventoryItem> getLowStockItems(int threshold) {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory WHERE inventoryNum < ? ORDER BY inventoryNum ASC";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new InventoryItem(
                            rs.getInt("inventoryID"),
                            rs.getString("name"),
                            rs.getDouble("cost"),
                            rs.getInt("inventoryNum"),
                            rs.getInt("useAverage")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Retrieves an employee by their ID.
     *
     * @param employeeID The ID of the employee.
     * @return An Employee object, or null if not found.
     */
    public static Employee getEmployeeByID(int employeeID) {
        String sql = "SELECT * FROM employees WHERE employeeID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                            rs.getInt("employeeID"),
                            rs.getString("name"),
                            rs.getDouble("pay"),
                            rs.getString("job"),
                            rs.getInt("orderNum"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Generates the next available ID for a table.
     *
     * @param conn  The database connection.
     * @param table The table name.
     * @param col   The column name for the ID.
     * @return The next available ID.
     * @throws SQLException If a database error occurs.
     */
    private static int getNextID(Connection conn, String table, String col) throws SQLException {
        String q = "SELECT MAX(" + col + ") FROM " + table;
        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(q)) {
            return rs.next() ? rs.getInt(1) + 1 : 1;
        }
    }

    /**
     * Retrieves X-Reports for a specific date.
     *
     * @param date The date.
     * @return A list of XReports objects.
     */
    public static List<XReports> getXReports(LocalDate date) 
    {
        List<XReports> reports = new ArrayList<>();
        String sql = "SELECT EXTRACT(HOUR FROM orderDateTime) AS hour, " +
                 "SUM(costTotal) AS total_sales, " +
                 "COUNT(*) AS order_count " +
                 "FROM orders WHERE orderDateTime::date = ? " +
                 "GROUP BY 1 ORDER BY 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
             {
                ps.setObject(1, date);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) 
                    {
                        int hour = rs.getInt("hour");
                        double totalSales = rs.getDouble("total_sales");
                        int orderCount = rs.getInt("order_count");
                        reports.add(new XReports(orderCount, hour, totalSales));
                    }
            } catch (SQLException e) 
            {
                e.printStackTrace();
                    
            }
            return reports;   
    }

    /**
     * Retrieves Z-Report data.
     *
     * @return A ZReportData object.
     */
    public static ZReportData getZReport() {
        double totalSales = 0;
        List<ZReportRow> employeeOrders = new ArrayList<>();

        String salesSql = "SELECT sales FROM orders_today WHERE id = 1";
        String empSql = """
            SELECT e.name, COUNT(o.orderID) as daily_orders
            FROM employees e
            LEFT JOIN orders o ON e.employeeID = o.employeeID AND o.orderDateTime::date = CURRENT_DATE
            GROUP BY e.name
            ORDER BY daily_orders DESC
            """;

        try (Connection conn = getConnection()) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(salesSql)) {
                if (rs.next()) {
                    totalSales = rs.getDouble("sales");
                }
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(empSql)) {
                while (rs.next()) {
                    employeeOrders.add(new ZReportRow(
                        rs.getString("name"),
                        totalSales == 0 ? 0 : rs.getInt("daily_orders")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ZReportData(totalSales, employeeOrders);
    }

    /**
     * Clears daily order data.
     */
    public static void clearOrdersToday() {
        String sql = "DELETE FROM orders_today";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the ingredients for a specific menu item.
     *
     * @param menuID      The ID of the menu item.
     * @param ingredients A map of inventory IDs to their required quantities.
     */
    public static void updateMenuIngredients(int menuID, Map<Integer, Integer> ingredients)
    {
        String deleteSql = "DELETE FROM menu_items WHERE menuID = ?";
        String insertSql = "INSERT INTO menu_items (ID, inventoryID, menuID, itemQuantity) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Delete existing ingredients for the menu item
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, menuID);
                ps.executeUpdate();
            }

            // Insert new ingredients
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Map.Entry<Integer, Integer> entry : ingredients.entrySet()) {
                    int recipeID = getNextID(conn, "menu_items", "ID");
                    ps.setInt(1, recipeID);
                    ps.setInt(2, entry.getKey());
                    ps.setInt(3, menuID);
                    ps.setInt(4, entry.getValue());
                    ps.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }
}
