/*
=====================================
This code displays four options:
1. Admin login
2. Client login
4. Client signup
3. Quit

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
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Main_loginController implements Initializable {

    @FXML private VBox adminLoginButton;
    @FXML private VBox clientLoginButton;
    @FXML private VBox quitButton;
    @FXML private VBox clientSignupButton;

    private String title = "UVR!";

    /**
     =====================================================================
     This function adds a sliding transition to the logo.
     @param node is a verticalBox that contains the logo and user-options
     in this scene.
     @param delay determines the delay of the transition
     =====================================================================
     */
    private ParallelTransition slideToTop(VBox node, int delay){

        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setToValue(2.0);

        TranslateTransition tt = new TranslateTransition(Duration.millis(500), node);
        tt.setToY(0);

        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.setDelay(Duration.millis(delay));

        return pt;
    }

    @Override public void initialize(URL url, ResourceBundle rb){

        //Option 1: Admin login
        adminLoginButton.setOpacity(0.0);
        adminLoginButton.setTranslateY(50);

        //Option 2: Client login
        clientLoginButton.setOpacity(0.0);
        clientLoginButton.setTranslateY(50);

        //Option 3: Client sign-up
        clientSignupButton.setOpacity(0.0);
        clientSignupButton.setTranslateY(50);

        //Option 4: Quit App
        quitButton.setOpacity(0.0);
        quitButton.setTranslateY(50);

        //Animations
        ParallelTransition animAdmin = slideToTop(adminLoginButton, 0);
        ParallelTransition animClient = slideToTop(clientLoginButton, 0);
        ParallelTransition animSignup = slideToTop(clientSignupButton, 0);
        ParallelTransition animQuit = slideToTop(quitButton, 0);

        //Play animations
        animAdmin.play();
        animClient.play();
        animSignup.play();
        animQuit.play();
    }

    /**
    ===========================================
    This function calls the Admin login scene
    when the admin login icon is clicked.
     @param e listens to the mouse action of the
     user.
    ===========================================
     */
    @FXML private void handleAdminLogin(MouseEvent e){
        System.out.println("Admin login clicked.");
        loadNextScene("Admin-login.fxml", title, adminLoginButton);
    }

    /**
    ===========================================
    This function calls the Client login scene
    when the client login icon is clicked.
     @param e listens to the mouse action of the
     user.
    ===========================================
     */
    @FXML private void handleClientLogin(MouseEvent e){
        System.out.println("Client login clicked.");
        loadNextScene("Client-login.fxml", title, clientLoginButton);
    }

    /**
     ==============================================
     This function calls the Client signup scene
     when the client signup icon is clicked.
     @param e listens to the mouse action of the
     user.
     ==============================================
     */
    @FXML private void handleClientSignup(MouseEvent e){
        System.out.println("Client sign up clicked.");
        loadNextScene("Client-signup.fxml", title, clientSignupButton);
    }


    /**
    ============================================
    This function makes the app terminate after
    the quit icon is clicked.
     @param e listens to the mouse action of the
     user.
    ============================================
     */
    @FXML private void handleQuit(MouseEvent e){
        System.out.println("Quit app clicked.");
        Platform.exit();
    }

    /**
     ==================================================================
     This function loads a new FXML file depending on what is clicked.
     @param fxmlFile The name of the .fxml file to load
     @param title The title will be UVR! regardless
     @param node This is the node for the current stage
     ==================================================================
     */
    private void loadNextScene(String fxmlFile, String title, VBox node){

        try {

            Parent nextSceneRoot = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene nextScene = new Scene(nextSceneRoot);
            Stage currentStage = (Stage) node.getScene().getWindow();

            currentStage.setScene(nextScene);
            currentStage.setTitle(title);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to lead new scene: " + fxmlFile);
        }
    }
}
