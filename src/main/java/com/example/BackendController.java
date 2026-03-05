package com.example;

import java.util.List;
import java.time.LocalDate;
import java.util.Map;


/**
 * Acts as the bridge between the Wireframe UI and the Data.
 */
public class BackendController {

    private static boolean isZReportRun = false;

    // --- CASHIER LOGIC ---
    public static List<Product> getMenu() {
        return Database.getAllProducts();
    }

    /**
     * Calculates the total price of items in the cart.
     */
    public static double calculateCartTotal(List<CartItem> cartItems) {
        return cartItems.stream().mapToDouble(CartItem::getCost).sum();
    }

    /**
     * Logic for the "Place Order" button.
     * @return true if successful, false if inventory check failed or database error.
     */
    public static boolean handlePlaceOrder(String customerName, int employeeID, List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            System.out.println("[WARN] Attempted to place order with empty cart.");
            return false;
        }
        
        try {
            // Validate inventory before submitting
            if (!Database.canFulfillOrder(cartItems)) {
                System.out.println("[BACKEND] Order rejected: Insufficient inventory.");
                return false;
            }

            // Hardcoded employeeID for now, will be replaced by login system. - changing it now
            //int employeeID = 1;
            Database.submitOrder(customerName, employeeID, cartItems);
            
            System.out.println("[BACKEND] Order successfully saved.");
            return true;

        } catch (Exception e) {
            System.out.println("[BACKEND ERROR] Failed to save order.");
            e.printStackTrace();
            return false;
        }
    }

    // --- MANAGER LOGIC: TEAM ---

    /**
     * Validates employee input from the Add Employee Dialog.
     */
    public static boolean validateEmployeeInput(String name, String role, String pay, String hrs) {
        if (name.isEmpty() || role.isEmpty() || pay.isEmpty() || hrs.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(pay);
            Double.parseDouble(hrs);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void handleAddEmployee(String name, String role, String pay) {
        try {
            double payRate = Double.parseDouble(pay);
            Database.addEmployee(name, role, payRate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleUpdateEmployee(int id, String name, String role, String pay) {
        try {
            double payRate = Double.parseDouble(pay);
            Database.updateEmployee(id, name, role, payRate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleRemoveEmployee(int id) {
        try {
            Database.deleteEmployee(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- MANAGER LOGIC: STOCK ---

    /**
     * Logic for the "Restock" button interaction.
     */
    public static void handleRestock(String itemName) {
        try {
            // Default restock amount of 100 units
            Database.restockItem(itemName, 100);
            System.out.println("[BACKEND] Restock successful for: " + itemName);
        } catch (Exception e) {
            System.out.println("[BACKEND ERROR] Restock failed for: " + itemName);
            e.printStackTrace();
        }
    }

    // --- MANAGER LOGIC: X REPORT ---

    /**
     * Logic for the "X Reports" tab from database.
     */
    public static List<XReports> getXReports(LocalDate date) {
        return Database.getXReports(date);
    }

    public static Database.ZReportData getZReport() {
        return Database.getZReport();
    }

    public static void clearOrdersToday() {
        Database.clearOrdersToday();
        isZReportRun = true;
    }

    public static boolean hasZReportRun() {
        return isZReportRun;
    }

    //menu ingredients modification
    public static void setMenuIngredients(int menuID, Map<Integer, Integer> ingredients) 
    {
            Database.updateMenuIngredients(menuID, ingredients);
    }

}