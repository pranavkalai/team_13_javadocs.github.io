package com.example;

public class CartItem {
    private int menuID;
    private String name;
    private double cost;
    private String iceLevel;
    private String sugarLevel;
    private String topping;

    public CartItem(int menuID, String name, double cost, String iceLevel, String sugarLevel, String topping) {
        this.menuID = menuID;
        this.name = name;
        this.cost = cost;
        this.iceLevel = iceLevel;
        this.sugarLevel = sugarLevel;
        this.topping = topping;
    }


    public double getCost()
    {
        return cost;
    }

    public int getMenuID() {
        return menuID;
    }

    public String getName() {
        return name;
    }

    public String getIce() {
        return iceLevel;
    }

    public String getSugar() {
        return sugarLevel;
    }

    public String getTopping() {
        return topping;
    }

}
