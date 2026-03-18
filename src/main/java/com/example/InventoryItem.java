package com.example;

/**
 * Represents an item in the inventory.
 */
public class InventoryItem {
    /** The unique ID of the inventory item. */
    private int inventoryID;
    /** The name of the inventory item. */
    private String name;
    /** The cost of the inventory item. */
    private double cost;
    /** The current quantity in inventory. */
    private int inventoryNum;
    /** The average usage of the item. */
    private int useAverage;

    /**
     * Constructs a new InventoryItem with the specified details.
     *
     * @param inventoryID  The unique ID of the inventory item.
     * @param name         The name of the inventory item.
     * @param cost         The cost of the inventory item.
     * @param inventoryNum The current quantity in inventory.
     * @param useAverage   The average usage of the item.
     */
    public InventoryItem(int inventoryID, String name, double cost, int inventoryNum, int useAverage) {
        this.inventoryID = inventoryID;
        this.name = name;
        this.cost = cost;
        this.inventoryNum = inventoryNum;
        this.useAverage = useAverage;
    }

    /**
     * Gets the inventory item's ID.
     *
     * @return The inventory ID.
     */
    public int getInventoryID() { return inventoryID; }

    /**
     * Gets the name of the inventory item.
     *
     * @return The name.
     */
    public String getName() { return name; }

    /**
     * Gets the cost of the inventory item.
     *
     * @return The cost.
     */
    public double getCost() { return cost; }

    /**
     * Gets the current quantity in inventory.
     *
     * @return The inventory number.
     */
    public int getInventoryNum() { return inventoryNum; }

    /**
     * Gets the average usage of the item.
     *
     * @return The average usage.
     */
    public int getUseAverage() { return useAverage; }
}
