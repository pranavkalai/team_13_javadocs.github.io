package com.example;

import java.util.List;

/**
 * Acts as the bridge between the Wireframe UI and the Data.
 * Currently holds "Mock" data for Frontend-first development.
 */
public class BackendController {

    // --- CASHIER LOGIC ---

    /**
     * Calculates the total price of items in the cart.
     * Maps to Page 1 of the wireframe for the "Total: $X.XX" label.
     */
    public static double calculateCartTotal(List<String> cartItems) {
        // Mock logic: assuming all Boba tea items are $5.00 for now.
        return cartItems.size() * 5.00;
    }

    /**
     * Placeholder for the "Place Order" button logic.
     * In the future, this will call Database.submitOrder().
     */
    public static void handlePlaceOrder(String customerName, List<String> cartItems) {
        System.out.println("[FRONTEND LOGIC] Processing order for: " + customerName);
        System.out.println("[FRONTEND LOGIC] Items: " + cartItems);
        // Calculation logic would happen here before saving to DB
    }

    // --- MANAGER LOGIC: TEAM ---

    /**
     * Validates employee input from the Add Employee Dialog.
     */
    public static boolean validateEmployeeInput(String name, String role, String pay, String hrs) {
        // Frontend check: ensure no fields are empty
        if (name.isEmpty() || role.isEmpty() || pay.isEmpty() || hrs.isEmpty()) {
            return false;
        }
        
        // Ensure pay and hours are actually numbers to avoid crashes later
        try {
            Double.parseDouble(pay);
            Double.parseDouble(hrs);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // --- MANAGER LOGIC: STOCK ---

    /**
     * Mock data for initial inventory levels (Boba, Sugar, Milk).
     */
    public static String getMockInventoryLevel(String itemName) {
        // Matches the wireframe starting state of "0 units".
        return "0 units";
    }

    /**
     * Logic for the "Restock" button interaction.
     */
    public static void handleRestock(String itemName) {
        System.out.println("[FRONTEND LOGIC] Restock triggered for: " + itemName);
    }
}