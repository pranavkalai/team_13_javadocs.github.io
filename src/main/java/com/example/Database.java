package com.example;

import io.github.cdimascio.dotenv.Dotenv;

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

public class Database {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private static final String URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_13_db";
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    public static class WeeklyOrdersRow {
        private final LocalDate weekStart;
        private final int ordersCount;

        public WeeklyOrdersRow(LocalDate weekStart, int ordersCount) {
            this.weekStart = weekStart;
            this.ordersCount = ordersCount;
        }

        public LocalDate getWeekStart() {
            return weekStart;
        }

        public int getOrdersCount() {
            return ordersCount;
        }
    }

    public static class PopularItemRow {
        private final String menuItem;
        private final int totalQuantity;

        public PopularItemRow(String menuItem, int totalQuantity) {
            this.menuItem = menuItem;
            this.totalQuantity = totalQuantity;
        }

        public String getMenuItem() {
            return menuItem;
        }

        public int getTotalQuantity() {
            return totalQuantity;
        }
    }

    public static class StockLevelRow {
        private final String name;
        private final int stock;

        public StockLevelRow(String name, int stock) {
            this.name = name;
            this.stock = stock;
        }

        public String getName() {
            return name;
        }

        public int getStock() {
            return stock;
        }
    }

    public static class InventoryUsageRow {
        private final String inventoryItem;
        private final int unitsUsed;

        public InventoryUsageRow(String inventoryItem, int unitsUsed) {
            this.inventoryItem = inventoryItem;
            this.unitsUsed = unitsUsed;
        }

        public String getInventoryItem() { return inventoryItem; }
        public int getUnitsUsed() { return unitsUsed; }
    }

    public static Connection getConnection() throws SQLException {
        if (USER == null || PASSWORD == null) {
            throw new SQLException("Database credentials not set in environment variables (DB_USER, DB_PASSWORD)");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

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

    public static void deleteEmployee(int empID) {
        String sql = "DELETE FROM employees WHERE employeeID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
    public static void submitOrder(String customer, int empID, List<CartItem> cartItems) {
        String ordSql = "INSERT INTO orders (orderID, customerName, costTotal, employeeID, orderDateTime) VALUES (?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_items (ID, menuID, orderID, quantity, iceLevel, sugarLevel, topping, cost) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String updateEmpSql = "UPDATE employees SET orderNum = orderNum + 1 WHERE employeeID = ?";
        String updateMenuSql = "UPDATE menu SET salesNum = salesNum + 1 WHERE menuID = ?";
        String recipeSql = "SELECT inventoryID, itemQuantity FROM menu_items WHERE menuID = ?";
        String deductInvSql = "UPDATE inventory SET inventoryNum = inventoryNum - ? WHERE inventoryID = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            int oID = getNextID(conn, "orders", "orderID");
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

            for (CartItem item : cartItems) {
                int itemID = getNextID(conn, "order_items", "ID");

                // Insert into order_items table
                try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                    ps.setInt(1, itemID);
                    ps.setInt(2, item.getMenuID());
                    ps.setInt(3, oID);
                    ps.setInt(4, 1); // quantity
                    ps.setString(5, item.getIce());
                    ps.setString(6, item.getSugar());
                    ps.setString(7, item.getTopping());
                    ps.setDouble(8, item.getCost());
                    ps.executeUpdate();
                }

                // Update menu sales count
                try (PreparedStatement ps = conn.prepareStatement(updateMenuSql)) {
                    ps.setInt(1, item.getMenuID());
                    ps.executeUpdate();
                }

                // Inventory deduction based on recipe
                try (PreparedStatement psRecipe = conn.prepareStatement(recipeSql)) {
                    psRecipe.setInt(1, item.getMenuID());
                    try (ResultSet rs = psRecipe.executeQuery()) {
                        while (rs.next()) {
                            int invID = rs.getInt("inventoryID");
                            int qtyToDeduct = rs.getInt("itemQuantity");
                            try (PreparedStatement psDeduct = conn.prepareStatement(deductInvSql)) {
                                psDeduct.setInt(1, qtyToDeduct);
                                psDeduct.setInt(2, invID);
                                psDeduct.executeUpdate();
                            }
                        }
                    }
                }
            }

            conn.commit();
            System.out.println("[LOG] Order #" + oID + " finalized and inventory updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    private static int getNextID(Connection conn, String table, String col) throws SQLException {
        String q = "SELECT MAX(" + col + ") FROM " + table;
        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(q)) {
            return rs.next() ? rs.getInt(1) + 1 : 1;
        }
    }
}
