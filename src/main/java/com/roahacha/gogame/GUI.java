package com.roahacha.gogame;

import com.roahacha.gogame.Common.Stone;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GUI extends Application {

    private final int SIZE = 19;
    private StackPane[][] cells = new StackPane[SIZE][SIZE];
    private Client client;

    private Label infoLabel = new Label("Łączenie...");
    private Label statusLabel = new Label("");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        client = new Client(this);


        BorderPane root = new BorderPane();

        VBox top = new VBox(10, infoLabel, statusLabel);
        top.setAlignment(Pos.CENTER);
        top.setStyle("-fx-padding: 10; -fx-background-color: #f4f4f4;");
        root.setTop(top);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #DEB887; -fx-padding: 10;");

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells[r][c] = createCell(r, c);
                grid.add(cells[r][c], c, r);
            }
        }
        root.setCenter(grid);

        Button passButton = new Button("PAS");
        passButton.setPrefWidth(100);
        passButton.setStyle("-fx-background-color: #87CEEB; -fx-cursor: hand;");

        Button surrenderButton = new Button("PODDAJ SIĘ");
        surrenderButton.setPrefWidth(100);
        surrenderButton.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white; -fx-cursor: hand;");

        passButton.setOnAction(e -> client.sendPass());
        surrenderButton.setOnAction(e -> client.sendSurrender());

        HBox bottomPanel = new HBox(20, passButton, surrenderButton);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setStyle("-fx-padding: 15; -fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-width: 1 0 0 0;");

        root.setBottom(bottomPanel);

        primaryStage.setScene(new Scene(root, 650, 750));primaryStage.setTitle("Go Game");
        primaryStage.setOnCloseRequest(e -> {
            client.close();
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

        //using Thread because of javafx
        new Thread(() -> client.connectToServer()).start();
    }

    private StackPane createCell(int r, int c) {
        StackPane cell = new StackPane();
        cell.setPrefSize(30, 30);
        Rectangle rect = new Rectangle(30, 30);
        rect.setFill(Color.TRANSPARENT);
        rect.setStroke(Color.BLACK);
        cell.getChildren().add(rect);

        //sending to the client
        cell.setOnMouseClicked(e -> client.sendMove(r, c));
        return cell;
    }

    //metoths for Client

    public void refreshBoard(Stone[][] grid) {
        Platform.runLater(() -> {
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    updateCell(r, c, grid[r][c]);
                }
            }
        });
    }

    private void updateCell(int r, int c, Stone stone) {
        StackPane cell = cells[r][c];


        cell.getChildren().removeIf(n -> n instanceof Circle);


        if (stone == Stone.BLACK) {
            Circle circle = new Circle(13);
            circle.setFill(Color.BLACK);
            circle.setStroke(Color.GREY);
            cell.getChildren().add(circle);
        }
        else if (stone == Stone.WHITE) {
            Circle circle = new Circle(13);
            circle.setFill(Color.WHITE);
            circle.setStroke(Color.BLACK);
            cell.getChildren().add(circle);
        }

    }

    public void updateInfo(String text) {
        Platform.runLater(() -> infoLabel.setText(text));
    }

    public void updateStatus(String text) {
        Platform.runLater(() -> statusLabel.setText(text));
    }

    public void showEndMessage(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Koniec Gry");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.show();
        });
    }
}