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
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

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

        public LocalDate getWeekStart() { return weekStart; }
        public int getOrdersCount() { return ordersCount; }
    }

    public static class PopularItemRow {
        private final String menuItem;
        private final int totalQuantity;

        public PopularItemRow(String menuItem, int totalQuantity) {
            this.menuItem = menuItem;
            this.totalQuantity = totalQuantity;
        }

        public String getMenuItem() { return menuItem; }
        public int getTotalQuantity() { return totalQuantity; }
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
                    rs.getInt("salesNum")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
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
                    rs.getInt("useAverage")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
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
                    rs.getInt("orderNum")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
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
                    rs.getInt("orders_count")
                ));
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
                        rs.getInt("total_quantity")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }
    
    
    public static void submitOrder(String customer, int empID, List<CartItem> cartItems) {
        String ordSql = "INSERT INTO orders (orderID, customerName, costTotal, employeeID, orderDateTime) VALUES (?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_items (ID, menuID, orderID, quantity, iceLevel, sugarLevel, topping, cost) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            int oID = getNextID(conn, "orders", "orderID");
            double total = cartItems.stream().mapToDouble(CartItem::getCost).sum();

            // insert into orders table
            try (PreparedStatement ps = conn.prepareStatement(ordSql)) 
                {
                ps.setInt(1, oID);
                ps.setString(2, customer);
                ps.setDouble(3, total);
                ps.setInt(4, empID);
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
                }
            for (CartItem item : cartItems) 
                {

                int itemID = getNextID(conn, "order_items", "ID");

                try (PreparedStatement ps = conn.prepareStatement(itemSql)) 
                {
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
            }

            conn.commit();
            System.out.println("[LOG] Order #" + oID + " finalized.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static int getNextID(Connection conn, String table, String col) throws SQLException {
        String q = "SELECT MAX(" + col + ") FROM " + table;
        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(q)) {
            return rs.next() ? rs.getInt(1) + 1 : 1;
        }
    }
}