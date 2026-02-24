package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import javafx.collections.ObservableList;

public class Database {
    private static final String URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_13_db";
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
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