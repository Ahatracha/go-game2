package com.roahacha.gogame;

import javafx.application.Application;
import javafx.application.Platform; // DODANO: dla poprawnego zamykania wątków
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GUI extends Application {

    private final int SIZE = 19;
    private StackPane[][] cells = new StackPane[SIZE][SIZE];
    private Client client;

    @Override
    public void start(Stage primaryStage) {

        client = new Client(this);
        client.connectToServer();

        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #DEB887;");


        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = createCell(row, col);
                grid.add(cells[row][col], col, row);
            }
        }

        Scene scene = new Scene(grid, 600, 600);
        primaryStage.setTitle("Go Game - Klient JavaFX");
        primaryStage.setScene(scene);


        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
    }

    private StackPane createCell(int row, int col) {
        StackPane cell = new StackPane();
        cell.setPrefSize(32, 32);


        Rectangle rect = new Rectangle(30, 30);
        rect.setFill(Color.TRANSPARENT);
        rect.setStroke(Color.BLACK);

        cell.getChildren().add(rect);


        cell.setOnMouseClicked(event -> {

            if (client.isMyTurn()) {
                client.sendMove(row, col);
            }
        });

        return cell;
    }


    public void MakeColor(int r, int c, Color color) {
        StackPane cell = cells[r][c];

        // Usuwamy stary kamień jeśli istniał na tym polu
        cell.getChildren().removeIf(node -> node instanceof Circle);

        if (color != null) {
            Circle stone = new Circle(13);
            stone.setFill(color);
            stone.setStroke(Color.DARKGRAY);
            cell.getChildren().add(stone);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
