package com.example;

/**
 * Represents an employee in the system.
 */
public class Employee {
    /** The unique ID of the employee. */
    private int employeeID;
    /** The name of the employee. */
    private String name;
    /** The pay rate of the employee. */
    private double pay;
    /** The job title of the employee. */
    private String job;
    /** The number of orders processed by the employee. */
    private int orderNum;

    /**
     * Constructs a new Employee with the specified details.
     *
     * @param employeeID The unique ID of the employee.
     * @param name       The name of the employee.
     * @param pay        The pay rate of the employee.
     * @param job        The job title of the employee.
     * @param orderNum   The number of orders processed by the employee.
     */
    public Employee(int employeeID, String name, double pay, String job, int orderNum) {
        this.employeeID = employeeID;
        this.name = name;
        this.pay = pay;
        this.job = job;
        this.orderNum = orderNum;
    }

    /**
     * Gets the employee's ID.
     *
     * @return The employee ID.
     */
    public int getEmployeeID() { return employeeID; }

    /**
     * Gets the name of the employee.
     *
     * @return The name.
     */
    public String getName() { return name; }

    /**
     * Gets the pay rate of the employee.
     *
     * @return The pay.
     */
    public double getPay() { return pay; }

    /**
     * Gets the job title of the employee.
     *
     * @return The job title.
     */
    public String getJob() { return job; }

    /**
     * Gets the number of orders processed by the employee.
     *
     * @return The order number.
     */
    public int getOrderNum() { return orderNum; }
}
