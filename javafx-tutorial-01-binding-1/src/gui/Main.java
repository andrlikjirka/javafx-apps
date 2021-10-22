package gui;

import controller.Controller;
import dataModel.Animal;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

public class Main extends Application {
    private final TextField energyField = new TextField(); //abych mel z kazde metody pristup k atributu TextField, je lepsi si ho vytvorit jako globalni promennou tridy
    private final TextField xField = new TextField("");
    private final TextField yField = new TextField("");

    private final Animal myAnimal = new Animal("Eagle", 10, 0, 0); //reference, ktera propoji view a datovy model
    private final Controller controller = new Controller(myAnimal);

    /**
     * JavaFX zacina beh tim, ze vytvori prvni okno (primaryStage) a preda ho do metody start.
     * V metode start() se zacina vytvaret to, co chceme videt na obrazovce.
     * JavaFX nam poskytla primarni okno, my ho musime vyplnit nejakym obsahem.
     * Stage je pouze prazdne zakladni okno (ramecek), cely vnitrek uvnitr okna se oznacuje jako scena (Scene).
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Animal Window - Jiri Andrlik");
        primaryStage.setScene(new Scene(getRootPane(), 500, 250)); //v okne vytvorime scenu (scena potrebuje nejaky korenovy element)
        primaryStage.show(); //cele okno chceme zobrazit
    }

    /**
     * Metoda vytvari hlavni okno aplikace
     */
    private Parent getRootPane() { //metoda vraci instanci tridy Parent (predka vsech Nodes)
        VBox rootPane = new VBox();
        rootPane.getChildren().add(getDataPane()); //do VBoxu potrebujeme pridavat dalsi komponenty, aby metoda nebyla dlouha vytvorime a zavolame dalsi metodu na vytvareni komponent
        rootPane.getChildren().add(getControlPane());
        return rootPane; //VBox je potomek Parent, muzeme ho vratit
    }

    private Node getDataPane() { //komponenty vkladane do hlavniho okna Parent se jiz vkladaji jako Node
        VBox dataPane = new VBox();
        dataPane.setAlignment(Pos.CENTER);
        dataPane.setPadding(new Insets(20, 0, 0, 0));

        Label nameLb = new Label(); //label potrebuji provazat s jmenem v datovem modelu (ne jenom natvrdo vypsat)
        nameLb.textProperty().bindBidirectional(myAnimal.name); //provazani property labelu s property nazvem v datovem modelu (obousmerne)

        nameLb.setFont(Font.font("System", FontWeight.BOLD,20));
        HBox animalDataPane = getAnimalDataPane();

        dataPane.getChildren().addAll(nameLb, animalDataPane);
        return dataPane;

    }

    private HBox getAnimalDataPane() {
        HBox animalDataPane = new HBox(5); //parametr spacing ovlivňuje mezeru mezi položkami HBoxu
        animalDataPane.setPadding(new Insets(10, 5, 10, 5));
        animalDataPane.setAlignment(Pos.CENTER);
        VBox energyPane = new VBox();
        energyPane.getChildren().addAll(new Label("Energy"), energyField);
        energyField.textProperty().bindBidirectional(myAnimal.energy, new NumberStringConverter()); //textProperty textFieldu provazeme s energii v datovem modelu, ale energie je IntegerProperty, proto vyuzijeme jako druhy parametr Converter Integer na String
        energyField.setEditable(false); //lze zakazat uzivateli psat do textfieldu a editovat ho (jednoduche zabraneni nedovoleneho meneni hodnot modelu)

        VBox xPane = new VBox();
        xPane.getChildren().addAll(new Label("X coord"), xField);
        xField.textProperty().bindBidirectional(myAnimal.xCoord, new NumberStringConverter(){ //vyuzitim anonymni vnitrni tridy mohu vytvorit potomka NumberStringConverter a prekryt mu metody toString a fromString, ktere se staraji o prevod int na string. Tim mohu chytit napriklad vyjimku pri parsovani neplatne hodnoty, ktera jinak vznika mimo moji tridu.
            @Override
            public String toString(Number value) {
                return super.toString(value);
            }

            @Override
            public Number fromString(String value) {
                try{
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return myAnimal.xCoord.get();
                }
            }
        });

        VBox yPane = new VBox();
        yPane.getChildren().addAll(new Label("Y coord"), yField);
        //yField.textProperty().bindBidirectional(myAnimal.yCoord, new NumberStringConverter());
        StringConverter<Number> converter = new NumberStringConverter();
        TextFormatter<Number> formatter = new TextFormatter(converter, 0);
        yField.setTextFormatter(formatter); //yField textova bunka je spojena s formaterem, skrz convertor je schopen konvertovat cisla na string, a skrz formatter je schopen z nich vyrabet stringy
        formatter.valueProperty().bindBidirectional(myAnimal.yCoord); //diky tomuto pristupu, lze prepsat hodnotu textfieldu s kontrolou, ze pokud nepujde hodnotaa prekonvertovat na cislo, tak zustane na predchozi hodnote

        animalDataPane.getChildren().addAll(energyPane, xPane, yPane);
        return animalDataPane;
    }

    private Node getControlPane() {
        HBox controlPane = new HBox(10);
        controlPane.setAlignment(Pos.CENTER);

        Button eatBT = new Button("Eat");
        eatBT.setOnAction(controller::eatAcion); //event -> eatAcion(event), pouziti reference na metodu = v teto tride (this) existuje metoda eatAction

        Button moveBT = new Button("Move");
        moveBT.setOnMouseClicked(controller::moveAction);

        Button printBT = new Button("Print");
        printBT.setOnAction(controller::printAction);

       //Button testBT = new Button("Change name");
        //testBT.setOnAction(this::changeName);

        Button terminateBT = new Button("End");
        terminateBT.setOnAction(controller::terminateApp); //k tlacitku muzeme priradit akci (! nejlepsi je reagovat na obecnou akci)

        controlPane.getChildren().addAll(eatBT, moveBT, printBT, terminateBT);
        controlPane.getChildren().forEach(node -> ((Region)node).setPrefWidth(80));//pro kazdeho potomka (jsou tam jen tlacitka), pretypuji ho na Region (potomek Node, ktery umoznuje nastavit pref sirku, nastavim pref sirku

        return controlPane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
