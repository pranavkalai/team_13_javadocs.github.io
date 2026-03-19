package com.teamMatcha.pos;

/**
 * Represents an X-Report which provides a summary of orders and revenue for a specific hour.
 */
public class XReports 
{
    /** The number of orders in the specified hour. */
    private int orderCount;
    /** The hour for which the report is generated. */
    private int hour;
    /** The total revenue for the hour. */
    private double totalAmount;

    /**
     * Constructs a new XReports object.
     *
     * @param orderCount  The number of orders in the specified hour.
     * @param hour        The hour for which the report is generated.
     * @param totalAmount The total revenue for the hour.
     */
    public XReports(int orderCount, int hour, double totalAmount) {
        this.orderCount = orderCount;
        this.hour = hour;
        this.totalAmount = totalAmount;
    }
    
    /**
     * Gets the order count for the report.
     *
     * @return The order count.
     */
    public int getOrderCount() {
        return orderCount;
    }

    /**
     * Gets the hour for which the report was generated.
     *
     * @return The hour.
     */
    public int getHour() {
        return hour;
    }

    /**
     * Gets the total revenue amount for the report.
     *
     * @return The total amount.
     */
    public double getTotalAmount() {
        return totalAmount;
    }
}
