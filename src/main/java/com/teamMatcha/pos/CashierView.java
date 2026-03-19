package com.teamMatcha.pos;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
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

/**
 * Provides the user interface for the Cashier view.
 * Allows employees to browse the menu, customize drinks, manage a cart, and place orders.
 */
public class CashierView {
    /** The border style for UI components. */
    private final String BORDER = "-fx-border-color: black; -fx-border-width: 1; -fx-background-radius: 0; -fx-border-radius: 0;";
    /** The list of items currently in the cart. */
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    /** The container for displaying cart items in the UI. */
    private VBox cartItemsContainer = new VBox(5);
    /** The label displaying the number of items in the cart. */
    private Label cartLabel = new Label("Cart (0)");
    /** The label displaying the total cost of items in the cart. */
    private Label totalLabel = new Label("Total: $0.00");
    /** The placeholder displayed when the cart is empty. */
    private StackPane emptyCartPlaceholder = new StackPane(new Label("Cart Empty"));
    /** The ID of the employee using the cashier view. */
    private final int employeeID;

    /**
     * Default constructor for CashierView.
     * Sets a default employee ID of 1.
     */
    public CashierView() {
        this.employeeID = 1; 
    }

    /**
     * Constructs a CashierView with a specific employee ID.
     *
     * @param employeeID The ID of the employee using this view.
     */
    public CashierView(int employeeID) {
        this.employeeID = employeeID;
    }

    /**
     * Constructs and returns the root layout of the Cashier view.
     *
     * @return The HBox containing the full Cashier view.
     */
    public HBox getView() {
        HBox layout = new HBox();
        layout.setStyle("-fx-background-color: white;");

        VBox menuSection = new VBox(15, new Label("Order"));
        menuSection.setPadding(new Insets(20));
        HBox.setHgrow(menuSection, Priority.ALWAYS);

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(20);
        
        List<Product> products = BackendController.getMenu();
        int index = 0;
        for (Product p : products) {
            grid.add(createProductCard(p), index % 3, index / 3);
            index++;
        }

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: transparent;");
        menuSection.getChildren().add(scrollPane);

        VBox cartSection = new VBox(10);
        cartSection.setPrefWidth(350);
        cartSection.setPadding(new Insets(20));
        cartSection.setStyle("-fx-border-color: transparent transparent transparent black;");

        emptyCartPlaceholder.setStyle(BORDER + "-fx-border-style: dashed;");
        emptyCartPlaceholder.setPrefHeight(200);

        Button placeOrder = new Button("Place Order");
        placeOrder.setMaxWidth(Double.MAX_VALUE);
        placeOrder.setStyle(BORDER);
        
        placeOrder.setOnAction(e -> {
            if (!cartItems.isEmpty()) { showCheckoutDialog(); }
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        cartSection.getChildren().addAll(cartLabel, emptyCartPlaceholder, cartItemsContainer, spacer, totalLabel, placeOrder);

        layout.getChildren().addAll(menuSection, cartSection);
        return layout;
    }

    /**
     * Displays the checkout dialog to enter customer name and select payment method.
     */
    private void showCheckoutDialog() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL); 
        popup.setTitle("Checkout");

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: white; " + BORDER);

        Label nameLabel = new Label("CUSTOMER NAME:");
        nameLabel.setStyle("-fx-font-weight: bold;");
        TextField nameInput = new TextField();
        nameInput.setPromptText("Enter name...");
        nameInput.setMaxWidth(200);

        Label title = new Label("SELECT PAYMENT METHOD");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Button cashBtn = createPaymentBtn("CASH", popup, nameInput);
        Button creditBtn = createPaymentBtn("CREDIT", popup, nameInput);
        Button debitBtn = createPaymentBtn("DEBIT", popup, nameInput);

        Button cancelBtn = new Button("CANCEL");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: gray; -fx-font-size: 10;");
        cancelBtn.setOnAction(e -> popup.close());

        layout.getChildren().addAll(nameLabel, nameInput, title, cashBtn, creditBtn, debitBtn, cancelBtn);

        Scene scene = new Scene(layout, 350, 450);
        popup.setScene(scene);
        popup.show();
    }

    /**
     * Creates a payment method button.
     *
     * @param text      The text to display on the button.
     * @param popup     The checkout stage to close upon completion.
     * @param nameInput The text field containing the customer's name.
     * @return The created button.
     */
    private Button createPaymentBtn(String text, Stage popup, TextField nameInput) {
        Button b = new Button(text);
        b.setPrefWidth(200); b.setPrefHeight(50);
        b.setStyle(BORDER + "-fx-background-color: white; -fx-font-weight: bold;");
        
        b.setOnAction(e -> {
            String customerName = nameInput.getText().trim();
            if (customerName.isEmpty()) {
                customerName = "Guest";
            }
            finalizeOrder(text, popup, customerName);
        });
        return b;
    }

