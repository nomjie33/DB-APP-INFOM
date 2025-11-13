/*
=====================================
This code controls the launch state of the UVR! app. It ensures
that the app will perform the following at launch:
1. The logo's initial opacity is 0
2. The logo will slide up
3. The logo fades in along with the animation

Version: 1.0
Latest edit: November 10, 2025

Authors:
Airon Matthew Bantillo | S22-07
Alexandra Gayle Gonzales | S22-07
Naomi Isabel Reyes | S22-07
Roberta Netanya Tan | S22-07
=====================================
*/

package main.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class main_launchController implements Initializable{

    @FXML
    private BorderPane splashRoot;

    @FXML
    private ImageView logo;

    /**
    ===================================================================
    This function initializes the launch sequence of
    the application.
    @param url is an attribute needed to override the initialize method.
    @param rb is an attributed needed to override the initialize method.
    ====================================================================
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){

        //1. Starting state of logo
        logo.setOpacity(0.0);
        logo.setTranslateY(100);

        //2. Slide up animation
        TranslateTransition tt = new TranslateTransition(Duration.seconds(1.5), logo);
        tt.setToY(0);

        //3. Fade in
        FadeTransition ft = new FadeTransition(Duration.seconds(1.5), logo);
        ft.setToValue(1.0);

        //4. Combining 2 and 3 for the full animation
        ParallelTransition pt = new ParallelTransition(tt, ft);

        pt.setOnFinished(event -> {

            PauseTransition delay = new PauseTransition(Duration.seconds(0.5));

            delay.setOnFinished(e -> loadLoginScene());
            delay.play();
        });

        pt.play();
    }

    /**
     ==================================================
     This method makes the code proceed to the main
     login page.
     ==================================================
     */
    private void loadLoginScene(){

        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("Main-login.fxml"));
            Scene loginScene = new Scene(loginRoot);

            Stage currentStage = (Stage) splashRoot.getScene().getWindow();

            currentStage.setScene(loginScene);
            currentStage.setTitle("UVR!");
            currentStage.show();

        } catch (IOException e){
            e.printStackTrace();
        }

    }
}