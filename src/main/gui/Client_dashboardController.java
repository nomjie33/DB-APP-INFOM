package main.gui;

import dao.PenaltyDAO;
import dao.RentalDAO;
import model.Customer;
import model.RentalTransaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Client_dashboardController implements Initializable {

    @FXML private AnchorPane centerContentPane;
    @FXML private Button quitButton;

    @FXML private HBox homeButton;
    @FXML private HBox settingsButton;
    @FXML private HBox rentButton;
    @FXML private HBox resolveButton;
    @FXML private HBox historyButton;

    private List<HBox> navButtons;
    private Customer loggedInCustomer;

    private PenaltyDAO penaltyDAO = new PenaltyDAO();
    private RentalDAO rentalDAO = new RentalDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        navButtons = List.of(homeButton, settingsButton, rentButton, resolveButton, historyButton);
    }

    public void initData(Customer customer){
        this.loggedInCustomer = customer;
        handleHome(null);
    }

    private void setActiveNav(HBox activeButton) {
        if (navButtons == null) return;
        for (HBox button : navButtons) {
            button.getStyleClass().remove("nav-button-active");
        }
        activeButton.getStyleClass().add("nav-button-active");
    }

    @FXML
    public void handleHome(MouseEvent event) {
        System.out.println("Home clicked");
        setActiveNav(homeButton);
        loadPage("Client-home.fxml");
    }

    @FXML
    public void handleSettings(MouseEvent event) {
        System.out.println("Settings clicked");
        setActiveNav(settingsButton);
        loadPage("Client-settings.fxml");
    }

    @FXML
    public void handleRentVehicle(MouseEvent event) {
        System.out.println("--- Rent a Vehicle clicked ---");
        setActiveNav(rentButton);

        if (loggedInCustomer == null) {
            System.out.println("ERROR: loggedInCustomer is NULL in dashboard. Aborting.");
            return;
        }

        if (penaltyDAO.hasUnpaidPenalties(loggedInCustomer.getCustomerID())) {
            System.out.println("BLOCK: User has unpaid penalties.");
            showPenaltyBlockDialog();
            return;
        }

        setActiveNav(rentButton);
        System.out.println("Checking for active rental for customer: " + loggedInCustomer.getCustomerID());

        List<RentalTransaction> allCustomerRentals = rentalDAO.getRentalsByCustomer(loggedInCustomer.getCustomerID());
        RentalTransaction activeRental = null;

        for (RentalTransaction rental: allCustomerRentals){
            if (rental.isOngoing()){
                activeRental = rental;
                break;
            }
        }

        if (activeRental != null) {
            System.out.println("INFO: Found active rental. Loading return/pending screen.");
            loadReturnScene(activeRental);
        } else {

            System.out.println("INFO: NO active rental found. Loading rent scene.");
            loadPage("Client-rent.fxml");
        }
    }

    private void showPenaltyBlockDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Client-penaltyDialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Action Required");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(quitButton.getScene().getWindow());

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            Client_penaltyDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainController(this);

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleResolvePenalty(MouseEvent event) {
        System.out.println("Resolve a Penalty clicked");
        setActiveNav(resolveButton);
        loadPage("Client-resolve.fxml");
    }

    @FXML
    public void handleTransactionHistory(MouseEvent event) {
        System.out.println("Transaction History clicked");
        setActiveNav(historyButton);
        loadPage("Client-history.fxml");
    }

    @FXML
    void handleLogout(ActionEvent event) {
        System.out.println("Logout/Quit clicked");
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("Main-login.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage currentStage = (Stage) quitButton.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.setTitle("U.V.R! - Select Role");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent page = loader.load();

            Object controller = loader.getController();

            if (controller instanceof Client_homeController) {
                Client_homeController homeController = (Client_homeController) controller;
                homeController.setMainController(this);
                homeController.initData(this.loggedInCustomer);

            } else if (controller instanceof Client_rentController) {
                Client_rentController rentController = (Client_rentController) controller;
                rentController.setMainController(this);
                rentController.initData(this.loggedInCustomer);

            }

            else if (controller instanceof Client_paymentController) {
                Client_paymentController paymentController = (Client_paymentController) controller;
                paymentController.setMainController(this);

            } else if (controller instanceof Client_deploymentController) {
                Client_deploymentController deploymentController = (Client_deploymentController) controller;
                deploymentController.setMainController(this);
            }

            else if (controller instanceof Client_resolveController){
                Client_resolveController resolveController = (Client_resolveController) controller;
                resolveController.setMainController(this);
                resolveController.initData(this.loggedInCustomer);
            }

            else if (controller instanceof Client_historyController){
                Client_historyController historyController = (Client_historyController) controller;
                historyController.setMainController(this);
                historyController.initData(this.loggedInCustomer);
            }

            else if (controller instanceof Client_settingsController){
                Client_settingsController settingsController = (Client_settingsController) controller;
                settingsController.setMainController(this);
                settingsController.initData(this.loggedInCustomer);
            }

            centerContentPane.getChildren().clear();
            centerContentPane.getChildren().add(page);

            AnchorPane.setTopAnchor(page, 0.0);
            AnchorPane.setBottomAnchor(page, 0.0);
            AnchorPane.setLeftAnchor(page, 0.0);
            AnchorPane.setRightAnchor(page, 0.0);

        } catch (IOException e) {
            System.err.println("Failed to load page: " + fxmlFile);
            e.printStackTrace();
        }
    }

    private void loadReturnScene(RentalTransaction activeRental) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Client-returnVehicle.fxml"));
            Parent page = loader.load();

            Client_returnVehicleController returnController = loader.getController();
            returnController.initData(loggedInCustomer, activeRental);
            returnController.setMainController(this);

            loadPageFromSub(page);

        } catch (IOException e) {
            System.err.println("Failed to load page: Client-returnVehicle.fxml");
            e.printStackTrace();
        }
    }

    public void loadPageFromSub(Parent page) {
        centerContentPane.getChildren().clear();
        centerContentPane.getChildren().add(page);

        AnchorPane.setTopAnchor(page, 0.0);
        AnchorPane.setBottomAnchor(page, 0.0);
        AnchorPane.setLeftAnchor(page, 0.0);
        AnchorPane.setRightAnchor(page, 0.0);
    }
}