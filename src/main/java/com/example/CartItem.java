package com.example;

import javafx.scene.control.MenuItem;
public class CartItem {
    private int menuID;
    private String name;
    private double price;
    private String ice;
    private String sugar;
    private String topping;

    public CartItem(int menuID, String name, double price, String ice, String sugar, String topping) {
        this.menuID = menuID;
        this.name = name;
        this.price = price;
        this.ice = ice;
        this.sugar = sugar;
        this.topping = topping;
    }


    public double getPrice()
    {
        return price;
    }

    public int getMenuID() {
        return menuID;
    }

    public String getName() {
        return name;
    }
}
