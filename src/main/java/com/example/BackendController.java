package com.example;

import java.util.List;

/**
 * Acts as the bridge between the Wireframe UI and the Data.
 */
public class BackendController {

    // --- CASHIER LOGIC ---

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
    public static boolean handlePlaceOrder(String customerName, List<CartItem> cartItems) {
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

            // Hardcoded employeeID for now, will be replaced by login system.
            int employeeID = 1;
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
}