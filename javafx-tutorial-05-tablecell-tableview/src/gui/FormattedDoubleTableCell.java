package gui;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.util.converter.DoubleStringConverter;
import utils.Messenger;

import java.util.function.Predicate;

//trida vlastni bunky tabulky (bude zajistovat formatovani a kontrolu dat) - pro Displacement udaj
//bunka muze byt ve 2 rezimech - zobrazovaci (renderer) a editacni (editor)
//tato trida je pro obecnou Double bunku (proto je parametr S obecny)
public class FormattedDoubleTableCell<S, T> extends TableCell<S, Double> {
    private final Label rendererLB = new Label();
    private final TextField editorTF = new TextField();
    private final TextFormatter<Double> formatter; //formatter pro editor (aby byla prijimana jen cisla)

    private Predicate<Number> predicate; //genericka kontrola (test platnosti hodnoty), uzivatel bude zadavat predikat, na zaklade toho se rozhodne zda bude hodnota prijata

    private Messenger messenger; //genericka reakce na chybu

    private String units; //jednotky ktere se budou zobrazovat v bunce

    public FormattedDoubleTableCell(String units, Predicate<Number> p, Messenger m){
        this.units = units;
        this.predicate = p;
        if (m == null){ //zadny predany messenger, pouzije se defaultni
            this.messenger = Messenger.DEFAULT_MESSANGER;
        } else {
            this.messenger = m;
        }

        formatter = new TextFormatter<Double>(new DoubleStringConverter(), 0d);
        editorTF.setTextFormatter(formatter); //nyni textfield (editor) prijima jen double cisla
        formatter.valueProperty().bindBidirectional(itemProperty()); //binding editoru s datovym modelem, itemProperty() vraci obraz toho, jaka data maji byt v bunce zobrazeny

        //pro vlastni editor musime implementovat logiku obsluhy (reakce na tlacitka, dvojkliky,...)
        editorTF.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)){ //pokud byl zmacknuty ESCAPE, ukonci editor
                cancelEdit();
            } else if (event.getCode().equals(KeyCode.DELETE)){ //jsem uvnitr bunky a je zmacknuty delete
                event.consume(); //udalost se nebude propagovat dal
            } else if (event.getCode().equals(KeyCode.ENTER)){ //ENTER -> propagace dat do modelu
                double newValue = formatter.getValue(); //nova hodnota vytazena z formatteru (automaticky Double), pokud bych bral hodnotu z editoru, musel bych osetrit vyjimky

                if (predicate == null){ //uzivatel zadal null - hodnotu kontrolovat nechce
                    commitEdit(newValue); //primo commitnu hodnotu
                } else {
                    if (predicate.test(newValue)){
                        commitEdit(newValue);
                    } else {
                        messenger.sendMessage("Value " + newValue + " is not allowed for this cell.");
                        cancelEdit();
                    }
                }
            }
        });

        setGraphic(rendererLB); //na zacatku je nastacen renderer
    }


    //bunku musime provazat s prislusnou property datoveho modelu
    //v editacnim rezimu pouzijeme binding (prime provazani), ale v zobrazovacim rezimu se bindingu vyhneme (protoze chceme zobrazit krome daneho cisla i jednotky)
    //resime prekrytim prislusnych metod
    @Override
    protected void updateItem(Double item, boolean empty) { //item = item ktery zobrazujeme, empty = indikator jestli policko je prazdne nebo neni
        super.updateItem(item, empty);

        if (empty){ //prazdny radek
            setText("");
            rendererLB.setText("");
            editorTF.setText("");
        } else if (item == null) { //radek neni prazdny, data jsou prazdne
            if (isEditing()){ //zeptam se bunky zda je v editacnim rezimu - pokud ano (budu pracovat s editorem)
                editorTF.setText("");

            } else { //pokud neni v editacnim rezimu (budu pracovat s rendererem)
                rendererLB.setText("not set");

            }
        } else { //radek neni prazdy, data jsou k dispozici
            if (isEditing()){
                editorTF.setText(item.toString());
            } else {
                if ((units == null) || (units.trim().length() == 0)){
                    rendererLB.setText(item.toString());
                } else {
                    rendererLB.setText(item.toString() + " " + units);
                }
            }
        }

    }

    //metoda spusteni editace (prechod z zobrazovaciho do editacniho rezimu), reakci na doubleClick vyvola tabulka sama
    @Override
    public void startEdit() {
        super.startEdit();
        System.out.println("Editation start");
        setGraphic(editorTF); //zmena z rendereru na editor
    }

    //metoda ukonceni editace
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        System.out.println("Editation canceled");
        setGraphic(rendererLB);
    }

    //metoda potvrzeni editace
    @Override
    public void commitEdit(Double newValue) {
        super.commitEdit(newValue);
        System.out.println("Value " + newValue + " commmited.");
        setGraphic(rendererLB);
    }

}
