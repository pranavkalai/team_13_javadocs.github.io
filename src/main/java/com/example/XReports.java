public class XReports 
{
    private int orderCount;
    private int hour;
    private double totalAmount;

    public XReports(int orderCount, int hour, double totalAmount) {
        this.orderCount = orderCount;
        this.hour = hour;
        this.totalAmount = totalAmount;
    }
    
    public int getOrderCount() {
        return orderCount;
    }

    public int getHour() {
        return hour;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
