package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapVisualiser extends Application {

    private GridPane grid;
    private Image grassImage, groundImage, animalImage;
    private final int width = 80;
    private final int height = 40;
    private SimulationEngine simulationEngine = new SimulationEngine(width, height, 40, 15, 0.2,6, 1);
    private final HashMap<Vector2d, ArrayList<IMapElement>> objectsOnMap = simulationEngine.worldMap.objectsOnMap;
    private int currentDay = 0;


    public static void main(String[] args) {
        launch(args);
    }



    @Override
    public void start(Stage primaryStage) throws Exception{

        grassImage = new Image(new FileInputStream("/Users/user/Desktop/grass.jpg"));
        groundImage = new Image(new FileInputStream("/Users/user/Desktop/ground.jpg"));
        animalImage = new Image(new FileInputStream("/Users/user/Desktop/animal.jpg"));

        Button button = new Button("change day");
        Label dayNumberLabel = new Label("day 0");
        grid = new GridPane();
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setPadding(new Insets(15));
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(grid);


        button.setOnAction(e ->
        {
            currentDay += 1;
            dayNumberLabel.setText("day " + currentDay);
            resetOccupiedFields();
            simulationEngine.day();
            setOccupiedFields();
        });
        VBox vBox = new VBox();
        vBox.getChildren().addAll(button,dayNumberLabel);
        borderPane.setBottom(vBox);
        Scene scene = new Scene(borderPane);
        fillGrid();

        grid.setId("grid");
        scene.getStylesheets().addAll(this.getClass().getResource("backgroundGrid.css").toExternalForm());
        primaryStage.setTitle("Evolution Generator");
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    private void fillGrid()
    {

        for(int i=0; i<width; i++)
        {
            for(int j=0; j<height; j++)
            {
                ImageView selectedImage = new ImageView();
                selectedImage.setImage(groundImage);
                GridPane.setConstraints(selectedImage,i,j);
                grid.getChildren().addAll(selectedImage);
            }
        }
    }

    private void resetOccupiedFields()
    {
        Iterator iterator = objectsOnMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry mapElement = (Map.Entry) iterator.next();
            Vector2d position = (Vector2d) mapElement.getKey();
            ImageView selectedImage = new ImageView();
            selectedImage.setImage(groundImage);
            GridPane.setConstraints(selectedImage,position.x,position.y);
            grid.getChildren().addAll(selectedImage);
        }
    }

    private void setOccupiedFields()
    {
        Iterator iterator = objectsOnMap.entrySet().iterator();
        Vector2d position = null;
        boolean isAnimalInList;
        while(iterator.hasNext())
        {
            Map.Entry mapElement = (Map.Entry) iterator.next();
            ArrayList<IMapElement> array = (ArrayList<IMapElement>) mapElement.getValue();
            position = (Vector2d) mapElement.getKey();

            if(array.size() >0 )
            {
                isAnimalInList = false;
                for(IMapElement iMapElement: array)
                {
                    if(iMapElement.getClass() == Animal.class)
                    {
                        isAnimalInList = true;
                        ImageView selectedImage = new ImageView();
                        selectedImage.setImage(animalImage);
                        GridPane.setConstraints(selectedImage,position.x,position.y);
                        grid.getChildren().addAll(selectedImage);
                    }
                }
                if(!isAnimalInList)
                {
                    ImageView selectedImage = new ImageView();
                    selectedImage.setImage(grassImage);
                    GridPane.setConstraints(selectedImage,position.x,position.y);
                    grid.getChildren().addAll(selectedImage);
                }
            }
        }
    }
}
