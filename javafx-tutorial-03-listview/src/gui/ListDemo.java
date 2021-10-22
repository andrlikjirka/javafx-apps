package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.DataModel;
import model.TextDescriptor;

import java.util.List;

public class ListDemo extends Application {

    //komponentu seznamu vytvorime jako atribut tridy, protoze s nim budeme pracovat v ruznych metodach
    private ListView<TextDescriptor> descriptorList;
    private final DataModel model = new DataModel();

    private ComboBox<String> fontNameCB;
    private Slider fontSizeSL;
    private ColorPicker fontColorPicker;

    private Label statusBar;

    //inicializacni metoda zdedena z Application, vola se pokazde kdyz spustime aplikaci
    public void init(){
        model.initializeModel(5);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("List Demo");
        primaryStage.setScene(new Scene(getRootPane(), 600, 400));
        primaryStage.show();
    }

    private Parent getRootPane() {
        BorderPane rootPane = new BorderPane();
        rootPane.setTop(getControlPane());
        rootPane.setCenter(getListPane());
        rootPane.setRight(getEditPane());
        rootPane.setBottom(getStatusBar());

        return rootPane;
    }

    private Node getStatusBar() {
        statusBar = new Label("Empty");
        statusBar.setPadding(new Insets(5));
        return statusBar;
    }

    private Node getEditPane() {
        GridPane editPane = new GridPane(); //tabulka indexovana po radcich a sloupcich (pridavani komponent pomoci metody pres indexy)
        editPane.setPadding(new Insets(10));
        editPane.setVgap(10);
        editPane.setHgap(10);

        Label fontFamilyLB = new Label("Font family");
        List<String> fonts = Font.getFamilies();
        fontNameCB = new ComboBox<>(FXCollections.observableArrayList(fonts)); //ComboBox potrebuje jako parametr ObservableList vytvoreny ze ziskaneho seznamu fontu
        fontNameCB.valueProperty().addListener((observable, oldValue, newValue) -> updateFamilyAction(newValue));
        editPane.add(fontFamilyLB, 0, 0);
        editPane.add(fontNameCB, 1, 0);

        Label fontSizeLB = new Label("Font size");
        fontSizeSL = new Slider();
        fontSizeSL.setMin(1);
        fontSizeSL.setMax(120);
        fontSizeSL.setValue(1);
        fontSizeSL.setShowTickLabels(true);
        fontSizeSL.setShowTickMarks(true);
        fontSizeSL.setMajorTickUnit(10);
        fontSizeSL.setMinorTickCount(2);
        fontSizeSL.setBlockIncrement(1);
        fontSizeSL.valueProperty().addListener((observable, oldValue, newValue) -> updateSizeAction(newValue));
        editPane.add(fontSizeLB, 0, 1);
        editPane.add(fontSizeSL, 1, 1);

        Label fontColorLB = new Label("Font color");
        fontColorPicker = new ColorPicker();
        fontColorPicker.setValue(Color.BLACK); //fontColorPicker.valueProperty().set(Color.BLACK);
        fontColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> updateColorAction(newValue));
        editPane.add(fontColorLB, 0, 2);
        editPane.add(fontColorPicker, 1, 2);

        Button commitBT = new Button("Commit data");
        commitBT.setOnAction(this::commitAction);
        editPane.add(commitBT, 0, 3, 2, 1);