    /**
     * Finalizes the order by communicating with the backend and updating the UI.
     *
     * @param method       The payment method used.
     * @param popup        The checkout stage.
     * @param customerName The name of the customer.
     */
    private void finalizeOrder(String method, Stage popup, String customerName) {
        boolean success = BackendController.handlePlaceOrder(customerName, employeeID, cartItems);
        
        if (success) {
            cartItems.clear();
            updateCartUI();
            popup.close(); 
        } else {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Order Error");
            alert.setHeaderText("Order Failed");
            alert.setContentText("Insufficient inventory to fulfill this order!");
            alert.showAndWait();
        }
    }

    /**
     * Creates a card representation of a product for the menu.
     *
     * @param p The product to represent.
     * @return A VBox containing the product details and an "Add" button.
     */
    private VBox createProductCard(Product p) {
        VBox card = new VBox();
        card.setStyle(BORDER);
        StackPane img = new StackPane(new Line(0,0,200,150), new Line(200,0,0,150));
        img.setPrefHeight(150);
        
        Button add = new Button("Add");
        add.setStyle(BORDER);
        add.setOnAction(e -> showCustomizationDialog(p.getMenuID(), p.getName(), p.getCost()));

        HBox footer = new HBox(new Label("$" + String.format("%.2f", p.getCost())), new Region(), add);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);
        footer.setPadding(new Insets(5));
        footer.setStyle("-fx-border-color: black transparent transparent transparent;");

        card.getChildren().addAll(img, new Label(p.getName()), footer);
        return card;
    }

    /**
     * Displays a dialog for customizing a drink (ice, sugar, toppings).
     *
     * @param id        The ID of the drink being customized.
     * @param drinkName The name of the drink.
     * @param price     The base price of the drink.
     */
    private void showCustomizationDialog(int id, String drinkName, double price) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Customize " + drinkName);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white; " + BORDER);

        ComboBox<String> iceBox = new ComboBox<>(FXCollections.observableArrayList("Regular", "Less Ice", "No Ice"));
        iceBox.setValue("Regular"); iceBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> sugarBox = new ComboBox<>(FXCollections.observableArrayList("100%", "75%", "50%", "0%"));
        sugarBox.setValue("100%"); sugarBox.setMaxWidth(Double.MAX_VALUE);

        Label toppingLabel = new Label("TOPPINGS (+$0.50 each)");
        toppingLabel.setStyle("-fx-font-weight: bold;");

        VBox toppingsList = new VBox(5);
        String[] toppingNames = {"Pearls", "Jelly", "Pudding", "Grass Jelly", "Red Bean", "Aloe Vera", "Lychee Jelly"};
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (String t : toppingNames) {
            CheckBox cb = new CheckBox(t);
            checkBoxes.add(cb);
            toppingsList.getChildren().add(cb);
        }

        ScrollPane toppingScroll = new ScrollPane(toppingsList);
        toppingScroll.setPrefHeight(120);
        toppingScroll.setFitToWidth(true);
        toppingScroll.setStyle("-fx-background: white; -fx-background-color: white;");

        Button confirmBtn = new Button("ADD TO CART");
        confirmBtn.setMaxWidth(Double.MAX_VALUE);
        confirmBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 0;");
        
        confirmBtn.setOnAction(e -> {
            List<String> selectedToppings = new ArrayList<>();
            for (CheckBox cb : checkBoxes) {
                if (cb.isSelected()) {
                    selectedToppings.add(cb.getText());
                }
            }
            
            String toppingsStr = selectedToppings.isEmpty() ? "None" : String.join(", ", selectedToppings);
            double finalPrice = price + (selectedToppings.size() * 0.50);

            cartItems.add(new CartItem(id, drinkName, finalPrice, iceBox.getValue(), sugarBox.getValue(), toppingsStr));
            updateCartUI();
            dialog.close();
        });

        layout.getChildren().addAll(new Label("ICE"), iceBox, new Label("SUGAR"), sugarBox, toppingLabel, toppingScroll, confirmBtn);
        dialog.setScene(new Scene(layout, 350, 500));
        dialog.show();
    }

    /**
     * Updates the cart section of the UI with the current items and total.
     */
    private void updateCartUI() {
        cartItemsContainer.getChildren().clear();
        emptyCartPlaceholder.setVisible(cartItems.isEmpty());
        emptyCartPlaceholder.setManaged(cartItems.isEmpty());

        for (CartItem item : cartItems) {
            HBox row = new HBox(new Label(item.getName()), new Region(), new Label("$" + String.format("%.2f", item.getCost())));
            HBox.setHgrow(row.getChildren().get(1), Priority.ALWAYS);
            row.setStyle("-fx-border-color: transparent transparent black transparent; -fx-padding: 5;");
            cartItemsContainer.getChildren().add(row);
        }
        cartLabel.setText("Cart (" + cartItems.size() + ")");
        totalLabel.setText(String.format("Total: $%.2f", BackendController.calculateCartTotal(cartItems)));
    }
}
