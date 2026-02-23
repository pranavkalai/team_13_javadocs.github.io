package com.example;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ManagerView {
    private enum Tab {
        TRENDS, STOCK, TEAM
    }

    private static Tab activeTab = Tab.TRENDS;

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

        StackPane contentArea = new StackPane();
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        t1.setOnAction(e -> {
            activeTab = Tab.TRENDS;
            renderActiveTab(contentArea, t1, t2, t3);
        });
        t2.setOnAction(e -> {
            activeTab = Tab.STOCK;
            renderActiveTab(contentArea, t1, t2, t3);
        });
        t3.setOnAction(e -> {
            activeTab = Tab.TEAM;
            renderActiveTab(contentArea, t1, t2, t3);
        });

        renderActiveTab(contentArea, t1, t2, t3);
        layout.getChildren().addAll(title, tabs, contentArea);
        return layout;
    }

    private Button createTab(String text) {
        Button b = new Button(text);
        b.setPrefWidth(100);
        b.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-background-radius: 0;");
        return b;
    }

    private VBox buildTrendsView() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(16));
        content.getChildren().addAll(
            new Label("Sales Trends"),
            new Label("Top selling item (week): Classic Milk Tea"),
            new Label("Orders today: 0"),
            new Label("Revenue today: $0.00")
        );
        return content;
    }

    private VBox buildStockView() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(16));
        content.getChildren().addAll(
            new Label("Inventory Stock"),
            new Label("Milk Tea Base: 0 units"),
            new Label("Tapioca Pearls: 0 units"),
            new Label("Matcha Powder: 0 units")
        );
        return content;
    }

    private VBox buildTeamView() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(16));
        VBox table = new VBox();
        table.setStyle("-fx-border-color: black;");
        HBox head = new HBox(new Label("NAME"), new Label("ROLE"), new Label("PAY RATE"), new Label("HRS WORKED")); // [cite: 54, 57, 58, 59]
        head.setStyle("-fx-background-color: #eee; -fx-padding: 5;");
        table.getChildren().addAll(head, new Label("----    ----    $0/hr    0h")); // [cite: 60, 61]
        content.getChildren().addAll(new Label("Team Roster"), table, new Button("Add Employee")); // [cite: 53, 62]
        return content;
    }

    private void renderActiveTab(StackPane contentArea, Button t1, Button t2, Button t3) {
        if (activeTab == Tab.TRENDS) {
            setActiveTab(t1, t2, t3);
            contentArea.getChildren().setAll(buildTrendsView());
            return;
        }
        if (activeTab == Tab.STOCK) {
            setActiveTab(t2, t1, t3);
            contentArea.getChildren().setAll(buildStockView());
            return;
        }
        setActiveTab(t3, t1, t2);
        contentArea.getChildren().setAll(buildTeamView());
    }

    private void setActiveTab(Button active, Button inactive1, Button inactive2) {
        active.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-color: black; -fx-background-radius: 0;");
        inactive1.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: black; -fx-background-radius: 0;");
        inactive2.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: black; -fx-background-radius: 0;");
    }
}
