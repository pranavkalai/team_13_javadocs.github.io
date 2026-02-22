package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        TabPane mainTabs = new TabPane();

        // 1. CASHIER VIEW (For 'orders' and 'order_items')
        Tab cashierTab = new Tab("Cashier View");
        cashierTab.setClosable(false);
        cashierTab.setContent(createCashierView());

        // 2. MANAGER VIEW (For 'inventory', 'menu', 'employees')
        Tab managerTab = new Tab("Manager View");
        managerTab.setClosable(false);
        managerTab.setContent(createManagerView());

        mainTabs.getTabs().addAll(cashierTab, managerTab);

        Scene scene = new Scene(mainTabs, 1100, 800);
        stage.setTitle("Boba Shop POS - Team 13 ");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createCashierView() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label title = new Label("Place New Order");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Customer Info (Maps to orders.customerName)
        TextField customerField = new TextField();
        customerField.setPromptText("Customer Name");

        // Order Specifics (Maps to order_items)
        HBox orderConfig = new HBox(20);
        
        VBox drinkCol = new VBox(10, new Label("Select Menu Item:"));
        ListView<String> drinkList = new ListView<>();
        drinkList.getItems().addAll("Classic Milk Tea", "Matcha Latte", "Taro Milk Tea");
        drinkCol.getChildren().add(drinkList);

        VBox customCol = new VBox(10);
        
        ComboBox<String> iceBox = new ComboBox<>();
        iceBox.getItems().addAll("Regular Ice", "Less Ice", "No Ice");
        iceBox.setPromptText("Ice Level");

        ComboBox<String> sugarBox = new ComboBox<>();
        sugarBox.getItems().addAll("100%", "75%", "50%", "25%", "0%");
        sugarBox.setPromptText("Sugar Level");

        TextField toppingField = new TextField();
        toppingField.setPromptText("Topping (e.g., Pearls)");

        customCol.getChildren().addAll(new Label("Customization:"), iceBox, sugarBox, toppingField);
        orderConfig.getChildren().addAll(drinkCol, customCol);

        Button submitBtn = new Button("Submit Order");
        submitBtn.setStyle("-fx-background-color: #500000; -fx-text-fill: white; -fx-font-weight: bold;");
        submitBtn.setOnAction(e -> {
            System.out.println("[LOG] Order created for: " + customerField.getText());
            System.out.println("[LOG] Item: " + drinkList.getSelectionModel().getSelectedItem() + 
                               " | Ice: " + iceBox.getValue() + " | Sugar: " + sugarBox.getValue());
        });

        layout.getChildren().addAll(title, customerField, orderConfig, submitBtn);
        return layout;
    }

    private TabPane createManagerView() {
        TabPane adminTabs = new TabPane();
        adminTabs.setSide(Side.LEFT);

        // Map tabs to your SQL Tables
        adminTabs.getTabs().add(new Tab("Inventory", createTablePanel("ID", "Name", "Cost", "Quantity")));
        adminTabs.getTabs().add(new Tab("Menu", createTablePanel("ID", "Drink Name", "Cost", "Sales Num")));
        adminTabs.getTabs().add(new Tab("Employees", createTablePanel("ID", "Name", "Pay", "Job")));
        
        // Reports (Day-to-Day)
        VBox reportBox = new VBox(15, new Label("Management Reports"), 
                                  new Button("Generate X-Report"), 
                                  new Button("Generate Z-Report"),
                                  new TextArea("Report data will pull from 'orders' and 'order_items'..."));
        reportBox.setPadding(new Insets(20));
        Tab reportTab = new Tab("Reports");
        reportTab.setContent(reportBox);
        adminTabs.getTabs().add(reportTab);

        return adminTabs;
    }

    // Helper to create CRUD panels matching your schema columns
    private VBox createTablePanel(String... cols) {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));

        TableView<String[]> table = new TableView<>();
        for (String colName : cols) {
            table.getColumns().add(new TableColumn<>(colName));
        }
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox inputBar = new HBox(10);
        TextField f1 = new TextField(); f1.setPromptText(cols[0]);
        TextField f2 = new TextField(); f2.setPromptText(cols[1]);
        Button addBtn = new Button("Add/Update");
        
        addBtn.setOnAction(e -> System.out.println("[LOG] Manager modified record: " + f2.getText()));

        inputBar.getChildren().addAll(f1, f2, addBtn);
        container.getChildren().addAll(new Label("Database Table: " + cols[1]), table, inputBar);
        return container;
    }

    public static void main(String[] args) {
        launch(args);
    }
}