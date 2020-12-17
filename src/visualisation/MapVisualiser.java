package visualisation;

import classes.PaintUpdate;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class MapVisualiser {

    private int currentDay = 0;
    private TimerTask timerTask;
    private final PaintUpdate paintUpdate;
    private final int delay;

    public MapVisualiser(int width, int height, int delay, int startEnergy, int plantEnergy, int jungleRatio, int startAnimalNumber, int dayEnergyCost) {

        Stage primaryStage = new Stage();

        // buttons
        Button startButton = new Button("start");
        startButton.setFont(new Font("Arial", 20));
        startButton.setStyle("-fx-background-color: green");
        Button stopButton = new Button("stop");
        stopButton.setStyle("-fx-background-color: red");
        stopButton.setFont(new Font("Arial", 20));

        // Labels
        Label dayNumberLabel = new Label("day 0");
        dayNumberLabel.setFont(new Font("Arial", 30));
        Label animalArrayLengthLabel = new Label("animals 0");
        animalArrayLengthLabel.setFont(new Font(20));
        Label grassArrayLengthLabel = new Label("grass number 0");
        grassArrayLengthLabel.setFont(new Font(20));
        Label averageEnergyLevelLabel = new Label("average animal energy 0");
        averageEnergyLevelLabel.setFont(new Font(20));
        Label averageAnimalLiveLengthLabel = new Label("average animal live length 0");
        averageAnimalLiveLengthLabel.setFont(new Font(20));
        Label averageChildNumberLabel = new Label("average child number 0");
        averageChildNumberLabel.setFont(new Font(20));

        this.delay = delay;

        GridPane grid = new GridPane();
        paintUpdate = new PaintUpdate(width, height, grid, startEnergy, plantEnergy, jungleRatio, startAnimalNumber, dayEnergyCost);
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setPadding(new Insets(15));

        // main Layout
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(grid);
        borderPane.setStyle("-fx-background-color:  darkslategray");

        // creating timer
        Timer myTimer = new Timer();


        startButton.setOnAction(e -> timerCycle(
                dayNumberLabel,
                animalArrayLengthLabel,
                grassArrayLengthLabel,
                averageEnergyLevelLabel,
                averageAnimalLiveLengthLabel,
                averageChildNumberLabel,
                myTimer));

        stopButton.setOnAction(e -> timerTask.cancel());


        // top Panel to render day number
        VBox vBoxTop = new VBox();
        vBoxTop.setPrefWidth(200);
        vBoxTop.setPrefHeight(60);
        vBoxTop.setAlignment(Pos.CENTER);
        vBoxTop.getChildren().addAll(dayNumberLabel);
        borderPane.setTop(vBoxTop);

        // left Panel to start and stop
        VBox vBoxLeft = new VBox();
        vBoxLeft.setSpacing(20);
        vBoxLeft.setPrefWidth(80);
        vBoxLeft.setAlignment(Pos.TOP_CENTER);
        vBoxLeft.getChildren().addAll(startButton, stopButton);
        borderPane.setLeft(vBoxLeft);


        // right Panel to render Statistics
        VBox vBoxRight = new VBox();
        vBoxLeft.setSpacing(20);
        vBoxRight.setPrefWidth(300);
        vBoxRight.setAlignment(Pos.TOP_CENTER);
        vBoxRight.getChildren().addAll(animalArrayLengthLabel, grassArrayLengthLabel, averageAnimalLiveLengthLabel, averageChildNumberLabel, averageEnergyLevelLabel);
        borderPane.setRight(vBoxRight);

        Scene scene = new Scene(borderPane);
        paintUpdate.fillGrid();

        grid.setId("grid");
        scene.getStylesheets().addAll(this.getClass().getResource("backgroundGrid.css").toExternalForm());

        primaryStage.setTitle("Evolution Generator");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void timerCycle(Label dayNumberLabel, Label animalArrayLength, Label grassArrayLength, Label averageEnergyLevelLabel, Label averageAnimalLiveLengthLabel, Label averageChildNumberLabel, Timer myTimer) {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    currentDay += 1;
                    dayNumberLabel.setText("day " + currentDay);
                    animalArrayLength.setText("animals " + paintUpdate.simulationEngine.worldMap.animalsNumber);
                    grassArrayLength.setText("grass  " + paintUpdate.simulationEngine.worldMap.grassesOnMap.size());
                    averageAnimalLiveLengthLabel.setText("average animal live length " + paintUpdate.simulationEngine.worldMap.averageSurvivedDaysNumber);
                    averageChildNumberLabel.setText("average child number " + paintUpdate.simulationEngine.worldMap.getAverageAnimalChildNumber());
                    averageEnergyLevelLabel.setText("average animal energy " + paintUpdate.simulationEngine.worldMap.getAverageEnergyLevel());
                    paintUpdate.changeAnimalsLabelToSand();
                    paintUpdate.simulationEngine.day();
                    paintUpdate.setOccupiedFields();
                    paintUpdate.placeAnimalsOnMap();
                });
            }
        };
        myTimer.schedule(timerTask, 0, delay);
    }
}
