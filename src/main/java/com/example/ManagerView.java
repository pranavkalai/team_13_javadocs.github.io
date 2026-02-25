package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class ManagerView {
    private final String BORDER = "-fx-border-color: black; -fx-border-width: 1; -fx-background-radius: 0; -fx-border-radius: 0;";
    private StackPane displayArea = new StackPane();
    
    // Moving this inside createTeamTab or handling it via a refresh method is cleaner
    private VBox tableRowsContainer = new VBox();

    public VBox getView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Label title = new Label("Manager Console");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 22;");

        // Tab Navigation
        HBox tabs = new HBox();
        Button menuBtn = createTab("Menu");
        Button trendsBtn = createTab("Trends");
        Button stockBtn = createTab("Stock");
        Button teamBtn = createTab("Team");

        menuBtn.setOnAction(e -> {
            updateTabStyle(menuBtn, trendsBtn, stockBtn, teamBtn);
            displayArea.getChildren().setAll(createMenuTab());
        });

        trendsBtn.setOnAction(e -> {
            updateTabStyle(trendsBtn, menuBtn, stockBtn, teamBtn);
            displayArea.getChildren().setAll(createTrendsTab());
        });
        
        stockBtn.setOnAction(e -> {
            updateTabStyle(stockBtn, menuBtn, trendsBtn, teamBtn);
            displayArea.getChildren().setAll(createStockTab());
        });
        
        teamBtn.setOnAction(e -> {
            updateTabStyle(teamBtn, menuBtn, trendsBtn, stockBtn);
            displayArea.getChildren().setAll(createTeamTab());
        });

        tabs.getChildren().addAll(menuBtn, trendsBtn, stockBtn, teamBtn);
        
        // Default to Menu
        menuBtn.fire();

        layout.getChildren().addAll(title, tabs, displayArea);
        return layout;
    }

    private void updateTabStyle(Button active, Button... others) {
        active.setStyle(BORDER + "-fx-background-color: black; -fx-text-fill: white;");
        for (Button button : others) {
            button.setStyle(BORDER + "-fx-background-color: white; -fx-text-fill: black;");
        }
    }

    // --- MENU TAB ---
    private VBox createMenuTab() {
        VBox menuLayout = new VBox(10);

        HBox header = new HBox();
        Label menuTitle = new Label("Menu Management");
        menuTitle.setStyle("-fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("Add Menu Item");
        addBtn.setStyle(BORDER);
        addBtn.setOnAction(e -> showAddMenuItemDialog());
        header.getChildren().addAll(menuTitle, spacer, addBtn);

        HBox tableHead = new HBox();
        tableHead.setStyle("-fx-background-color: #eee; " + BORDER);
        tableHead.getChildren().addAll(
            createHeaderCell("NAME", 220),
            createHeaderCell("PRICE", 120),
            createHeaderCell("ACTIONS", 200)
        );

        VBox rowsContainer = new VBox();
        List<Product> products = Database.getAllProducts();
        for (Product product : products) {
            rowsContainer.getChildren().add(createMenuRow(product));
        }

        ScrollPane scrollPane = new ScrollPane(rowsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: transparent;");
        scrollPane.setPrefHeight(520);

        menuLayout.getChildren().addAll(header, tableHead, scrollPane);
        return menuLayout;
    }

    private HBox createMenuRow(Product product) {
        HBox row = new HBox();
        row.setStyle("-fx-border-color: transparent transparent black transparent; -fx-padding: 5;");

        Label name = createDataCell(product.getName(), 220);
        Label price = createDataCell("$" + String.format("%.2f", product.getCost()), 120);

        Button editBtn = new Button("Edit");
        editBtn.setStyle(BORDER + "-fx-background-color: white;");
        editBtn.setPrefWidth(180);
        editBtn.setOnAction(e -> showEditMenuItemDialog(product));

        row.getChildren().addAll(name, price, editBtn);
        return row;
    }

    private void showAddMenuItemDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Menu Item");

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));
        form.setStyle(BORDER + "-fx-background-color: white;");

        TextField nameField = new TextField();
        nameField.setPromptText("Item Name");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        nameField.setStyle(BORDER);
        priceField.setStyle(BORDER);

        Label feedback = new Label();
        feedback.setStyle("-fx-text-fill: red;");

        Button confirm = new Button("Confirm");
        confirm.setStyle(BORDER);
        confirm.setMaxWidth(Double.MAX_VALUE);
        confirm.setOnAction(e -> {
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            if (name.isEmpty() || priceText.isEmpty()) {
                feedback.setText("All fields are required.");
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                if (price < 0) {
                    feedback.setText("Price must be non-negative.");
                    return;
                }

                System.out.println("[FRONTEND] Manager requested add menu item: name=\"" + name + "\", price=$" + String.format("%.2f", price));
                boolean inserted = Database.addMenuItem(name, price);
                if (inserted) {
                    System.out.println("[BACKEND][LOG] Menu item added successfully.");
                    displayArea.getChildren().setAll(createMenuTab());
                    dialog.close();
                } else {
                    feedback.setText("Failed to add menu item.");
                    System.out.println("[BACKEND][LOG] Failed to add menu item.");
                }
            } catch (NumberFormatException ex) {
                feedback.setText("Price must be a number.");
            }
        });

        form.getChildren().addAll(new Label("New Menu Item"), nameField, priceField, confirm, feedback);
        dialog.setScene(new Scene(form, 320, 280));
        dialog.show();
    }

    private void showEditMenuItemDialog(Product product) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Update Menu Item");

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));
        form.setStyle(BORDER + "-fx-background-color: white;");

        TextField nameField = new TextField(product.getName());
        TextField priceField = new TextField(String.format("%.2f", product.getCost()));
        nameField.setStyle(BORDER);
        priceField.setStyle(BORDER);

        Label feedback = new Label();
        feedback.setStyle("-fx-text-fill: red;");

        Button confirm = new Button("Update");
        confirm.setStyle(BORDER);
        confirm.setMaxWidth(Double.MAX_VALUE);
        confirm.setOnAction(e -> {
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            if (name.isEmpty() || priceText.isEmpty()) {
                feedback.setText("All fields are required.");
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                if (price < 0) {
                    feedback.setText("Price must be non-negative.");
                    return;
                }

                System.out.println("[FRONTEND] Manager requested update for menuID=" + product.getMenuID()
                        + " -> name=\"" + name + "\", price=" + String.format("%.2f", price));
                boolean updated = Database.updateMenuItem(product.getMenuID(), name, price);
                if (updated) {
                    System.out.println("[BACKEND][LOG] Menu item updated for menuID=" + product.getMenuID() + ".");
                    displayArea.getChildren().setAll(createMenuTab());
                    dialog.close();
                } else {
                    feedback.setText("Failed to update menu item.");
                    System.out.println("[BACKEND][LOG] Failed to update menu item for menuID=" + product.getMenuID() + ".");
                }
            } catch (NumberFormatException ex) {
                feedback.setText("Price must be a number.");
            }
        });

        Button deleteBtn = new Button("Delete Item");
        deleteBtn.setStyle(BORDER + "-fx-background-color: white;");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setOnAction(e -> {
            System.out.println("[FRONTEND] Manager requested delete for menuID=" + product.getMenuID() + " (" + product.getName() + ").");
            Alert confirmDelete = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Delete \"" + product.getName() + "\" from the menu?",
                    ButtonType.YES,
                    ButtonType.NO
            );
            confirmDelete.setTitle("Confirm Delete");
            confirmDelete.setHeaderText("This action cannot be undone.");
            confirmDelete.initOwner(dialog);

            ButtonType result = confirmDelete.showAndWait().orElse(ButtonType.NO);
            if (result == ButtonType.YES) {
                boolean deleted = Database.deleteMenuItem(product.getMenuID());
                if (deleted) {
                    System.out.println("[BACKEND][LOG] Menu item deleted for menuID=" + product.getMenuID() + ".");
                    displayArea.getChildren().setAll(createMenuTab());
                    dialog.close();
                } else {
                    feedback.setText("Failed to delete menu item.");
                    System.out.println("[BACKEND][LOG] Failed to delete menu item for menuID=" + product.getMenuID() + ".");
                }
            } else {
                System.out.println("[LOG] Delete canceled by manager for menuID=" + product.getMenuID() + ".");
            }
        });

        form.getChildren().addAll(new Label("Edit Menu Item"), nameField, priceField, confirm, deleteBtn, feedback);
        dialog.setScene(new Scene(form, 320, 320));
        dialog.show();
    }

    // --- TRENDS TAB ---
    private HBox createTrendsTab() {
        HBox trends = new HBox(20);
        trends.getChildren().addAll(createTrendCard("Sales History"), createTrendCard("Popular Items"));
        return trends;
    }

    private VBox createTrendCard(String trendName) {
        VBox card = new VBox();
        card.setPrefWidth(400);
        card.setStyle(BORDER);
        
        Label header = new Label(trendName.toUpperCase());
        header.setStyle("-fx-font-size: 10; -fx-padding: 5; -fx-border-color: transparent transparent black transparent;");
        
        StackPane placeholder = new StackPane(new Line(0,0,400,200), new Line(400,0,0,200));
        placeholder.setPrefHeight(200);
        
        card.getChildren().addAll(header, placeholder);
        return card;
    }

    // --- STOCK TAB ---
    private GridPane createStockTab() {
        GridPane stockGrid = new GridPane();
        stockGrid.setHgap(10); stockGrid.setVgap(10);
        
        List<InventoryItem> inventory = Database.getAllInventory();
        int row = 0;
        int col = 0;
        for (InventoryItem item : inventory) {
            stockGrid.add(createStockCell(item.getName(), item.getInventoryNum() + " units"), col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
        
        return stockGrid;
    }

    private VBox createStockCell(String item, String qty) {
        VBox cell = new VBox(10);
        cell.setPadding(new Insets(15));
        cell.setStyle(BORDER);
        cell.setAlignment(Pos.CENTER);
        
        Label name = new Label(item);
        Label amount = new Label(qty);
        amount.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        
        Button restock = new Button("Restock");
        restock.setStyle(BORDER + "-fx-background-color: white;");
        restock.setOnAction(e -> {
            BackendController.handleRestock(item);
            amount.setText("100 units"); // Visual feedback for frontend-first
        });
        
        cell.getChildren().addAll(name, amount, restock);
        return cell;
    }

    // --- TEAM TAB ---
    private VBox createTeamTab() {
        VBox teamLayout = new VBox(10);
        tableRowsContainer.getChildren().clear();
        
        HBox header = new HBox();
        Label teamTitle = new Label("Team Roster");
        teamTitle.setStyle("-fx-font-weight: bold;");
        Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
        
        Button addBtn = new Button("Add Employee");
        addBtn.setStyle(BORDER);
        addBtn.setOnAction(e -> showAddEmployeeDialog());
        
        header.getChildren().addAll(teamTitle, s, addBtn);
        
        HBox tableHead = new HBox();
        tableHead.setStyle("-fx-background-color: #eee; " + BORDER);
        tableHead.getChildren().addAll(
            createHeaderCell("NAME", 150), createHeaderCell("ROLE", 100),
            createHeaderCell("PAY RATE", 100), createHeaderCell("ORDERS", 100),
            createHeaderCell("ACTIONS", 100)
        );

        refreshTeamTable();

        teamLayout.getChildren().addAll(header, tableHead, tableRowsContainer);
        return teamLayout;
    }

    private void refreshTeamTable() {
        tableRowsContainer.getChildren().clear();
        List<Employee> employees = Database.getAllEmployees();
        for (Employee emp : employees) {
            addEmployeeRow(emp);
        }
    }

    private void showAddEmployeeDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Employee");

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));
        form.setStyle(BORDER + "-fx-background-color: white;");

        TextField n = new TextField(); n.setPromptText("Name");
        TextField r = new TextField(); r.setPromptText("Role");
        TextField p = new TextField(); p.setPromptText("Pay");
        n.setStyle(BORDER); r.setStyle(BORDER); p.setStyle(BORDER);

        Button confirm = new Button("Confirm");
        confirm.setStyle(BORDER);
        confirm.setMaxWidth(Double.MAX_VALUE);
        
        confirm.setOnAction(e -> {
            if (BackendController.validateEmployeeInput(n.getText(), r.getText(), p.getText(), "0")) {
                BackendController.handleAddEmployee(n.getText(), r.getText(), p.getText());
                refreshTeamTable();
                dialog.close();
            } else {
                confirm.setText("Invalid Input!");
                confirm.setStyle(BORDER + "-fx-text-fill: red;");
            }
        });

        form.getChildren().addAll(new Label("New Employee Details"), n, r, p, confirm);
        dialog.setScene(new Scene(form, 300, 320));
        dialog.show();
    }

    private void showEditEmployeeDialog(Employee emp) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Employee");

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));
        form.setStyle(BORDER + "-fx-background-color: white;");

        TextField n = new TextField(emp.getName());
        TextField r = new TextField(emp.getJob());
        TextField p = new TextField(String.valueOf(emp.getPay()));
        n.setStyle(BORDER); r.setStyle(BORDER); p.setStyle(BORDER);

        Button confirm = new Button("Save Changes");
        confirm.setStyle(BORDER);
        confirm.setMaxWidth(Double.MAX_VALUE);
        
        confirm.setOnAction(e -> {
            if (BackendController.validateEmployeeInput(n.getText(), r.getText(), p.getText(), "0")) {
                BackendController.handleUpdateEmployee(emp.getEmployeeID(), n.getText(), r.getText(), p.getText());
                refreshTeamTable();
                dialog.close();
            } else {
                confirm.setText("Invalid Input!");
                confirm.setStyle(BORDER + "-fx-text-fill: red;");
            }
        });

        Button removeBtn = new Button("REMOVE EMPLOYEE");
        removeBtn.setStyle(BORDER + "-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");
        removeBtn.setMaxWidth(Double.MAX_VALUE);
        removeBtn.setOnAction(e -> {
            BackendController.handleRemoveEmployee(emp.getEmployeeID());
            refreshTeamTable();
            dialog.close();
        });

        form.getChildren().addAll(new Label("Edit Employee Details"), n, r, p, confirm, new Region(), removeBtn);
        dialog.setScene(new Scene(form, 300, 380));
        dialog.show();
    }

    private void addEmployeeRow(Employee emp) {
        HBox row = new HBox();
        row.setStyle("-fx-border-color: transparent transparent black transparent; -fx-padding: 5;");
        
        Button editBtn = new Button("Edit");
        editBtn.setStyle(BORDER + "-fx-font-size: 10;");
        editBtn.setOnAction(e -> showEditEmployeeDialog(emp));

        row.getChildren().addAll(
            createDataCell(emp.getName(), 150), createDataCell(emp.getJob(), 100),
            createDataCell("$" + emp.getPay() + "/hr", 100), createDataCell(String.valueOf(emp.getOrderNum()), 100),
            new StackPane(editBtn)
        );
        tableRowsContainer.getChildren().add(row);
    }

    private Label createHeaderCell(String t, double w) {
        Label l = new Label(t); l.setPrefWidth(w);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 10; -fx-padding: 5;");
        return l;
    }

    private Label createDataCell(String t, double w) {
        Label l = new Label(t); l.setPrefWidth(w); l.setStyle("-fx-padding: 5;");
        return l;
    }

    private Button createTab(String text) {
        Button b = new Button(text);
        b.setPrefWidth(100);
        b.setStyle(BORDER + "-fx-background-color: white;");
        return b;
    }
}
