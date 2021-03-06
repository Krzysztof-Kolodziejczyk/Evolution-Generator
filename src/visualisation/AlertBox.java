package visualisation;

import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AlertBox {

    public AlertBox(){
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Alert");
        window.setMinWidth(200);
        window.setMinHeight(200);

        Label alertLabel = new Label("Invalid Data");
        alertLabel.setFont(new Font("Arial", 30));
        Button closeButton = new Button("close");
        closeButton.setOnAction(e->window.close());

        VBox mainLayout = new VBox();
        mainLayout.setStyle("-fx-background-color: red");
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setSpacing(40);
        mainLayout.getChildren().addAll(alertLabel, closeButton);

        Scene scene = new Scene(mainLayout);
        window.setScene(scene);
        window.showAndWait();

    }
}
