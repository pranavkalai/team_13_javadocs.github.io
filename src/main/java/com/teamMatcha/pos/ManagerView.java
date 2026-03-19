package com.teamMatcha.pos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Provides the user interface for the Manager view.
 * Allows managers to manage the menu, view trends, manage stock, and view team reports.
 */
public class ManagerView {
    /** The border style for UI components. */
    private final String BORDER = "-fx-border-color: black; -fx-border-width: 1; -fx-background-radius: 0; -fx-border-radius: 0;";
    /** The main display area for tab content. */
    private final StackPane displayArea = new StackPane();
    /** The container for table rows in the team view. */
    private final VBox tableRowsContainer = new VBox();

    /**
     * Helper class to track ingredient selection and quantity in dialogs.
     */
    private static class IngredientSelection {
        /** The inventory item being selected. */
        private final InventoryItem item;
        /** The checkbox indicating if the item is selected. */
        private final CheckBox selected;
        /** The text field for the quantity of the item. */
        private final TextField qtyField;

        /**
         * Constructs an IngredientSelection object.
         *
         * @param item      The inventory item.
         * @param selected  The checkbox for selection.
         * @param qtyField  The text field for quantity.
         */
        private IngredientSelection(InventoryItem item, CheckBox selected, TextField qtyField) {
            this.item = item;
            this.selected = selected;
            this.qtyField = qtyField;
        }
    }

    /**
     * Constructs and returns the root layout of the Manager view.
     *
     * @return The VBox containing the full Manager view.
     */
    public VBox getView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Label title = new Label("Manager Console");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 22;");

        HBox tabs = new HBox();
        Button menuBtn = createTab("Menu");
        Button trendsBtn = createTab("Trends");
        Button stockBtn = createTab("Stock");
        Button teamBtn = createTab("Team");
        Button xBtn = createTab("X Reports");
        Button zBtn = createTab("Z Reports");

        menuBtn.setOnAction(e -> {
            updateTabStyle(menuBtn, trendsBtn, stockBtn, teamBtn, xBtn, zBtn);
            displayArea.getChildren().setAll(createMenuTab());
        });

        trendsBtn.setOnAction(e -> {
            updateTabStyle(trendsBtn, menuBtn, stockBtn, teamBtn, xBtn, zBtn);
            displayArea.getChildren().setAll(createTrendsTab());
        });

        stockBtn.setOnAction(e -> {
            updateTabStyle(stockBtn, menuBtn, trendsBtn, teamBtn, xBtn, zBtn);
            displayArea.getChildren().setAll(createStockTab());
        });

        teamBtn.setOnAction(e -> {
            updateTabStyle(teamBtn, menuBtn, trendsBtn, stockBtn, xBtn, zBtn);
            displayArea.getChildren().setAll(createTeamTab());
        });
        xBtn.setOnAction(e -> {
            updateTabStyle(xBtn, menuBtn, trendsBtn, stockBtn, teamBtn, zBtn);
            displayArea.getChildren().setAll(createXReportsTab());
        });
        zBtn.setOnAction(e -> {
            updateTabStyle(zBtn, menuBtn, trendsBtn, stockBtn, teamBtn, xBtn);
            displayArea.getChildren().setAll(createZReportsTab());
        });

        tabs.getChildren().addAll(menuBtn, trendsBtn, stockBtn, teamBtn, xBtn, zBtn);
        menuBtn.fire();

        displayArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(displayArea, Priority.ALWAYS);
        layout.getChildren().addAll(title, tabs, displayArea);
        return layout;
    }

    /**
     * Updates the style of the tabs to highlight the active one.
     *
     * @param active The active tab button.
     * @param others The other tab buttons.
     */
    private void updateTabStyle(Button active, Button... others) {
        active.setStyle(BORDER + "-fx-background-color: black; -fx-text-fill: white;");
        for (Button b : others) {
            b.setStyle(BORDER + "-fx-background-color: white; -fx-text-fill: black;");
        }
    }

    // --- MENU TAB ---

    /**
     * Creates the layout for the Menu management tab.
     *
     * @return The VBox containing the menu management UI.
     */
    private VBox createMenuTab() {
        VBox menuLayout = new VBox(10);
        menuLayout.setFillWidth(true);
        menuLayout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

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
                createHeaderCell("ACTIONS", 200));

        VBox rowsContainer = new VBox();
        List<Product> products = Database.getAllProducts();
        for (Product product : products) {
            rowsContainer.getChildren().add(createMenuRow(product));
        }

        ScrollPane scrollPane = new ScrollPane(rowsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: transparent;");
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        menuLayout.getChildren().addAll(header, tableHead, scrollPane);
        return menuLayout;
    }

    /**
     * Creates a row for the menu table representing a product.
     *
     * @param product The product to represent.
     * @return An HBox containing the product details and an "Edit" button.
     */
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

    /**
     * Displays a dialog for adding a new menu item.
     */
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

        Label ingredientsLabel = new Label("Associated Inventory Ingredients");
        ingredientsLabel.setStyle("-fx-font-weight: bold;");

        FlowPane ingredientsFlow = new FlowPane();
        ingredientsFlow.setHgap(10);
        ingredientsFlow.setVgap(10);
        ingredientsFlow.setPrefWrapLength(620);
        List<InventoryItem> inventoryItems = Database.getAllInventory();
        List<IngredientSelection> ingredientSelections = new java.util.ArrayList<>();
        for (InventoryItem inventoryItem : inventoryItems) {
            CheckBox useIngredient = new CheckBox(inventoryItem.getName());
            TextField qtyField = new TextField();
            qtyField.setPromptText("Qty used per drink");
            qtyField.setPrefWidth(140);
            qtyField.setDisable(true);
            qtyField.setStyle(BORDER);

            useIngredient.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                qtyField.setDisable(!isSelected);
                if (!isSelected) {
                    qtyField.clear();
                }
            });

            VBox ingredientCell = new VBox(8, useIngredient, qtyField);
            ingredientCell.setPadding(new Insets(10));
            ingredientCell.setStyle(BORDER + "-fx-background-color: white;");
            ingredientCell.setPrefWidth(200);
            ingredientsFlow.getChildren().add(ingredientCell);
            ingredientSelections.add(new IngredientSelection(inventoryItem, useIngredient, qtyField));
        }

        ScrollPane ingredientsScroll = new ScrollPane(ingredientsFlow);
        ingredientsScroll.setFitToWidth(true);
        ingredientsScroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        ingredientsScroll.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: #ddd;");
        ingredientsScroll.viewportBoundsProperty().addListener(
                (obs, oldVal, newVal) -> ingredientsFlow.setPrefWrapLength(Math.max(220, newVal.getWidth() - 20)));
        VBox.setVgrow(ingredientsScroll, Priority.ALWAYS);

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

                Map<Integer, Integer> ingredientMap = new HashMap<>();
                for (IngredientSelection selection : ingredientSelections) {
                    if (!selection.selected.isSelected()) {
                        continue;
                    }
                    String qtyText = selection.qtyField.getText().trim();
                    if (qtyText.isEmpty()) {
                        feedback.setText("Selected ingredients need a quantity.");
                        return;
                    }
                    int qty = Integer.parseInt(qtyText);
                    if (qty <= 0) {
                        feedback.setText("Ingredient quantities must be > 0.");
                        return;
                    }
                    ingredientMap.put(selection.item.getInventoryID(), qty);
                }

                if (ingredientMap.isEmpty()) {
                    feedback.setText("Select at least one inventory ingredient.");
                    return;
                }

                boolean inserted = Database.addMenuItemWithIngredients(name, price, ingredientMap);
                if (inserted) {
                    displayArea.getChildren().setAll(createMenuTab());
                    dialog.close();
                } else {
                    feedback.setText("Failed to add menu item.");
                }
            } catch (NumberFormatException ex) {
                feedback.setText("Price and ingredient quantities must be numeric.");
            }
        });

        form.getChildren().addAll(
                createBoldLabel("New Menu Item"),
                nameField,
                priceField,
                ingredientsLabel,
                ingredientsScroll,
                confirm,
                feedback);
        form.setFillWidth(true);
        form.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        dialog.setScene(new Scene(form, 680, 620));
        dialog.show();
    }

    /**
     * Displays a dialog for editing an existing menu item.
     *
     * @param product The product to edit.
     */
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

    // Build ingredient checklist with current ingredients pre-checked
    Label ingredientsLabel = new Label("Ingredients");
    ingredientsLabel.setStyle("-fx-font-weight: bold;");

    // Get current ingredients for this product so we can pre-fill
    Map<String, Integer> currentIngredients = new HashMap<>();
    for (Database.MenuIngredientRow row : Database.getMenuItemIngredients(product.getMenuID())) {
        currentIngredients.put(row.getIngredientName(), row.getQuantity());
    }

    FlowPane ingredientsFlow = new FlowPane();
    ingredientsFlow.setHgap(10);
    ingredientsFlow.setVgap(10);
    ingredientsFlow.setPrefWrapLength(620);

    List<IngredientSelection> ingredientSelections = new java.util.ArrayList<>();
    for (InventoryItem item : Database.getAllInventory()) {
        boolean alreadyUsed = currentIngredients.containsKey(item.getName());

        CheckBox useIngredient = new CheckBox(item.getName());
        useIngredient.setSelected(alreadyUsed);

        TextField qtyField = new TextField();
        qtyField.setPromptText("Qty per drink");
        qtyField.setPrefWidth(120);
        qtyField.setStyle(BORDER);
        qtyField.setDisable(!alreadyUsed);
        if (alreadyUsed) {
            qtyField.setText(String.valueOf(currentIngredients.get(item.getName())));
        }

        useIngredient.selectedProperty().addListener((obs, was, isNow) -> {
            qtyField.setDisable(!isNow);
            if (!isNow) qtyField.clear();
        });

        VBox cell = new VBox(5, useIngredient, qtyField);
        cell.setPadding(new Insets(8));
        cell.setStyle(BORDER + "-fx-background-color: white;");
        cell.setPrefWidth(180);
        ingredientsFlow.getChildren().add(cell);
        ingredientSelections.add(new IngredientSelection(item, useIngredient, qtyField));
    }

    ScrollPane ingredientsScroll = new ScrollPane(ingredientsFlow);
    ingredientsScroll.setFitToWidth(true);
    ingredientsScroll.setPrefHeight(250);
    ingredientsScroll.setStyle("-fx-background: white; -fx-background-color: white;");

    Label feedback = new Label();
    feedback.setStyle("-fx-text-fill: red;");

    Button confirm = new Button("Save Changes");
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

            Map<Integer, Integer> ingredientMap = new HashMap<>();
            for (IngredientSelection sel : ingredientSelections) {
                if (!sel.selected.isSelected()) continue;
                String qtyText = sel.qtyField.getText().trim();
                if (qtyText.isEmpty()) {
                    feedback.setText("Selected ingredients need a quantity.");
                    return;
                }
                int qty = Integer.parseInt(qtyText);
                if (qty <= 0) {
                    feedback.setText("Quantities must be greater than 0.");
                    return;
                }
                ingredientMap.put(sel.item.getInventoryID(), qty);
            }

            boolean updatedItem = Database.updateMenuItem(product.getMenuID(), name, price);
            BackendController.setMenuIngredients(product.getMenuID(), ingredientMap);

            if (updatedItem) {
                displayArea.getChildren().setAll(createMenuTab());
                dialog.close();
            } else {
                feedback.setText("Failed to save changes.");
            }
        } catch (NumberFormatException ex) {
            feedback.setText("Price and quantities must be numeric.");
        }
    });

    Button deleteBtn = new Button("Delete Item");
    deleteBtn.setStyle(BORDER + "-fx-background-color: white;");
    deleteBtn.setMaxWidth(Double.MAX_VALUE);
    deleteBtn.setOnAction(e -> {
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + product.getName() + "\" from the menu?",
                ButtonType.YES, ButtonType.NO);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("This action cannot be undone.");
        confirmDelete.initOwner(dialog);
        if (confirmDelete.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            if (Database.deleteMenuItem(product.getMenuID())) {
                displayArea.getChildren().setAll(createMenuTab());
                dialog.close();
            } else {
                feedback.setText("Failed to delete.");
            }
        }
    });

    form.getChildren().addAll(
            new Label("Edit Menu Item"), nameField, priceField,
            ingredientsLabel, ingredientsScroll,
            confirm, deleteBtn, feedback);
    form.setFillWidth(true);
    dialog.setScene(new Scene(form, 680, 580));
    dialog.show();
}

    /**
     * Creates a bold label.
     *
     * @param text The text for the label.
     * @return The created Label.
     */
    private Label createBoldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    // --- TRENDS TAB ---

    /**
     * Creates the layout for the Trends &amp; Analytics tab.
     *
     * @return The VBox containing the trends UI.
     */
    private VBox createTrendsTab() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(10));

        Label title = new Label("Trends & Analytics");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");

        DatePicker startPicker = new DatePicker(LocalDate.now().minusDays(7));
        DatePicker endPicker = new DatePicker(LocalDate.now());

        startPicker.setStyle(BORDER);
        endPicker.setStyle(BORDER);

        Button loadBtn = new Button("Load Reports");
        loadBtn.setStyle(BORDER + "-fx-background-color: white;");

        Label status = new Label();
        status.setStyle("-fx-text-fill: #666;");

        HBox controls = new HBox(10,
                new Label("Start"), startPicker,
                new Label("End"), endPicker,
                loadBtn,
                status);
        controls.setAlignment(Pos.CENTER_LEFT);

        // --- INVENTORY USAGE SECTION ---
        Label invTitle = new Label("Inventory Usage");
        invTitle.setStyle("-fx-font-weight: bold;");

        StackPane invChartPane = new StackPane(new ProgressIndicator());
        invChartPane.setMinHeight(300);
        invChartPane.setStyle(BORDER);

        TableView<Database.InventoryUsageRow> invTable = new TableView<>();
        invTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Database.InventoryUsageRow, String> invCol = new TableColumn<>("Inventory item");
        invCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItem"));
        TableColumn<Database.InventoryUsageRow, Integer> usedCol = new TableColumn<>("Units used");
        usedCol.setCellValueFactory(new PropertyValueFactory<>("unitsUsed"));
        invTable.getColumns().setAll(invCol, usedCol);
        invTable.setPrefHeight(200);

        // --- SALES REPORT SECTION ---
        Label salesTitle = new Label("Sales Report");
        salesTitle.setStyle("-fx-font-weight: bold;");

        StackPane salesChartPane = new StackPane(new ProgressIndicator());
        salesChartPane.setMinHeight(300);
        salesChartPane.setStyle(BORDER);

        TableView<Database.SalesReportRow> salesTable = new TableView<>();
        salesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Database.SalesReportRow, String> itemCol = new TableColumn<>("Menu Item");
        itemCol.setCellValueFactory(new PropertyValueFactory<>("menuItem"));
        TableColumn<Database.SalesReportRow, Integer> qtyCol = new TableColumn<>("Qty Sold");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
        TableColumn<Database.SalesReportRow, Double> revCol = new TableColumn<>("Revenue");
        revCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        salesTable.getColumns().setAll(itemCol, qtyCol, revCol);
        salesTable.setPrefHeight(200);

        VBox content = new VBox(15, invTitle, invChartPane, invTable, new Region(), salesTitle, salesChartPane,
                salesTable);
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Runnable runLoad = () -> {
            LocalDate start = startPicker.getValue();
            LocalDate end = endPicker.getValue();
            if (start == null || end == null) {
                status.setText("Pick both start and end dates.");
                status.setStyle("-fx-text-fill: #b00020;");
                return;
            }
            if (end.isBefore(start)) {
                status.setText("End date must be on/after start date.");
                status.setStyle("-fx-text-fill: #b00020;");
                return;
            }

            status.setText("Loading…");
            status.setStyle("-fx-text-fill: #666;");
            loadBtn.setDisable(true);
            invChartPane.getChildren().setAll(new ProgressIndicator());
            salesChartPane.getChildren().setAll(new ProgressIndicator());

            CompletableFuture<List<Database.InventoryUsageRow>> invFuture = CompletableFuture
                    .supplyAsync(() -> Database.getInventoryUsage(start, end));
            CompletableFuture<List<Database.SalesReportRow>> salesFuture = CompletableFuture
                    .supplyAsync(() -> Database.getSalesReport(start, end));

            invFuture.thenAcceptBoth(salesFuture, (invRows, salesRows) -> Platform.runLater(() -> {
                invTable.setItems(FXCollections.observableArrayList(invRows));
                invChartPane.getChildren().setAll(createInventoryUsageChart(invRows));

                salesTable.setItems(FXCollections.observableArrayList(salesRows));
                salesChartPane.getChildren().setAll(createSalesChart(salesRows));

                status.setText("Loaded reports.");
                status.setStyle("-fx-text-fill: #666;");
                loadBtn.setDisable(false);
            })).exceptionally(ex -> {
                Platform.runLater(() -> {
                    invChartPane.getChildren().setAll(errorPane(ex));
                    salesChartPane.getChildren().setAll(errorPane(ex));
                    status.setText("Failed to load.");
                    status.setStyle("-fx-text-fill: #b00020;");
                    loadBtn.setDisable(false);
                });
                return null;
            });
        };

        loadBtn.setOnAction(e -> runLoad.run());
        runLoad.run();

        layout.getChildren().addAll(title, controls, scroll);
        return layout;
    }

    /**
     * Creates a bar chart for sales revenue.
     *
     * @param rows The sales report data.
     * @return The BarChart node.
     */
    private Node createSalesChart(List<Database.SalesReportRow> rows) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Menu Item");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Revenue ($)");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        XYChart.Series<String, Number> s = new XYChart.Series<>();
        int limit = Math.min(rows.size(), 12);
        for (int i = 0; i < limit; i++) {
            Database.SalesReportRow r = rows.get(i);
            s.getData().add(new XYChart.Data<>(r.getMenuItem(), r.getTotalRevenue()));
        }
        chart.getData().setAll(s);
        return chart;
    }

    /**
     * Creates a bar chart for inventory usage.
     *
     * @param rows The inventory usage data.
     * @return The BarChart node.
     */
    private Node createInventoryUsageChart(List<Database.InventoryUsageRow> rows) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Inventory item");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Units used");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        XYChart.Series<String, Number> s = new XYChart.Series<>();
        int limit = Math.min(rows.size(), 12);
        for (int i = 0; i < limit; i++) {
            Database.InventoryUsageRow r = rows.get(i);
            s.getData().add(new XYChart.Data<>(r.getInventoryItem(), r.getUnitsUsed()));
        }
        chart.getData().setAll(s);
        return chart;
    }

    /**
     * Creates an error pane to display exceptions.
     *
     * @param ex The exception to display.
     * @return A StackPane containing the error message.
     */
    private Node errorPane(Throwable ex) {
        String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
        Label l = new Label("Failed to load trends.\n" + (msg == null ? "" : msg));
        l.setWrapText(true);
        l.setStyle("-fx-text-fill: #b00020;");
        return new StackPane(l);
    }

    // --- STOCK TAB ---

    /**
     * Creates the layout for the Stock management tab.
     *
     * @return The VBox containing the stock management UI.
     */
    private VBox createStockTab() {
        VBox stockLayout = new VBox(10);
        stockLayout.setFillWidth(true);
        stockLayout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        HBox header = new HBox();
        Label stockTitle = new Label("Inventory Management");
        stockTitle.setStyle("-fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("Add Inventory Item");
        addBtn.setStyle(BORDER);
        addBtn.setOnAction(e -> showAddInventoryDialog());
        header.getChildren().addAll(stockTitle, spacer, addBtn);

        GridPane stockGrid = new GridPane();
        stockGrid.setHgap(15);
        stockGrid.setVgap(15);
        stockGrid.setPadding(new Insets(10));

        List<InventoryItem> inventory = Database.getAllInventory();
        int row = 0;
        int col = 0;
        for (InventoryItem item : inventory) {
            stockGrid.add(createStockCell(item), col, row);
            col++;
            if (col > 3) {
                col = 0;
                row++;
            }
        }

        ScrollPane scrollPane = new ScrollPane(stockGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: transparent;");
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        stockLayout.getChildren().addAll(header, scrollPane);
        return stockLayout;
    }

    /**
     * Creates a cell representing an inventory item.
     *
     * @param item The inventory item.
     * @return A VBox representing the item.
     */
    private VBox createStockCell(InventoryItem item) {
        VBox cell = new VBox(10);
        cell.setPadding(new Insets(15));
        cell.setStyle(BORDER);
        cell.setAlignment(Pos.CENTER);
        cell.setPrefWidth(200);

        if (item.getInventoryNum() < 50) {
            cell.setStyle(BORDER + "-fx-background-color: #ffebee;");
        }

        Label name = new Label(item.getName());
        name.setStyle("-fx-font-weight: bold;");
        Label amount = new Label(item.getInventoryNum() + " units");
        amount.setStyle("-fx-font-size: 16;");

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);

        Button restock = new Button("Restock");
        restock.setStyle(BORDER + "-fx-background-color: white; -fx-font-size: 10;");
        restock.setOnAction(e -> {
            BackendController.handleRestock(item.getName());
            displayArea.getChildren().setAll(createStockTab());
        });

        Button edit = new Button("Edit");
        edit.setStyle(BORDER + "-fx-background-color: white; -fx-font-size: 10;");
        edit.setOnAction(e -> showEditInventoryDialog(item));

        actions.getChildren().addAll(restock, edit);
        cell.getChildren().addAll(name, amount, actions);
        return cell;
    }

    /**
     * Displays a dialog for editing an inventory item.
     *
     * @param item The inventory item to edit.
     */
    private void showEditInventoryDialog(InventoryItem item) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Inventory Item");

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));
        form.setStyle(BORDER + "-fx-background-color: white;");

        TextField nameField = new TextField(item.getName());
        TextField priceField = new TextField(String.valueOf(item.getCost()));
        TextField qtyField = new TextField(String.valueOf(item.getInventoryNum()));
        TextField avgField = new TextField(String.valueOf(item.getUseAverage()));

        Label feedback = new Label();
        feedback.setStyle("-fx-text-fill: red;");

        Button confirm = new Button("Save Changes");
        confirm.setStyle(BORDER);
        confirm.setMaxWidth(Double.MAX_VALUE);
        confirm.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                double cost = Double.parseDouble(priceField.getText());
                int qty = Integer.parseInt(qtyField.getText());
                int avg = Integer.parseInt(avgField.getText());

                boolean ok = Database.updateInventoryItem(item.getInventoryID(), name, cost, qty, avg);
                if (ok) {
                    displayArea.getChildren().setAll(createStockTab());
                    dialog.close();
                } else {
                    feedback.setText("Update failed.");
                }
            } catch (NumberFormatException ex) {
                feedback.setText("Invalid number format.");
            }
        });

        Button deleteBtn = new Button("Delete Item");
        deleteBtn.setStyle(BORDER + "-fx-background-color: white;");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setOnAction(e -> {
            Alert confirmDelete = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Delete \"" + item.getName() + "\" from inventory?",
                    ButtonType.YES,
                    ButtonType.NO);
            confirmDelete.setTitle("Confirm Delete");
            confirmDelete.setHeaderText("This action cannot be undone.");
            confirmDelete.initOwner(dialog);

            ButtonType result = confirmDelete.showAndWait().orElse(ButtonType.NO);
            if (result == ButtonType.YES) {
                boolean ok = Database.deleteInventoryItem(item.getInventoryID());
                if (ok) {
                    displayArea.getChildren().setAll(createStockTab());
                    dialog.close();
                } else {
                    feedback.setText("Delete failed.");
                }
            }
        });

        form.getChildren().addAll(
                new Label("Item Name"), nameField,
                new Label("Cost Per Unit"), priceField,
                new Label("Current Quantity"), qtyField,
                new Label("Usage Average"), avgField,
                confirm, deleteBtn, feedback);
        dialog.setScene(new Scene(form, 300, 450));
        dialog.show();
    }

    /**
     * Displays a dialog for adding a new inventory item.
     */
    private void showAddInventoryDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Inventory Item");

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));
        form.setStyle(BORDER + "-fx-background-color: white;");

        TextField nameField = new TextField();
        nameField.setPromptText("Item Name");
        TextField priceField = new TextField();
        priceField.setPromptText("Cost Per Unit");
        TextField qtyField = new TextField();
        qtyField.setPromptText("Current Quantity");
        TextField avgField = new TextField();
        avgField.setPromptText("Usage Average");

        Label feedback = new Label();
        feedback.setStyle("-fx-text-fill: red;");

        Button confirm = new Button("Add Item");
        confirm.setStyle(BORDER);
        confirm.setMaxWidth(Double.MAX_VALUE);
        confirm.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                double cost = Double.parseDouble(priceField.getText().trim());
                int qty = Integer.parseInt(qtyField.getText().trim());
                int avg = Integer.parseInt(avgField.getText().trim());

                if (name.isEmpty()) {
                    feedback.setText("Item name is required.");
                    return;
                }
                if (cost < 0 || qty < 0 || avg < 0) {
                    feedback.setText("Values must be non-negative.");
                    return;
                }

                boolean ok = Database.addInventoryItem(name, cost, qty, avg);
                if (ok) {
                    displayArea.getChildren().setAll(createStockTab());
                    dialog.close();
                } else {
                    feedback.setText("Add failed.");
                }
            } catch (NumberFormatException ex) {
                feedback.setText("Invalid number format.");
            }
        });

        form.getChildren().addAll(
                new Label("Item Name"), nameField,
                new Label("Cost Per Unit"), priceField,
                new Label("Current Quantity"), qtyField,
                new Label("Usage Average"), avgField,
                confirm, feedback);
        dialog.setScene(new Scene(form, 300, 450));
        dialog.show();
    }

    // --- TEAM TAB ---

    /**
     * Creates the layout for the Team management tab.
     *
     * @return The VBox containing the team management UI.
     */
    private VBox createTeamTab() {
        VBox teamLayout = new VBox(10);
        tableRowsContainer.getChildren().clear();

        HBox header = new HBox();
        Label teamTitle = new Label("Team Roster");
        teamTitle.setStyle("-fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("Add Employee");
        addBtn.setStyle(BORDER);
        addBtn.setOnAction(e -> showAddEmployeeDialog());

        header.getChildren().addAll(teamTitle, spacer, addBtn);

        HBox tableHead = new HBox();
        tableHead.setStyle("-fx-background-color: #eee; " + BORDER);
        tableHead.getChildren().addAll(
                createHeaderCell("NAME", 150), createHeaderCell("ROLE", 100),
                createHeaderCell("PAY RATE", 100), createHeaderCell("ORDERS", 100),
                createHeaderCell("ACTIONS", 100));

        refreshTeamTable();

        teamLayout.getChildren().addAll(header, tableHead, tableRowsContainer);
        return teamLayout;
    }

    /**
     * Refreshes the team roster table from the database.
     */
    private void refreshTeamTable() {
        tableRowsContainer.getChildren().clear();
        List<Employee> employees = Database.getAllEmployees();
        for (Employee emp : employees) {
            addEmployeeRow(emp);
        }
    }

    /**
     * Creates the layout for the X-Reports tab.
     *
     * @return The VBox containing the X-Reports UI.
     */
    private VBox createXReportsTab() {
        VBox container = new VBox(0);
        container.setStyle("-fx-background-color: white;");

        // Date picker controls
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle(BORDER);
        Button loadBtn = new Button("Load");
        loadBtn.setStyle(BORDER + "-fx-background-color: white;");

        HBox controls = new HBox(10, new Label("Date:"), datePicker, loadBtn);
        controls.setStyle("-fx-padding: 10;");
        container.getChildren().add(controls);

        VBox reportArea = new VBox(0);
        container.getChildren().add(reportArea);

        Runnable loadReport = () -> {
            reportArea.getChildren().clear();

            List<XReports> report = BackendController.getXReports(datePicker.getValue());

            Label header = new Label("X-Report  —  " + datePicker.getValue());
            header.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 0 10 10;");

            HBox titles = new HBox();
            titles.setStyle("-fx-border-color: transparent transparent black transparent; -fx-padding: 5 10;");
            Label tHour = new Label("HOUR");
            tHour.setPrefWidth(150);
            Label tSales = new Label("TOTAL SALES");
            tSales.setPrefWidth(150);
            Label tCount = new Label("ORDERS");
            tCount.setPrefWidth(150);
            titles.getChildren().addAll(tHour, tSales, tCount);
            reportArea.getChildren().addAll(header, titles);

            for (XReports entry : report) {
                String hourLabel = LocalTime.of(entry.getHour(), 0)
                        .format(DateTimeFormatter.ofPattern("h:mm a"));
                HBox row = new HBox();
                row.setStyle("-fx-border-color: transparent transparent black transparent; -fx-padding: 8 10;");
                Label lHour = new Label(hourLabel);
                lHour.setPrefWidth(150);
                Label lSales = new Label(String.format("$%.2f", entry.getTotalAmount()));
                lSales.setPrefWidth(150);
                Label lCount = new Label(String.valueOf(entry.getOrderCount()));
                lCount.setPrefWidth(150);
                row.getChildren().addAll(lHour, lSales, lCount);
                reportArea.getChildren().add(row);
            }

            if (report.isEmpty()) {
                Label empty = new Label("No sales recorded for this day.");
                empty.setStyle("-fx-padding: 20; -fx-text-fill: gray;");
                reportArea.getChildren().add(empty);
            }
        };

        loadBtn.setOnAction(e -> loadReport.run());
        loadReport.run(); // load today by default

        ScrollPane scroll = new ScrollPane(container);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: transparent;");
        VBox wrapper = new VBox(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return wrapper;
    }

    /**
     * Creates the layout for the Z-Reports tab.
     *
     * @return The VBox containing the Z-Reports UI.
     */
    private VBox createZReportsTab() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: white;");

        Label header = new Label("Z-Report (End of Day Summary)");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");

        VBox reportContent = new VBox(15);
        reportContent.setStyle(BORDER + "-fx-padding: 20;");

        Label totalSalesLabel = new Label("Total Sales Today: $0.00");
        totalSalesLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        VBox empOrdersList = new VBox(5);
        Label empHeader = new Label("Orders per Employee:");
        empHeader.setStyle("-fx-font-weight: bold;");
        empOrdersList.getChildren().add(empHeader);

        Button generateBtn = new Button("Generate Z-Report & Clear Today's Sales");
        generateBtn.setStyle(BORDER + "-fx-background-color: white; -fx-font-weight: bold;");
        generateBtn.setPadding(new Insets(10, 20, 10, 20));

        Runnable refreshZReport = () -> {
            Database.ZReportData data = BackendController.getZReport();
            totalSalesLabel.setText(String.format("Total Sales Today: $%.2f", data.getTotalSales()));
            
            empOrdersList.getChildren().clear();
            empOrdersList.getChildren().add(empHeader);
            for (Database.ZReportRow row : data.getEmployeeOrders()) {
                Label l = new Label(row.getEmployeeName() + ": " + row.getDailyOrders() + " orders");
                empOrdersList.getChildren().add(l);
            }
        };

        generateBtn.setOnAction(e -> {
            if (BackendController.hasZReportRun()) {
                Alert alreadyRun = new Alert(Alert.AlertType.WARNING,
                    "Z-Report has already been run today.");
                alreadyRun.showAndWait();
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
                "Are you sure you want to generate the Z-Report and CLEAR all daily sales? This should only be done once at the end of the day.",
                ButtonType.YES, ButtonType.NO);
            alert.setGraphic(null);
            alert.setTitle("Confirm Z-Report");
            alert.setHeaderText("Action is Permanent");
            
            if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                // Fetch final data to show success message
                Database.ZReportData finalData = BackendController.getZReport();
                BackendController.clearOrdersToday();
                
                Alert success = new Alert(Alert.AlertType.INFORMATION,
                    String.format("Z-Report Successful!\nTotal Sales: $%.2f\nToday's data has been cleared.", finalData.getTotalSales()));
                success.showAndWait();
                
                refreshZReport.run();
            }
        });

        reportContent.getChildren().addAll(totalSalesLabel, empOrdersList, generateBtn);
        container.getChildren().addAll(header, reportContent);
        
        refreshZReport.run();
        return container;
    }

    /**
     * Displays a dialog for adding a new employee.
     */
    private void showAddEmployeeDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Employee");

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));
        form.setStyle(BORDER + "-fx-background-color: white;");

        TextField n = new TextField();
        n.setPromptText("Name");
        TextField r = new TextField();
        r.setPromptText("Role");
        TextField p = new TextField();
        p.setPromptText("Pay");
        n.setStyle(BORDER);
        r.setStyle(BORDER);
        p.setStyle(BORDER);

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

    /**
     * Displays a dialog for editing an existing employee's details.
     *
     * @param emp The employee to edit.
     */
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
        n.setStyle(BORDER);
        r.setStyle(BORDER);
        p.setStyle(BORDER);

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

    /**
     * Adds an employee row to the team roster table.
     *
     * @param emp The employee to add.
     */
    private void addEmployeeRow(Employee emp) {
        HBox row = new HBox();
        row.setStyle("-fx-border-color: transparent transparent black transparent; -fx-padding: 5;");

        Button editBtn = new Button("Edit");
        editBtn.setStyle(BORDER + "-fx-font-size: 10;");
        editBtn.setOnAction(e -> showEditEmployeeDialog(emp));

        row.getChildren().addAll(
                createDataCell(emp.getName(), 150),
                createDataCell(emp.getJob(), 100),
                createDataCell("$" + emp.getPay() + "/hr", 100),
                createDataCell(String.valueOf(emp.getOrderNum()), 100),
                new StackPane(editBtn));
        tableRowsContainer.getChildren().add(row);
    }

    /**
     * Creates a header cell for a table.
     *
     * @param t The text for the header.
     * @param w The width of the cell.
     * @return The created Label.
     */
    private Label createHeaderCell(String t, double w) {
        Label l = new Label(t);
        l.setPrefWidth(w);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 10; -fx-padding: 5;");
        return l;
    }

    /**
     * Creates a data cell for a table.
     *
     * @param t The text for the cell.
     * @param w The width of the cell.
     * @return The created Label.
     */
    private Label createDataCell(String t, double w) {
        Label l = new Label(t);
        l.setPrefWidth(w);
        l.setStyle("-fx-padding: 5;");
        return l;
    }

    /**
     * Creates a tab button.
     *
     * @param text The text for the tab.
     * @return The created Button.
     */
    private Button createTab(String text) {
        Button b = new Button(text);
        b.setPrefWidth(100);
        b.setStyle(BORDER + "-fx-background-color: white;");
        return b;
    }
}
