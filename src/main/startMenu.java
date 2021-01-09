package main;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import visualisation.AlertBox;
import visualisation.MapVisualiser;


public class startMenu extends Application{ // nazwy klas raczej PascalCase


    MapVisualiser mapVisualiser;

    AlertBox alertBox;

    TextField widthTextField;
    TextField heightTextField;
    TextField delayTextField;
    TextField startAnimalsNumberTextField;
    TextField startAnimalsEnergyTextField;
    TextField plantEnergyTextField;
    TextField jungleRatioTextField;
    TextField dayEnergyCostTextField;


    private int width;
    private int height;
    private int delay;
    private int startEnergy;    // czy to na pewno cechy menu?
    private int plantEnergy;
    private int jungleRatio;
    private int startAnimalNumber;
    private int dayEnergyCost;
    private final RuntimeException runtimeException = new RuntimeException();   // przygotowuje Pan wyjątek na zapas?

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Evolution Generator");

        // main Layout
        VBox mainLayout = new VBox();
        mainLayout.setStyle("-fx-background-position: center");
        mainLayout.setStyle("-fx-background-color: darkslategray");
        mainLayout.setPrefSize(400,400);
        mainLayout.setAlignment(Pos.TOP_CENTER);

        // start Button
        Button startButton = new Button("start");
        startButton.setStyle("-fx-background-color: green");
        startButton.setFont(new Font("bold", 20));
        startButton.setOnAction(e->{
            try{
                width = Integer.parseInt(widthTextField.getCharacters().toString());
                height = Integer.parseInt(heightTextField.getCharacters().toString());
                delay = Integer.parseInt(delayTextField.getCharacters().toString());
                checkData(delay);
                startEnergy = Integer.parseInt(startAnimalsEnergyTextField.getCharacters().toString());
                checkData(startEnergy);
                plantEnergy = Integer.parseInt(plantEnergyTextField.getCharacters().toString());
                checkData(plantEnergy);
                jungleRatio = Integer.parseInt(jungleRatioTextField.getCharacters().toString());
                checkData(jungleRatio);
                if(jungleRatio > 100)
                {
                    throw runtimeException;
                }
                startAnimalNumber = Integer.parseInt(startAnimalsNumberTextField.getCharacters().toString());
                checkData(startAnimalNumber);
                dayEnergyCost = Integer.parseInt(dayEnergyCostTextField.getCharacters().toString());
                checkData(dayEnergyCost);

                mapVisualiser = new MapVisualiser(width,height,delay,startEnergy, plantEnergy, jungleRatio, startAnimalNumber, dayEnergyCost);
            }catch (Exception ex)   // wszystkie wyjątki do jednego worka?
            {
                alertBox = new AlertBox();
            }


        });


        // text Fields and Labels and HBox Layouts

        HBox widthLayout = new HBox();
        Label widthLabel = new Label("width ");
        widthLabel.setStyle("-fx-text-fill: white");
        widthTextField = new TextField();
        widthLayout.getChildren().addAll(widthLabel, widthTextField);
        widthLayout.setAlignment(Pos.CENTER);

        HBox heightLayout = new HBox();
        Label heightLabel = new Label("height ");
        heightLabel.setStyle("-fx-text-fill: white");
        heightTextField = new TextField();
        heightLayout.getChildren().addAll(heightLabel, heightTextField);
        heightLayout.setAlignment(Pos.CENTER);

        HBox delayLayout = new HBox();
        Label delayLabel = new Label("delay (milliseconds");
        delayLabel.setStyle("-fx-text-fill: white");
        delayTextField = new TextField();
        delayLayout.getChildren().addAll(delayLabel,delayTextField);
        delayLayout.setAlignment(Pos.CENTER);

        HBox startAnimalsNumberLayout = new HBox();
        Label startAnimalsNumberLabel = new Label("start animals number ");
        startAnimalsNumberLabel.setStyle("-fx-text-fill: white");
        startAnimalsNumberTextField = new TextField();
        startAnimalsNumberLayout.getChildren().addAll(startAnimalsNumberLabel,startAnimalsNumberTextField);
        startAnimalsNumberLayout.setAlignment(Pos.CENTER);

        HBox startAnimalEnergyLayout = new HBox();
        Label startAnimalsEnergyLabel = new Label("start animal energy ");
        startAnimalsEnergyLabel.setStyle("-fx-text-fill: white");
        startAnimalsEnergyTextField = new TextField();
        startAnimalEnergyLayout.getChildren().addAll(startAnimalsEnergyLabel,startAnimalsEnergyTextField);
        startAnimalEnergyLayout.setAlignment(Pos.CENTER);

        HBox plantEnergyLayout = new HBox();
        Label plantEnergyLabel = new Label("plant energy ");
        plantEnergyLabel.setStyle("-fx-text-fill: white");
        plantEnergyTextField = new TextField();
        plantEnergyLayout.getChildren().addAll(plantEnergyLabel,plantEnergyTextField);
        plantEnergyLayout.setAlignment(Pos.CENTER);

        HBox jungleRatioLayout = new HBox();
        Label jungleRatioLabel = new Label("jungle Ratio [0,100] ");
        jungleRatioLabel.setStyle("-fx-text-fill: white");
        jungleRatioTextField = new TextField();
        jungleRatioLayout.getChildren().addAll(jungleRatioLabel,jungleRatioTextField);
        jungleRatioLayout.setAlignment(Pos.CENTER);

        HBox dayEnergyCostLayout = new HBox();
        Label dayEnergyCostLabel = new Label("day energy cost ");
        dayEnergyCostLabel.setStyle("-fx-text-fill: white");
        dayEnergyCostTextField = new TextField();
        dayEnergyCostLayout.getChildren().addAll(dayEnergyCostLabel, dayEnergyCostTextField);
        dayEnergyCostLayout.setAlignment(Pos.CENTER);


        // configure main Layout and add horizontal Layout to main layout
        mainLayout.setSpacing(15);
        mainLayout.getChildren().addAll(
                widthLayout,
                heightLayout,
                delayLayout,
                startAnimalsNumberLayout,
                startAnimalEnergyLayout,
                plantEnergyLayout,
                jungleRatioLayout,
                dayEnergyCostLayout
        );


        mainLayout.getChildren().add(startButton);

        Scene mainScene = new Scene(mainLayout);
        primaryStage.setScene(mainScene);
        primaryStage.show();

    }

    private void checkData(int data)
    {
        if(data <= 0)
        {
            throw runtimeException;
        }
    }
}

