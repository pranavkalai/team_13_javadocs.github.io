package com.example;

/**
 * Represents a product in the menu.
 */
public class Product {
    /** The unique ID of the menu item. */
    private int menuID;
    /** The name of the menu item. */
    private String name;
    /** The cost of the menu item. */
    private double cost;
    /** The number of units sold. */
    private int salesNum;

    /**
     * Constructs a new Product with the specified details.
     *
     * @param menuID   The unique ID of the menu item.
     * @param name     The name of the menu item.
     * @param cost     The cost of the menu item.
     * @param salesNum The number of units sold.
     */
    public Product(int menuID, String name, double cost, int salesNum) {
        this.menuID = menuID;
        this.name = name;
        this.cost = cost;
        this.salesNum = salesNum;
    }

    /**
     * Gets the menu item's ID.
     *
     * @return The menu ID.
     */
    public int getMenuID() { return menuID; }

    /**
     * Gets the name of the menu item.
     *
     * @return The name.
     */
    public String getName() { return name; }

    /**
     * Gets the cost of the menu item.
     *
     * @return The cost.
     */
    public double getCost() { return cost; }

    /**
     * Gets the number of units sold.
     *
     * @return The sales number.
     */
    public int getSalesNum() { return salesNum; }
}
