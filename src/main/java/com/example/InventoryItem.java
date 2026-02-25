package com.example;

public class InventoryItem {
    private int inventoryID;
    private String name;
    private double cost;
    private int inventoryNum;
    private int useAverage;

    public InventoryItem(int inventoryID, String name, double cost, int inventoryNum, int useAverage) {
        this.inventoryID = inventoryID;
        this.name = name;
        this.cost = cost;
        this.inventoryNum = inventoryNum;
        this.useAverage = useAverage;
    }

    public int getInventoryID() { return inventoryID; }
    public String getName() { return name; }
    public double getCost() { return cost; }
    public int getInventoryNum() { return inventoryNum; }
    public int getUseAverage() { return useAverage; }
}
