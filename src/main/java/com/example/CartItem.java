package com.example;

public class CartItem {
    private int menuID;
    private String name;
    private double cost;
    private String ice;
    private String sugar;
    private String topping;

    public CartItem(int menuID, String name, double cost, String ice, String sugar, String topping) {
        this.menuID = menuID;
        this.name = name;
        this.cost = cost;
        this.ice = ice;
        this.sugar = sugar;
        this.topping = topping;
    }
    public int getMenuID() { return menuID; }
    public String getName() { return name; }
    public double getCost() { return cost; }
    public String getIce() { return ice; }
    public String getSugar() { return sugar; }
    public String getTopping() { return topping; }
}