        return editPane;
    }

    private void updateFamilyAction(String newValue) {
        TextDescriptor selectedD = descriptorList.getSelectionModel().getSelectedItem();
        if (selectedD != null) {
            selectedD.setFontFamily(newValue);
        }
    }

    private void updateColorAction(Color newValue) {
        TextDescriptor selectedD = descriptorList.getSelectionModel().getSelectedItem();
        if (selectedD != null){
            selectedD.setFontColor(newValue);
        }
    }

    private void updateSizeAction(Number newValue) {
        TextDescriptor selectedD = descriptorList.getSelectionModel().getSelectedItem();
        if (selectedD != null){
            selectedD.setFontSize(newValue.intValue());
        }
    }

    private void commitAction(ActionEvent event) {
        int selectedIndex = descriptorList.getSelectionModel().getSelectedIndex();
        TextDescriptor newDesc = new TextDescriptor();
        if (selectedIndex >= 0){
            newDesc.setFontSize((int)fontSizeSL.getValue());
            newDesc.setFontFamily(fontNameCB.getValue());
            newDesc.setFontColor(fontColorPicker.getValue());
            model.descriptors.set(selectedIndex, newDesc); //v modelu v seznamu descriptoru vymenim na danem indexu stary descriptor za nove vytvoreny
        } else {
            if (fontNameCB.getValue() != null){
                newDesc.setFontSize((int)fontSizeSL.getValue());
                newDesc.setFontFamily(fontNameCB.getValue());
                newDesc.setFontColor(fontColorPicker.getValue());
                model.descriptors.add(newDesc);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Update error");
                alert.setHeaderText("Cannot save data");
                alert.setContentText("Select a font before saving.");
                alert.show();
            }
        }
    }

    private Node getListPane() {
        descriptorList = new ListView<>(model.descriptors.get()); //ListView se vytvari kolem ObservableList, nas datovy model (ListProperty) dokaze vratit sam Observable List
        descriptorList.setPadding(new Insets(5));
        //zmenu formatu vypisu provedeme v nastaveni ListView:
        descriptorList.setCellFactory(TextFieldListCell.forListView(new StringConverter<TextDescriptor>() {
            @Override
            public String toString(TextDescriptor object) {
                return object.getFontFamily() + " (" + object.getFontSize() + "): " + object.getFontColor();
            }

            @Override
            public TextDescriptor fromString(String string) { //metodu fromString nebudeme pouzivat (nechceme ze Stringu vytvaret Font)
                return null;
            }
        }));

        //jakmile se zmeni vyber, zmeni se text statusbaru
        descriptorList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateSelectionAction());

        return  descriptorList;
    }

    private void updateSelectionAction() {
        TextDescriptor descriptor = descriptorList.getSelectionModel().getSelectedItem();
        statusBar.textProperty().unbind(); //pred kazdou reakci na zmenu je nutne statusbar odbindovat (aby se mohl nabindovat na novou vybrany textdescriptor)
        if (descriptor != null){ //pokud je neco vybrano
            statusBar.textProperty().bind(descriptor.previewText); //provazu textproperty statusbaru s previewTextem descriptoru

            fontNameCB.getSelectionModel().select(descriptor.getFontFamily());
            fontSizeSL.valueProperty().set(descriptor.getFontSize());
            fontColorPicker.valueProperty().set(descriptor.getFontColor());

        } else {
            statusBar.textProperty().set("Empty");
            //pri zruseni vyberu se komponenty vrati do default nastaveni
            fontNameCB.getSelectionModel().clearSelection();
            fontSizeSL.valueProperty().set(1);
            fontColorPicker.valueProperty().set(Color.BLACK);
        }
    }

    private Node getControlPane() {
        HBox controlPane = new HBox(10);
        controlPane.setAlignment(Pos.CENTER);
        controlPane.setPadding(new Insets(10));

        Button exitBT = new Button("End app");
        exitBT.setOnAction(event -> Platform.exit());

        Button addRandomBT = new Button("Add random");
        addRandomBT.setOnAction(this::addRandomAction);

        Button deleteBT = new Button("Delete");
        deleteBT.setOnAction(this::deleteAction);

        Button showDialogBT = new Button("Edit in dialog");
        showDialogBT.setOnAction(this::showInDialogAction);

        Button showDialogSeparateBT = new Button("Edit separate");
        showDialogSeparateBT.setOnAction(this::showInDialogSeparateAction);

        Button unselectBT = new Button("Unselect");
        unselectBT.setOnAction(this::unselectAction);

        controlPane.getChildren().addAll(exitBT, addRandomBT, deleteBT, showDialogBT, showDialogSeparateBT, unselectBT);
        return controlPane;
    }

    private void unselectAction(ActionEvent actionEvent) {
        descriptorList.getSelectionModel().clearSelection();
    }

    private void showInDialogSeparateAction(ActionEvent actionEvent) {
        TextDescriptor selectedDesc = descriptorList.getSelectionModel().getSelectedItem();
        if (selectedDesc != null){
            FontFormatDisplay dialog = new FontFormatDisplay();

            dialog.setOnHidden(event -> saveDataFromDialogAction(dialog.getTextDescriptor())); //akce skryti dialogu vyvola metodu, ktera bude ukladat data

            dialog.display(selectedDesc.clone()); //do dialogu predam klon (hlubokou kopii) vybraneho textdescriptoru
        } else {
            showAlertForDialog();
        }
    }

    private void saveDataFromDialogAction(TextDescriptor textDescriptor) {
        int selectedIndex = descriptorList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0){
            model.descriptors.set(selectedIndex, textDescriptor);
        }
    }


    private void showInDialogAction(ActionEvent event) {
        TextDescriptor selectedDesc = descriptorList.getSelectionModel().getSelectedItem();
        if (selectedDesc != null){
            FontFormatDisplay dialog = new FontFormatDisplay();
            dialog.display(selectedDesc);
        } else {
            showAlertForDialog();
        }
    }

    private void showAlertForDialog(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Dialog error");
        alert.setHeaderText("Dialog can´t be shown");
        alert.setContentText("Select a font before editing in dialog.");
        alert.show();
    }

    private void deleteAction(ActionEvent event) {
        int index = descriptorList.getSelectionModel().getSelectedIndex();
        if (index >= 0){
            model.descriptors.remove(index);
            descriptorList.getSelectionModel().clearSelection(); //po mazani nebude nic vybrano
        }
    }

    private void addRandomAction(ActionEvent event) {
        TextDescriptor randDesc = TextDescriptor.generateRandomDescriptor();
        model.descriptors.add(randDesc);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
