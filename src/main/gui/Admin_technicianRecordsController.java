package main.gui;

import dao.TechnicianDAO;
import model.Technician;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_technicianRecordsController implements Initializable{

    @FXML private Label countLabel;
    @FXML private TableView<Technician> table;
    @FXML private TableColumn<Technician, String> idColumn;
    @FXML private TableColumn<Technician, String> lastNameColumn;
    @FXML private TableColumn<Technician, String> firstNameColumn;
    @FXML private TableColumn<Technician, String> specIdColumn;
    @FXML private TableColumn<Technician, BigDecimal> rateColumn;
    @FXML private TableColumn<Technician, String> contactColumn;
    @FXML private TableColumn<Technician, Void> editColumn;

    private TechnicianDAO technicianDAO = new TechnicianDAO();
    private Admin_dashboardController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("technicianId"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        specIdColumn.setCellValueFactory(new PropertyValueFactory<>("specializationId"));
        rateColumn.setCellValueFactory(new PropertyValueFactory<>("rate"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));

        loadData();
        setupEditButton();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void handleEditTechnician(Technician tech) {
        mainController.loadTechnicianForm(tech);
    }

    @FXML private void handleAdd(ActionEvent event) {
        mainController.loadTechnicianForm(null);
    }

    private void loadData() {
        List<Technician> list = technicianDAO.getAllTechnicians();
        ObservableList<Technician> data = FXCollections.observableArrayList(list);
        table.setItems(data);
        countLabel.setText("(" + data.size() + ") TECHNICIANS:");
    }

    private void setupEditButton() {
        Callback<TableColumn<Technician, Void>, TableCell<Technician, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Technician, Void> call(final TableColumn<Technician, Void> param) {
                final TableCell<Technician, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Edit");
                    {
                        btn.getStyleClass().add("edit-button");
                        btn.setOnAction((ActionEvent event) -> {
                            Technician technician = getTableView().getItems().get(getIndex());
                            handleEditTechnician(technician);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        editColumn.setCellFactory(cellFactory);
    }
}
