package main.gui;

import dao.LocationDAO;
import model.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_locationRecordsController implements  Initializable{

    @FXML private Label countLabel;
    @FXML private TableView<Location> table;
    @FXML private TableColumn<Location, String> idColumn;
    @FXML private TableColumn<Location, String> nameColumn;
    @FXML private TableColumn<Location, String> statusColumn;
    @FXML private TableColumn<Location, Void> editColumn;

    private LocationDAO dao = new LocationDAO();
    private Admin_dashboardController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("locationID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadData();
        setupEditButton();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private  void loadData(){
        List<Location> list = dao.getAllLocations();
        ObservableList<Location> data = FXCollections.observableArrayList(list);
        table.setItems(data);
        countLabel.setText("(" + data.size() + ") LOCATIONS:");
    }

    @FXML
    private void handleAdd(){
        mainController.loadLocationForm(null);
    }

    private void handleEdit(Location loc){
        mainController.loadLocationForm(loc);
    }

    private void setupEditButton() {
        editColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Edit");
            {
                btn.getStyleClass().add("edit-button");
                btn.setOnAction(event -> handleEdit(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }
}


