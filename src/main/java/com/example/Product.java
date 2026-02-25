package com.example;

public class Product {
    private int menuID;
    private String name;
    private double cost;
    private int salesNum;

    public Product(int menuID, String name, double cost, int salesNum) {
        this.menuID = menuID;
        this.name = name;
        this.cost = cost;
        this.salesNum = salesNum;
    }

    public int getMenuID() { return menuID; }
    public String getName() { return name; }
    public double getCost() { return cost; }
    public int getSalesNum() { return salesNum; }
}
