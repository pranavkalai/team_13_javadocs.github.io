package com.example;

public class Employee {
    private int employeeID;
    private String name;
    private double pay;
    private String job;
    private int orderNum;

    public Employee(int employeeID, String name, double pay, String job, int orderNum) {
        this.employeeID = employeeID;
        this.name = name;
        this.pay = pay;
        this.job = job;
        this.orderNum = orderNum;
    }

    public int getEmployeeID() { return employeeID; }
    public String getName() { return name; }
    public double getPay() { return pay; }
    public String getJob() { return job; }
    public int getOrderNum() { return orderNum; }
}
