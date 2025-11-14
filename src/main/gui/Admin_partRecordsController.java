package main.gui;

import dao.PartDAO;
import model.Part;
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

public class Admin_partRecordsController implements Initializable {

    @FXML private Label countLabel;
    @FXML private TableView<Part> table;
    @FXML private TableColumn<Part, String> idColumn;
    @FXML private TableColumn<Part, String> nameColumn;
    @FXML private TableColumn<Part, Integer> stockColumn;
    @FXML private TableColumn<Part, BigDecimal> priceColumn;
    @FXML private TableColumn<Part, String> statusColumn;
    @FXML private TableColumn<Part, Void> editColumn;

    private PartDAO dao = new PartDAO();
    private Admin_dashboardController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("partId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("partName"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadData();
        setupEditButton();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadData() {
        List<Part> list = dao.getAllParts();
        ObservableList<Part> data = FXCollections.observableArrayList(list);
        table.setItems(data);
        countLabel.setText("(" + data.size() + ") PARTS:");
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        mainController.loadPartForm(null);
    }

    private void handleEdit(Part part) {
        mainController.loadPartForm(part);
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