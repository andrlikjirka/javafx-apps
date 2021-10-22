package utils;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

//trida ma pouze dedit od klasicke TextField bunky a pridat ji consuming reakci na DELETE
public class DeleteConsumingTextFieldTableCell<S, T> extends TextFieldTableCell<S,T> {

    public DeleteConsumingTextFieldTableCell(final StringConverter<T> converter){
        super(converter);
        setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.DELETE)){
                event.consume();
            }
        });
    }

    public DeleteConsumingTextFieldTableCell(){
        this((StringConverter<T>) new DefaultStringConverter());
    }

    //nutne prekryt i metody forTableColumn, ktere vytvareji nove instance (tovarna)
    public static <S> Callback<TableColumn<S,String>,TableCell<S,String>> forTableColumn(){
        return forTableColumn(new DefaultStringConverter());
    }


    public static <S,T> Callback<TableColumn<S, T>, TableCell<S,T>> forTableColumn​(StringConverter<T> converter){
        return column -> new DeleteConsumingTextFieldTableCell(converter);
    }



}
