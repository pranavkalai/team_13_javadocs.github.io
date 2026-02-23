package com.example;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;

public class CashierView {
    public HBox getView() {
        HBox layout = new HBox();
        
        VBox menu = new VBox(15);
        menu.setPadding(new Insets(20));
        HBox.setHgrow(menu, Priority.ALWAYS);
        
        Label title = new Label("Order"); // [cite: 16]
        TextField search = new TextField();
        search.setPromptText("Search"); // [cite: 17]
        search.setStyle("-fx-border-color: black; -fx-background-radius: 0;");

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(20);
        for(int i=0; i<3; i++) grid.add(createCard(), i, 0);

        menu.getChildren().addAll(new HBox(title, new Region(), search), grid);

        VBox cart = new VBox(10);
        cart.setPrefWidth(300);
        cart.setStyle("-fx-border-color: transparent transparent transparent black;");
        cart.setPadding(new Insets(10));

        Label cartLabel = new Label("Cart (0)"); // [cite: 15]
        StackPane empty = new StackPane(new Label("Cart Empty")); // [cite: 28]
        empty.setStyle("-fx-border-color: black; -fx-border-style: dashed;");
        empty.setPrefHeight(200);

        Button place = new Button("Place Order"); // [cite: 29, 41]
        place.setMaxWidth(Double.MAX_VALUE);
        place.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-background-radius: 0;");
        place.setOnMouseEntered(e -> place.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 0;"));
        place.setOnMouseExited(e -> place.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: black; -fx-background-radius: 0;"));

        cart.getChildren().addAll(cartLabel, empty, new Region(), new Label("Total: $0.00"), place);
        layout.getChildren().addAll(menu, cart);
        return layout;
    }

    private VBox createCard() {
        VBox card = new VBox();
        card.setStyle("-fx-border-color: black;");
        StackPane x = new StackPane(new Line(0,0,150,150), new Line(150,0,0,150)); // [cite: 14]
        x.setPrefSize(150, 150);
        
        VBox info = new VBox(5, new Label("Boba tea"), new HBox(new Label("$0.00"), new Region(), new Button("Add"))); // [cite: 18, 19, 23]
        info.setPadding(new Insets(5));
        card.getChildren().addAll(x, info);
        return card;
    }
}