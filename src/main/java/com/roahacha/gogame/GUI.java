package com.roahacha.gogame;

import com.roahacha.gogame.Common.Stone;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
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
        VBox top = new VBox(infoLabel, statusLabel);
        root.setTop(top);

        GridPane grid = new GridPane();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells[r][c] = createCell(r, c);
                grid.add(cells[r][c], c, r);
            }
        }
        root.setCenter(grid);

        primaryStage.setScene(new Scene(root, 600, 650));
        primaryStage.setTitle("Go Game");


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
        cell.getChildren().removeIf(n -> n instanceof Circle); // Czyścimy
        if (stone != null) {
            Circle circle = new Circle(13);
            circle.setFill(stone == Stone.BLACK ? Color.BLACK : Color.WHITE);
            circle.setStroke(Color.GREY);
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
            alert.setContentText(msg);
            alert.show();
        });
    }
}