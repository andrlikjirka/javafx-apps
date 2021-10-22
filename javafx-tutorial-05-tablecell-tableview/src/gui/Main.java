package gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import model.DataModel;
import model.Ship;
import model.ShipType;
import utils.CheckedSimpleDoubleProperty;
import utils.DeleteConsumingTextFieldTableCell;
import utils.Messenger;
import utils.MyLocalDateStringConverter;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class Main extends Application {
    TextField nameTF, displacementTF;
    DatePicker commissionedDP, retiredDP;
    ComboBox<ShipType> typeCB;

    private DataModel model;
    private TableView<Ship> shipTableView;
    private SelectionModel<Ship> shipSelectionModel; //umozni pracovat s vyberem ve vsech metodach

    //messenger indikujici spatne zadanou hodnotu displacement
    private final Messenger DISPLACEMENT_EDIT_MESSENGER = new Messenger() {
        @Override
        public void sendMessage(String message) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Edit error");
            a.setHeaderText("Displacement value is not allowed.");
            a.setContentText(message);
            a.showAndWait();
        }
    };

    @Override
    public void init() throws Exception {
        super.init();
        model = new DataModel();
        model.populate();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Table example");
        primaryStage.setScene(new Scene(getRootPane(), 950, 400));
        primaryStage.show();
    }

    private Parent getRootPane() {
        BorderPane rootPane = new BorderPane();
        rootPane.setPadding(new Insets(10));
        rootPane.setCenter(getTablePane());
        rootPane.setBottom(getControlPane());
        rootPane.setRight(getDataReviewPane());
        return rootPane;
    }

    private Node getDataReviewPane() {
        VBox dataReviewPane = new VBox(10);
        dataReviewPane.setAlignment(Pos.CENTER);
        dataReviewPane.setPadding(new Insets(10));

        Button printSelectedBT = new Button("Print Selected");
        printSelectedBT.setOnAction(this::printSelectedAction);

        Button deleteSelectedBT = new Button("Delete Selected");
        deleteSelectedBT.setOnAction(this::deleteSelectedAction);

        Button setDateBT = new Button("Retire ship");
        setDateBT.setOnAction(this::retireSelectedAction);

        Button zeroDisplacementBT = new Button("Zero displacement");
        zeroDisplacementBT.setOnAction(this::zeroDisplacementAction);

        Button refreshScreenBT = new Button("Refresh");
        refreshScreenBT.setOnAction(this::refreshAction);

        dataReviewPane.getChildren().addAll(printSelectedBT, deleteSelectedBT, setDateBT, zeroDisplacementBT, refreshScreenBT);
        dataReviewPane.getChildren().forEach(node -> ((Region)node).setPrefWidth(120));
        return dataReviewPane;
    }

    private void refreshAction(ActionEvent event) {
        shipTableView.refresh();
    }

    private void zeroDisplacementAction(ActionEvent event) {
        Ship selected = shipSelectionModel.getSelectedItem();
        if (selected != null){
            selected.setDisplacement(Double.parseDouble("0.0"));
        }
    }

    private void retireSelectedAction(ActionEvent event) {
        Ship selected = shipSelectionModel.getSelectedItem();
        if (selected != null){
            selected.setRetired(LocalDate.now());
        }
    }

    private void deleteSelectedAction(ActionEvent event) {
        int index = shipSelectionModel.getSelectedIndex();
        if (index >= 0){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete confirmation");
            alert.setHeaderText("Do you really want to this record?");
            alert.setContentText(model.ships.get(index).toString());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.orElse(ButtonType.CANCEL) == ButtonType.OK){
                System.out.println("Deleted record: " + model.ships.get(index).getName());
                model.ships.remove(index);
            }
        } else {
            System.out.println("Nothing to delete");
        }
    }

    private void printSelectedAction(ActionEvent event) {
        Ship selected = shipSelectionModel.getSelectedItem();

        if (selected != null){
            System.out.println(selected.toString());
        } else {
            System.out.println("Nothing selected");
        }
    }

    private Node getControlPane() {
        GridPane controlPane = new GridPane();
        controlPane.setPadding(new Insets(10));
        controlPane.setVgap(5);
        controlPane.setHgap(5);

        Label nameLB = new Label("Ship name");
        controlPane.add(nameLB, 0, 0);

        nameTF = new TextField();
        controlPane.add(nameTF, 0, 1);

        Label displacementLB = new Label("Displacement");
        controlPane.add(displacementLB, 1, 0);

        displacementTF = new TextField();
        displacementTF.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0));
        controlPane.add(displacementTF, 1, 1);

        Label commissionedLB = new Label("Commissioned");
        controlPane.add(commissionedLB, 2, 0);

        commissionedDP = new DatePicker(null);
        controlPane.add(commissionedDP, 2, 1);

        Label retiredLB = new Label("Retired");
        controlPane.add(retiredLB, 3, 0);

        retiredDP = new DatePicker(null);
        controlPane.add(retiredDP, 3, 1);

        Label typeLB = new Label("Ship type");
        controlPane.add(typeLB, 4, 0);

        typeCB = new ComboBox<>(FXCollections.observableArrayList(ShipType.values()));
        controlPane.add(typeCB, 4, 1);

        Button addBT = new Button("Add");
        addBT.setOnAction(this::addNewShipAction);
        controlPane.add(addBT, 5, 0, 1, 2);

        Button clearBT = new Button("Clear form");
        clearBT.setOnAction(this::clearFormAction);
        controlPane.add(clearBT, 6, 0, 1, 2);

        return controlPane;
    }

    private void clearFormAction(ActionEvent event) {
        nameTF.setText("");
        displacementTF.setText("0.0");
        commissionedDP.setValue(null);
        retiredDP.setValue(null);
        typeCB.setValue(null);
    }

    private void addNewShipAction(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Input error");

        if (nameTF.getText().trim().length() == 0) {
            a.setHeaderText("Error in name");
            a.setContentText("You have to provide some ship name");
            a.show();
            return;
        }
        if (Double.parseDouble(displacementTF.getText()) < 0) {
            a.setHeaderText("Error in displacement");
            a.setContentText("The displacement has to be a positive number");
            a.show();
            return;
        }

        LocalDate commissioned = commissionedDP.getValue();
        LocalDate retired = retiredDP.getValue();
        if (commissioned == null) {
            a.setHeaderText("Error in commissioned date");
            a.setContentText("You have to provide a commissioned date");
            a.show();
            return;
        }
        if ((retired != null) && ((retired.isBefore(commissioned)))) {
            a.setHeaderText("Error in retired date");
            a.setContentText("Retired date has to be after commissioned date");
            a.show();
            return;
        }
        if (typeCB.getValue() == null) {
            a.setHeaderText("Error in ship type");
            a.setContentText("You have to select a ship type");
            a.show();
            return;
        }
        try{
            model.ships.add(new Ship(nameTF.getText(), Double.parseDouble(displacementTF.getText()), commissioned, retired, typeCB.getValue()));
        } catch (IllegalArgumentException e ){
            a.setHeaderText("Another error");
            a.setContentText("Data were not added, " + e.getMessage());
            a.show();
        }
    }

    private Node getTablePane() {
        shipTableView = new TableView<>(model.ships.get()); //sparovani tableView s datovym modelem
        shipSelectionModel = shipTableView.getSelectionModel();
        shipTableView.setEditable(true);

        TableColumn<Ship, String> nameColumn = new TableColumn<>("Ship name"); //(typ radku, typ sloupce)
        nameColumn.setCellValueFactory(new PropertyValueFactory<Ship, String>("name")); //nastaveni tovarny na data; setCellValueFactory je tovarna zodpovedna za ziskani dat pro bunku; binding se provadi automaticky
        nameColumn.setCellFactory(DeleteConsumingTextFieldTableCell.forTableColumn()); //nastaveni tovarny na bunky, bunka je zalozena na textfieldu

        TableColumn<Ship, Double> displacementColumn = new TableColumn<>("Displacement");
        displacementColumn.setCellValueFactory(new PropertyValueFactory<Ship, Double>("displacement"));
        //displacementColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        displacementColumn.setCellFactory(cellData -> new FormattedDoubleTableCell<>("t", CheckedSimpleDoubleProperty.POSITIVE_DOUBLE_PREDICATE, DISPLACEMENT_EDIT_MESSENGER)); //tovarna je jen metoda ktera vytvari instance, predan predicat ktery kontroluje ze cislo je od nula do Double.max (viz CheckedSimpleDoubleProperty)

        TableColumn<Ship, LocalDate> commissionedColumn = new TableColumn<>("Commissioned");
        commissionedColumn.setCellValueFactory(new PropertyValueFactory<Ship, LocalDate>("commissioned")); //commissioned property je read only, nedojde k bindingu, je nutne na zmenu bunky reagovat
        commissionedColumn.setCellFactory(cellData -> new DeleteConsumingTextFieldTableCell<>(new MyLocalDateStringConverter(Ship.FORMATTER)));
        commissionedColumn.setOnEditCommit(this:: commitCommissionedDateAction); //jelikoz se touto property (wrapper) nelze udelat binding, je nutne rucne reagovat na zmenu (tuto akci vyvola tabulka pokazde kdy dojde k uspesnemu commitu hodnoty)

        TableColumn<Ship, LocalDate> retiredColumn = new TableColumn<>("Retired");
        retiredColumn.setCellValueFactory(new PropertyValueFactory<Ship, LocalDate>("retired"));
        retiredColumn.setCellFactory(DeleteConsumingTextFieldTableCell.forTableColumn(new MyLocalDateStringConverter(Ship.FORMATTER)));
        retiredColumn.setOnEditCommit(this:: commitRetiredDateAction);

        TableColumn<Ship, ShipType> typeColumn = new TableColumn<>("Ship type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<Ship, ShipType>("type"));
        typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(ShipType.values())); //bunka zalozena na ComboBoxu s// hodnotami enumu, pres binding provazana s datovym modelem

        TableColumn<Ship, Period> serviceTimeColumn = new TableColumn<Ship, Period>("Service time");
        serviceTimeColumn.setCellValueFactory(cellData -> cellData.getValue().serviceTimeProperty());
        serviceTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Period>() {
            @Override
            public String toString(Period object) {
                if (object != null) {
                    return object.getYears() + " years, " + object.getMonths() + " months, " + object.getDays() + " days";
                } else {
                    return "Still in service";
                }
            }

            @Override
            public Period fromString(String string) {
                return null;
            }
        }));
        serviceTimeColumn.setEditable(false);
        serviceTimeColumn.setComparator(new Comparator<Period>() { //trida Period nema prirozeny komparator, aby slo casy radit, musime doimplementovat komparator
            @Override
            public int compare(Period p1, Period p2) {
                if ((p1 == null) && (p2 == null)){ //p1 a p2 jsou stejne (null)
                    return 0;
                }
                if (p1 == null){
                    return 1;
                }
                if (p2 == null){
                    return -1;
                }
                return toDays(p1) - toDays(p2); //komparator vrati 0 pokud jsou periody stejne, kladne cislo pokud je p1>p2, zaporne pokud p1<p2
            }
            private int toDays(Period period){
                return period.getDays() + period.getMonths()*30 + period.getYears()*365; //hruby odhad celkoveho poctu dni serviceTime
            }
        });

        //nameColumn.prefWidthProperty().bind(shipTableView.widthProperty().multiply(0.5)); // take moznost jak nastavit sirku sloupce (binding k sirce cele tabulky krat koef)
        shipTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //parametr je prepripravena trida, ktera je schopna prepocitavat sirku sloupcu tak, aby vyplnily cely prostor tabulky
        shipTableView.getColumns().addAll(nameColumn, displacementColumn, commissionedColumn, retiredColumn, typeColumn, serviceTimeColumn);

        shipTableView.setOnKeyReleased(event -> { //mazani zaznamu po stisku DELETE (ale kdyz jsem v editaci bunky, delete nesmi vest k mazani zaznamu => nutno doresit ve tride vlastni bunky)
            if (event.getCode().equals(KeyCode.DELETE)){
                deleteSelectedAction(null);
            }
        });

        return shipTableView;
    }

    private void commitRetiredDateAction(TableColumn.CellEditEvent<Ship, LocalDate> event) {
        try {
            event.getRowValue().setRetired(event.getNewValue());
        } catch (IllegalArgumentException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Wrong date");
            alert.setHeaderText("Wrong retired date");
            alert.setContentText(e.getMessage());
            alert.show();

            event.getRowValue().setRetired(event.getOldValue());
        }
    }

    private void commitCommissionedDateAction(TableColumn.CellEditEvent<Ship, LocalDate> event) {
        try {
            event.getRowValue().setCommissioned(event.getNewValue());
        } catch (IllegalArgumentException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Wrong date");
            alert.setHeaderText("Wrong commissioned date");
            alert.setContentText(e.getMessage());
            alert.show();

            event.getRowValue().setCommissioned(event.getOldValue()); //po zobrazeni alertu vratim starou (platnou) hodnotu
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
