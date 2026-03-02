package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class Main extends Application {
    private StackPane contentArea = new StackPane();
    private final String BORDER = "-fx-border-color: black; -fx-border-width: 1; -fx-background-radius: 0; -fx-border-radius: 0;";

    @Override
    public void start(Stage stage) {
        stage.setTitle("BOBA SHOP POS");
        showEmployeeLogin(stage);
    }

    public void showEmployeeLogin(Stage stage) {
        List<Employee> employees = Database.getAllEmployees();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        // Header
        Label title = new Label("BOBA SHOP");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 20; -fx-padding: 20;");
        Label subtitle = new Label("Select Employee");
        subtitle.setStyle("-fx-font-size: 13; -fx-padding: 0 0 0 20;");

        Label divider = new Label("");
        divider.setMaxWidth(Double.MAX_VALUE);
        divider.setStyle("-fx-border-color: transparent transparent black transparent; -fx-padding: 10 0 0 0;");

        layout.getChildren().addAll(title, subtitle, divider);

        // One button per employee, same style as sidebar buttons
        for (Employee emp : employees) {
            Button btn = new Button(emp.getName() + "  —  " + emp.getJob());
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setPrefHeight(60);
            btn.setStyle(
                    "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; -fx-background-radius: 0; -fx-alignment: CENTER-LEFT; -fx-padding: 0 0 0 20;");
            btn.setOnMouseEntered(e -> btn.setStyle(
                    "-fx-background-color: black; -fx-text-fill: white; -fx-border-color: black; -fx-border-width: 1; -fx-background-radius: 0; -fx-alignment: CENTER-LEFT; -fx-padding: 0 0 0 20;"));
            btn.setOnMouseExited(e -> btn.setStyle(
                    "-fx-background-color: white; -fx-text-fill: black; -fx-border-color: black; -fx-border-width: 1; -fx-background-radius: 0; -fx-alignment: CENTER-LEFT; -fx-padding: 0 0 0 20;"));
            btn.setOnAction(e -> launchMainApp(stage, emp.getEmployeeID()));
            layout.getChildren().add(btn);
        }

        Scene scene = new Scene(layout, 1200, 800);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    private void launchMainApp(Stage stage, int employeeID) {
        contentArea = new StackPane(); // reset content area for new employee
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // Sidebar setup
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);
        sidebar.setStyle("-fx-border-color: transparent black transparent transparent;");

        Label logo = new Label("BOBA SHOP");
        logo.setStyle("-fx-font-weight: bold; -fx-font-size: 20; -fx-padding: 20;");

        Button cashierBtn = createSideBtn("Cashier");
        Button managerBtn = createSideBtn("Manager");
        Button switchBtn = createSideBtn("Switch Employee");

        cashierBtn.setOnAction(e -> {
            setActive(cashierBtn, managerBtn);
            contentArea.getChildren().setAll(new CashierView(employeeID).getView());
        });
        managerBtn.setOnAction(e -> {
            setActive(managerBtn, cashierBtn);
            contentArea.getChildren().setAll(new ManagerView().getView());
        });
        switchBtn.setOnAction(e -> showEmployeeLogin(stage));

        sidebar.getChildren().addAll(logo, cashierBtn, managerBtn, switchBtn);

        // Main Body Container to fix resizing issues
        HBox body = new HBox(sidebar, contentArea);
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        root.setCenter(body);

        cashierBtn.fire();

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setFullScreen(true);

        stage.setTitle("BOBA SHOP POS");
    }

    private Button createSideBtn(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefHeight(60);
        b.setStyle(
                "-fx-background-color: white; -fx-border-color: transparent transparent black transparent; -fx-background-radius: 0;");
        return b;
    }

    private void setActive(Button active, Button inactive) {
        active.setStyle("-fx-background-color: black; -fx-text-fill: white; " + BORDER);
        inactive.setStyle(
                "-fx-background-color: white; -fx-text-fill: black; -fx-border-color: transparent transparent black transparent; -fx-background-radius: 0;");
    }

    public static void main(String[] args) {
        launch(args);
    }
}