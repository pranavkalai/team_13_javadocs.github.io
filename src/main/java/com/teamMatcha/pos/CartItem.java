package com.teamMatcha.pos;

/**
 * Represents an item in the shopping cart.
 */
public class CartItem {
    /** The unique ID of the menu item. */
    private int menuID;
    /** The name of the menu item. */
    private String name;
    /** The cost of the menu item. */
    private double cost;
    /** The ice level for the item. */
    private String ice;
    /** The sugar level for the item. */
    private String sugar;
    /** The topping for the item. */
    private String topping;

    /**
     * Constructs a new CartItem with the specified details.
     *
     * @param menuID  The unique ID of the menu item.
     * @param name    The name of the menu item.
     * @param cost    The cost of the menu item.
     * @param ice     The ice level for the item.
     * @param sugar   The sugar level for the item.
     * @param topping The topping for the item.
     */
    public CartItem(int menuID, String name, double cost, String ice, String sugar, String topping) {
        this.menuID = menuID;
        this.name = name;
        this.cost = cost;
        this.ice = ice;
        this.sugar = sugar;
        this.topping = topping;
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
     * Gets the ice level of the item.
     *
     * @return The ice level.
     */
    public String getIce() { return ice; }

    /**
     * Gets the sugar level of the item.
     *
     * @return The sugar level.
     */
    public String getSugar() { return sugar; }

    /**
     * Gets the topping of the item.
     *
     * @return The topping.
     */
    public String getTopping() { return topping; }
}
