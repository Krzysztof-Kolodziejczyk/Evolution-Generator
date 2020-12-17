package classes;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import visualisation.Colors;


public class PaintUpdate {

    private final int width;
    private final int height;
    private Label sandLabel;
    private final double blockSize;
    private final GridPane grid;
    public final SimulationEngine simulationEngine;
    private final Colors colors;

    public PaintUpdate(int width, int height, GridPane gridPane, int startEnergy, int plantEnergy, int jungleRatio, int startAnimalNumber, int dayEnergyCost) {
        this.width = width;
        this.height = height;
        this.grid = gridPane;
        this.simulationEngine = new SimulationEngine(width, height, startEnergy, plantEnergy, jungleRatio, startAnimalNumber, dayEnergyCost);

        this.colors = new Colors();

        blockSize = sizeCounter();

        fillGrid();

    }

    public void fillGrid() {

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                sandLabel = new Label();
                sandLabel.setStyle("-fx-background-color: olive");
                sandLabel.setMinSize(blockSize, blockSize);
                sandLabel.setMaxSize(blockSize, blockSize);
                GridPane.setConstraints(sandLabel, i, j);
                grid.getChildren().addAll(sandLabel);
            }
        }
    }

    public void changeAnimalsLabelToSand() {
        for (Vector2d position : simulationEngine.worldMap.animalsPositions) {
            sandLabel = new Label();
            sandLabel.setStyle("-fx-background-color: olive");
            sandLabel.setMinSize(blockSize, blockSize);
            sandLabel.setMaxSize(blockSize, blockSize);

            GridPane.setConstraints(sandLabel, position.x, position.y);
            grid.getChildren().addAll(sandLabel);
        }
    }

    public void placeAnimalsOnMap() {
        for (Vector2d position : simulationEngine.worldMap.animalsPositions) {
            Label animalLabel;
            animalLabel = new Label();
            try {
                animalLabel.setStyle(colors.animalColors[simulationEngine.worldMap.getMaximumEnergyAnimal(position).animalColor()]);
            } catch (Exception ex) {
                animalLabel.setStyle("-fx-background-color: olive");
            }
            animalLabel.setMinSize(blockSize, blockSize);
            animalLabel.setMaxSize(blockSize, blockSize);

            GridPane.setConstraints(animalLabel, position.x, position.y);
            grid.getChildren().addAll(animalLabel);
        }
    }

    private double sizeCounter() {
        double x = (double) 400 / width;
        double y = (double) 400 / height;

        if (x > 1 && y > 1) {
            return Math.max(x, y);
        }
        return Math.min(x, y);
    }

    public void setOccupiedFields() {
        for (Vector2d position : simulationEngine.worldMap.addedGrasses) {
            Label grassLabel = new Label();
            grassLabel.setStyle("-fx-background-color: lawngreen");
            grassLabel.setMinSize(blockSize, blockSize);
            grassLabel.setMaxSize(blockSize, blockSize);
            GridPane.setConstraints(grassLabel, position.x, position.y);
            grid.getChildren().addAll(grassLabel);
        }
    }
}
