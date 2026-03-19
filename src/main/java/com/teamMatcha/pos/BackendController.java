package com.teamMatcha.pos;

import java.util.List;
import java.time.LocalDate;
import java.util.Map;


/**
 * Acts as the bridge between the UI and the Database.
 * Handles various business logic tasks for the cashier and manager views.
 */
public class BackendController {

    /** Indicates whether the Z-Report has been run for the current day. */
    private static boolean isZReportRun = false;

    // --- CASHIER LOGIC ---

    /**
     * Retrieves the full menu from the database.
     *
     * @return A list of all products in the menu.
     */
    public static List<Product> getMenu() {
        return Database.getAllProducts();
    }

    /**
     * Calculates the total price of items in the cart.
     *
     * @param cartItems The list of items in the cart.
     * @return The total cost of all items in the cart.
     */
    public static double calculateCartTotal(List<CartItem> cartItems) {
        return cartItems.stream().mapToDouble(CartItem::getCost).sum();
    }

    /**
     * Logic for the "Place Order" button.
     * Validates inventory and submits the order to the database.
     *
     * @param customerName The name of the customer placing the order.
     * @param employeeID   The ID of the employee processing the order.
     * @param cartItems    The list of items being ordered.
     * @return true if the order was successfully placed, false otherwise.
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
     * Validates employee input from the Add/Edit Employee Dialog.
     *
     * @param name The name of the employee.
     * @param role The job role of the employee.
     * @param pay  The pay rate as a string.
     * @param hrs  The hours (currently unused but validated).
     * @return true if all inputs are valid, false otherwise.
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

    /**
     * Handles adding a new employee to the database.
     *
     * @param name The name of the new employee.
     * @param role The job role of the new employee.
     * @param pay  The pay rate of the new employee as a string.
     */
    public static void handleAddEmployee(String name, String role, String pay) {
        try {
            double payRate = Double.parseDouble(pay);
            Database.addEmployee(name, role, payRate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles updating an existing employee's information.
     *
     * @param id   The unique ID of the employee to update.
     * @param name The updated name of the employee.
     * @param role The updated job role of the employee.
     * @param pay  The updated pay rate of the employee as a string.
     */
    public static void handleUpdateEmployee(int id, String name, String role, String pay) {
        try {
            double payRate = Double.parseDouble(pay);
            Database.updateEmployee(id, name, role, payRate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles removing an employee from the database.
     *
     * @param id The unique ID of the employee to remove.
     */
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
     *
     * @param itemName The name of the inventory item to restock.
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
     * Retrieves X-Reports for a specific date from the database.
     *
     * @param date The date for which to retrieve reports.
     * @return A list of X-Reports for the given date.
     */
    public static List<XReports> getXReports(LocalDate date) {
        return Database.getXReports(date);
    }

    /**
     * Retrieves the Z-Report data.
     *
     * @return The Z-Report data.
     */
    public static Database.ZReportData getZReport() {
        return Database.getZReport();
    }

    /**
     * Clears the daily order summary.
     */
    public static void clearOrdersToday() {
        Database.clearOrdersToday();
        isZReportRun = true;
    }

    /**
     * Checks if the Z-Report has already been run today.
     *
     * @return true if the Z-Report has run, false otherwise.
     */
    public static boolean hasZReportRun() {
        return isZReportRun;
    }

    /**
     * Updates the ingredients for a specific menu item.
     *
     * @param menuID      The ID of the menu item.
     * @param ingredients A map of inventory IDs to their required quantities.
     */
    public static void setMenuIngredients(int menuID, Map<Integer, Integer> ingredients) 
    {
            Database.updateMenuIngredients(menuID, ingredients);
    }

}
