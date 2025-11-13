/*
=====================================
This code serves as the main method
of the DB Application, UVR!

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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class UVR extends Application {

    /**
    ================================================
    This method facilitates the loading of external
    assets such as the fonts, and the launch scene
    of the application.

     @param stage is the stage of the application.
    ================================================
     */
    @Override
    public void start(Stage stage) throws IOException {

        //1. Loading of the light font
        URL lightFontUrl = getClass().getResource("fonts/SFTSchriftedSansTRIAL-Regular.ttf");

        if (lightFontUrl != null) {
            Font.loadFont(lightFontUrl.toExternalForm(), 12);
            System.out.println("Light font successfully loaded.");
        } else {
            System.err.println("CRITICAL ERROR: Could not find Light font.");
        }

        URL boldFontUrl = getClass().getResource("fonts/SFTSchriftedSansTRIAL-Bold.ttf");

        //2. Loading of the bold font
        if (boldFontUrl != null) {
            Font.loadFont(boldFontUrl.toExternalForm(), 12);
            System.out.println("Bold font successfully loaded.");
        } else {
            System.err.println("CRITICAL ERROR: Could not find Bold font.");
        }

        //3. This statement loads the first application state which is the launch scene.
        Parent root = FXMLLoader.load(getClass().getResource("main-launch.fxml"));

        Scene scene = new Scene(root, 1024, 768);
        stage.setTitle("UVR!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}