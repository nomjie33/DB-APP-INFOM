package main.gui;

import dao.StaffDAO;
import model.Staff;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.ResourceBundle;

public class Admin_profileController implements Initializable {

    private Admin_dashboardController mainController;
    private Staff loggedInStaff;

    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void initData(Staff staff){
        this.loggedInStaff = staff;
        if (staff != null) {
            System.out.println("Profile Controller received data for Admin: " + staff.getUsername());
            loadProfileData();
        }
    }

    private void loadProfileData(){
        if (this.loggedInStaff == null) return;

        String username = loggedInStaff.getUsername();
        String email = loggedInStaff.getStaffEmail();

        usernameLabel.setText(username);
        emailLabel.setText(email);
    }

}


