package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {
    private StackPane contentArea = new StackPane();
    private final String BORDER = "-fx-border-color: black; -fx-border-width: 1; -fx-background-radius: 0; -fx-border-radius: 0;";

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-border-color: transparent black transparent transparent;");

        Label logo = new Label("BOBA SHOP"); // [cite: 5]
        logo.setStyle("-fx-font-weight: bold; -fx-font-size: 20; -fx-padding: 20;");

        Button cashierBtn = createSideBtn("Cashier"); // [cite: 6]
        Button managerBtn = createSideBtn("Manager"); // [cite: 7]

        cashierBtn.setOnAction(e -> {
            setActive(cashierBtn, managerBtn);
            contentArea.getChildren().setAll(new CashierView().getView());
        });
        managerBtn.setOnAction(e -> {
            setActive(managerBtn, cashierBtn);
            contentArea.getChildren().setAll(new ManagerView().getView());
        });

        sidebar.getChildren().addAll(logo, cashierBtn, managerBtn);
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        cashierBtn.fire();
        stage.setScene(new Scene(root, 1200, 800));
        stage.show();
    }

    private Button createSideBtn(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefHeight(60);
        b.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent black transparent; -fx-background-radius: 0;");
        return b;
    }

    private void setActive(Button active, Button inactive) {
        active.setStyle("-fx-background-color: black; -fx-text-fill: white; " + BORDER);
        inactive.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: transparent transparent black transparent; -fx-background-radius: 0;");
    }

    public static void main(String[] args) { launch(args); }
}