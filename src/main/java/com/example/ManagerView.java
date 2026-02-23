package com.example;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ManagerView {
    public VBox getView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));

        Label title = new Label("Manager Console"); // [cite: 43]
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 22;");

        HBox tabs = new HBox();
        Button t1 = createTab("Trends"); // [cite: 44]
        Button t2 = createTab("Stock"); // [cite: 45]
        Button t3 = createTab("Team"); // [cite: 46]
        tabs.getChildren().addAll(t1, t2, t3);

        VBox table = new VBox();
        table.setStyle("-fx-border-color: black;");
        HBox head = new HBox(new Label("NAME"), new Label("ROLE"), new Label("PAY RATE"), new Label("HRS WORKED")); // [cite: 54, 57, 58, 59]
        head.setStyle("-fx-background-color: #eee; -fx-padding: 5;");
        table.getChildren().addAll(head, new Label("----    ----    $0/hr    0h")); // [cite: 60, 61]

        layout.getChildren().addAll(title, tabs, new Label("Team Roster"), table, new Button("Add Employee")); // [cite: 53, 62]
        return layout;
    }

    private Button createTab(String text) {
        Button b = new Button(text);
        b.setPrefWidth(100);
        b.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-background-radius: 0;");
        return b;
    }
}