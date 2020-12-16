package sample;

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

    private final GridPane grid;
    private int currentDay = 0;
    private TimerTask timerTask;
    private PaintUpdate paintUpdate;
    private int delay;

    public MapVisualiser(int width, int height, int delay, int startEnergy, int plantEnergy, int jungleRatio, int startAnimalNumber, int dayEnergyCost)
    {

        Stage primaryStage = new Stage();
        Button startButton = new Button("start Simulation");
        Button stopButton = new Button("stop Simulation");
        Button restartButton = new Button("Restart Simulation");
        Label dayNumberLabel = new Label("day 0");
        dayNumberLabel.setFont(new Font("Arial", 30));
        Label animalArrayLength = new Label("animals 0");
        Label grassArrayLength = new Label("grass number 0");
        Label averageEnergyLevelLabel = new Label("average animal energy 0");
        Label averageAnimalLiveLengthLabel = new Label("average animal live length 0");
        Label averageChildNumberLabel = new Label("average child number 0");

        this.delay = delay;

        grid = new GridPane();
        paintUpdate = new PaintUpdate(width, height, grid, startEnergy, plantEnergy, jungleRatio, startAnimalNumber, dayEnergyCost);
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setPadding(new Insets(15));
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(grid);

        // creating timer
        Timer myTimer = new Timer();


        startButton.setOnAction(e ->
        {
            timerCycle(dayNumberLabel, animalArrayLength, grassArrayLength, averageEnergyLevelLabel, averageAnimalLiveLengthLabel, averageChildNumberLabel, myTimer);
        });

        stopButton.setOnAction(e -> {
            timerTask.cancel();
        });

        restartButton.setOnAction(e ->
        {
            paintUpdate = new PaintUpdate(width,height,grid, startEnergy, plantEnergy, jungleRatio, startAnimalNumber, dayEnergyCost);
            currentDay = 0;
        });

        // top Panel to render day number
        VBox vBoxTop = new VBox();
        vBoxTop.setPrefWidth(200);
        vBoxTop.setPrefHeight(60);
        vBoxTop.setAlignment(Pos.CENTER);
        vBoxTop.getChildren().addAll(dayNumberLabel);
        borderPane.setTop(vBoxTop);

        // left Panel to change Days
        VBox vBoxLeft = new VBox();
        vBoxLeft.setPrefWidth(200);
        vBoxLeft.setAlignment(Pos.TOP_CENTER);
        vBoxLeft.getChildren().addAll(startButton, stopButton, restartButton);
        borderPane.setLeft(vBoxLeft);


        // right Panel to render Statistics
        VBox vBoxRight = new VBox();
        vBoxRight.setPrefWidth(200);
        vBoxRight.setAlignment(Pos.TOP_CENTER);
        vBoxRight.getChildren().addAll(animalArrayLength, grassArrayLength, averageAnimalLiveLengthLabel, averageChildNumberLabel, averageEnergyLevelLabel);
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
