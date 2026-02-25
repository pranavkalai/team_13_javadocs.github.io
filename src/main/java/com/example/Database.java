package com.example;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

public class Database {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_13_db";
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

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
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateEmployee(int empID, String name, String job, double pay) {
        String sql = "UPDATE employees SET name = ?, pay = ?, job = ? WHERE employeeID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, pay);
            ps.setString(3, job);
            ps.setInt(4, empID);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void deleteEmployee(int empID) {
        String sql = "DELETE FROM employees WHERE employeeID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empID);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
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