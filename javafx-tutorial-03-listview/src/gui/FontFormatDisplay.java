package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.TextDescriptor;

//FontFormatDisplay nebude aplikace, ale dialogove okno pro editaci zaznamu v listview
public class FontFormatDisplay extends Stage{
    private Button printBT, endBT;
    private TextDescriptor textDescriptor;

    public TextDescriptor getTextDescriptor(){
        return textDescriptor;
    }

    public void display(TextDescriptor textDescriptor) {
        this.initModality(Modality.APPLICATION_MODAL);
        this.textDescriptor = textDescriptor; //pri zobrazeni dialogoveho okna se vytvori predany textDescriptor
        this.setTitle("Font dialog");
        this.setScene(new Scene(getRootPane(), 600, 250));
        this.show();
    }

    private Parent getRootPane() {
        VBox root = new VBox();
        root.getChildren().add(getFontLabelPane());
        root.getChildren().add(getFontControlPane());
        root.getChildren().add(getButtonsPane());
        return root;
    }

    private Node getButtonsPane() {
        HBox buttonsPane = new HBox(5);
        buttonsPane.setAlignment(Pos.CENTER);
        buttonsPane.setPadding(new Insets(20, 0, 0, 0));

        printBT = new Button("Print model");
        endBT = new Button("Close dialog");
        setButtonsActions();

        buttonsPane.getChildren().addAll(printBT, endBT);
        return buttonsPane;
    }

    private void setButtonsActions() {
        printBT.setOnAction(this::printAction);
        endBT.setOnAction(this::endAction);
    }

    private void printAction(ActionEvent event) {
        System.out.println(textDescriptor.toString());
    }

    private void endAction(ActionEvent event) {
        this.close();
    }

    private Node getFontControlPane() {
        HBox fontControlPane = new HBox(5);
        fontControlPane.setAlignment(Pos.CENTER);

        fontControlPane.getChildren().add(getFontSizeControl());
        fontControlPane.getChildren().add(getFontFamilyControl());
        fontControlPane.getChildren().add(getFontColorControl());
        return fontControlPane;
    }

    private Node getFontColorControl() {
        VBox fontColorControl = new VBox(5);
        fontColorControl.setAlignment(Pos.CENTER);

        Label fontColorControlLabel = new Label("Font Color");

        ColorPicker fontCP = new ColorPicker();
        fontCP.valueProperty().addListener(((observable, oldValue, newValue) -> textDescriptor.setFontColor(newValue))); //misto bindingu pridam posluchac nejake udalosti a v datovem modelu zavolam setr (v setru probehnou kontroly parametru a informuje posluchace o zmene barvy)
        //textDescriptor.fontColorProperty().addListener((observable, oldValue, newValue) -> fontCP.setValue(newValue));
        fontCP.setValue(textDescriptor.getFontColor());

        Rectangle rect = new Rectangle(50, 20);
        rect.setFill(textDescriptor.getFontColor());
        rect.fillProperty().bind(textDescriptor.fontColorProperty()); //obdelnik je provazan take s datovym modelem

        fontColorControl.getChildren().addAll(fontColorControlLabel, fontCP, rect);
        return fontColorControl;
    }

    private Node getFontFamilyControl() {
        VBox fontFamilyControl = new VBox(5);
        fontFamilyControl.setAlignment(Pos.CENTER);

        Label fontFamilyControlLabel = new Label("Font Family");

        ObservableList<String> fontTypes = FXCollections.observableArrayList(Font.getFamilies());
        ComboBox<String> fontsComboBox = new ComboBox<>(fontTypes);
        fontsComboBox.getSelectionModel().select(textDescriptor.getFontFamily());
        fontsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> textDescriptor.setFontFamily(newValue));
        textDescriptor.fontFamilyProperty().addListener((observable, oldValue, newValue) -> fontsComboBox.getSelectionModel().select(newValue));

        Label selectedFontFamilyLabel = new Label();
        selectedFontFamilyLabel.textProperty().bind(textDescriptor.fontFamilyProperty()); //provazani labelu font family s datovym modelem

        fontFamilyControl.getChildren().addAll(fontFamilyControlLabel, fontsComboBox, selectedFontFamilyLabel);
        return fontFamilyControl;
    }

    private Node getFontSizeControl() {
        VBox fontSizeControl = new VBox(5);
        fontSizeControl.setAlignment(Pos.CENTER);

        Label fontSizeControlLabel = new Label("Font Size");

        Slider fontSizeSlider = new Slider();
        fontSizeSlider.setMin(0);
        fontSizeSlider.setMax(120);
        fontSizeSlider.setValue(textDescriptor.getFontSize());
        fontSizeSlider.setShowTickLabels(true);
        fontSizeSlider.setShowTickMarks(true);
        fontSizeSlider.setMajorTickUnit(10);
        fontSizeSlider.setMinorTickCount(2);
        fontSizeSlider.setBlockIncrement(1);
        fontSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> textDescriptor.setFontSize(newValue.intValue()));
        textDescriptor.fontSizeProperty().addListener((observable, oldValue, newValue) -> fontSizeSlider.setValue(newValue.intValue()));

        Label selectedFontSizeLabel = new Label();
        selectedFontSizeLabel.textProperty().bind(textDescriptor.fontSizeProperty().asString()); //lable fontsize propojim s datovym modelem, prevod na string pomoci asString (protoze pro jednosmerny binding neni k dispozici NumberStringConverter)

        fontSizeControl.getChildren().addAll(fontSizeControlLabel, fontSizeSlider, selectedFontSizeLabel);
        return fontSizeControl;
    }

    private Node getFontLabelPane() {
        HBox labelPane = new HBox();
        labelPane.setAlignment(Pos.CENTER);

        ListeningLabel previewLB = new ListeningLabel();
        previewLB.setText(textDescriptor.previewText.get());
        previewLB.setFont(Font.font(textDescriptor.getFontFamily(), textDescriptor.getFontSize()));
        previewLB.setTextFill(textDescriptor.getFontColor());
        previewLB.setMinSize(200, 100);
        textDescriptor.addTextDescriptorListener(previewLB); //listening label se zaregistuje jako posluchac

        labelPane.getChildren().add(previewLB);
        return labelPane;
    }
}
