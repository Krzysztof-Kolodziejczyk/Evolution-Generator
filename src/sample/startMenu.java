package sample;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class startMenu extends Application{


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
    private int startEnergy;
    private int plantEnergy;
    private int jungleRatio;
    private int startAnimalNumber;
    private int dayEnergyCost;
    private RuntimeException runtimeException;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Evolution Generator");

        // main Layout
        VBox mainLayout = new VBox();
        mainLayout.setPrefSize(400,400);
        mainLayout.setAlignment(Pos.TOP_CENTER);

        // start Button
        Button startButton = new Button("start");
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
            }catch (Exception ex)
            {
                alertBox = new AlertBox();
            }


        });


        // text Fields and Labels and HBox Layouts

        HBox widthLayout = new HBox();
        Label widthLabel = new Label("width ");
        widthTextField = new TextField();
        widthLayout.getChildren().addAll(widthLabel, widthTextField);
        widthLayout.setAlignment(Pos.CENTER);

        HBox heightLayout = new HBox();
        Label heightLabel = new Label("height ");
        heightTextField = new TextField();
        heightLayout.getChildren().addAll(heightLabel, heightTextField);
        heightLayout.setAlignment(Pos.CENTER);

        HBox delayLayout = new HBox();
        Label delayLabel = new Label("delay (milliseconds");
        delayTextField = new TextField();
        delayLayout.getChildren().addAll(delayLabel,delayTextField);
        delayLayout.setAlignment(Pos.CENTER);

        HBox startAnimalsNumberLayout = new HBox();
        Label startAnimalsNumberLabel = new Label("start animals number ");
        startAnimalsNumberTextField = new TextField();
        startAnimalsNumberLayout.getChildren().addAll(startAnimalsNumberLabel,startAnimalsNumberTextField);
        startAnimalsNumberLayout.setAlignment(Pos.CENTER);

        HBox startAnimalEnergyLayout = new HBox();
        Label startAnimalsEnergyLabel = new Label("start animal energy ");
        startAnimalsEnergyTextField = new TextField();
        startAnimalEnergyLayout.getChildren().addAll(startAnimalsEnergyLabel,startAnimalsEnergyTextField);
        startAnimalEnergyLayout.setAlignment(Pos.CENTER);

        HBox plantEnergyLayout = new HBox();
        Label plantEnergyLabel = new Label("plant energy ");
        plantEnergyTextField = new TextField();
        plantEnergyLayout.getChildren().addAll(plantEnergyLabel,plantEnergyTextField);
        plantEnergyLayout.setAlignment(Pos.CENTER);

        HBox jungleRatioLayout = new HBox();
        Label jungleRatioLabel = new Label("jungle Ratio [0,100] ");
        jungleRatioTextField = new TextField();
        jungleRatioLayout.getChildren().addAll(jungleRatioLabel,jungleRatioTextField);
        jungleRatioLayout.setAlignment(Pos.CENTER);

        HBox dayEnergyCostLayout = new HBox();
        Label dayEnergyCostLabel = new Label("day energy cost ");
        dayEnergyCostTextField = new TextField();
        dayEnergyCostLayout.getChildren().addAll(dayEnergyCostLabel, dayEnergyCostTextField);
        dayEnergyCostLayout.setAlignment(Pos.CENTER);


        // configure main Layout and add horizontal Layout to main layout
        mainLayout.setSpacing(10);
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

