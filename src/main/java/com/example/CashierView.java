package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CashierView {
    private final String BORDER = "-fx-border-color: black; -fx-border-width: 1; -fx-background-radius: 0; -fx-border-radius: 0;";
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private VBox cartItemsContainer = new VBox(5);
    private Label cartLabel = new Label("Cart (0)");
    private Label totalLabel = new Label("Total: $0.00");
    private StackPane emptyCartPlaceholder = new StackPane(new Label("Cart Empty"));

    public HBox getView() {
        HBox layout = new HBox();
        layout.setStyle("-fx-background-color: white;");

        // --- MENU SECTION ---
        VBox menuSection = new VBox(15, new Label("Order"));
        menuSection.setPadding(new Insets(20));
        HBox.setHgrow(menuSection, Priority.ALWAYS);

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(20);
        for (int i = 0; i < 20; i++) 
            {
            grid.add(createProductCard( i, "Boba tea " + i, 5.00, "Normal", "Normal", "None"), i % 3, i / 3);
            }
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: transparent;");
        menuSection.getChildren().add(scrollPane);

        // --- CART SECTION ---
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
            if (!cartItems.isEmpty()) {
                showCheckoutDialog(); 
            }
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        cartSection.getChildren().addAll(cartLabel, emptyCartPlaceholder, cartItemsContainer, spacer, totalLabel, placeOrder);

        layout.getChildren().addAll(menuSection, cartSection);
        return layout;
    }

    private void showCheckoutDialog() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL); 
        popup.setTitle("Checkout");

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: white; " + BORDER);

        Label title = new Label("SELECT PAYMENT METHOD");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Button cashBtn = createPaymentBtn("CASH");
        Button creditBtn = createPaymentBtn("CREDIT");
        Button debitBtn = createPaymentBtn("DEBIT");

        cashBtn.setOnAction(e -> finalizeOrder("CASH", popup));
        creditBtn.setOnAction(e -> finalizeOrder("CREDIT", popup));
        debitBtn.setOnAction(e -> finalizeOrder("DEBIT", popup));

        Button cancelBtn = new Button("CANCEL");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: gray; -fx-font-size: 10;");
        cancelBtn.setOnAction(e -> popup.close());

        layout.getChildren().addAll(title, cashBtn, creditBtn, debitBtn, cancelBtn);

        Scene scene = new Scene(layout, 350, 450);
        popup.setScene(scene);
        popup.show();
    }

    private Button createPaymentBtn(String text) {
        Button b = new Button(text);
        b.setPrefWidth(200);
        b.setPrefHeight(50);
        b.setStyle(BORDER + "-fx-background-color: white; -fx-font-weight: bold;");
        
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 0;"));
        b.setOnMouseExited(e -> b.setStyle(BORDER + "-fx-background-color: white; -fx-text-fill: black;"));
        
        return b;
    }

    private void finalizeOrder(String method, Stage popup) {
        BackendController.handlePlaceOrder("Guest Customer", cartItems);
        System.out.println("[INFO] Payment successful via: " + method);

        cartItems.clear();
        updateCartUI();
        popup.close(); 
    }

    //make product card with add button
    private VBox createProductCard(int menuID, String name, double cost, String ice, String sugar, String topping) {
        VBox card = new VBox();
        card.setStyle(BORDER);
        StackPane img = new StackPane(new Line(0,0,200,150), new Line(200,0,0,150));
        img.setPrefHeight(150);
        
        Button add = new Button("Add");
        add.setStyle(BORDER);
        add.setOnAction(e -> { cartItems.add(new CartItem(menuID, name, cost, ice, sugar, topping)); updateCartUI(); });

        HBox footer = new HBox(new Label("$" + cost), new Region(), add);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);
        footer.setPadding(new Insets(5));
        footer.setStyle("-fx-border-color: black transparent transparent transparent;");

        card.getChildren().addAll(img, new Label(name), footer);
        return card;
    }

    private void updateCartUI() {
        cartItemsContainer.getChildren().clear();
        boolean empty = cartItems.isEmpty();
        emptyCartPlaceholder.setVisible(empty);
        emptyCartPlaceholder.setManaged(empty);

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