package utils;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MyLocalDateStringConverter extends StringConverter<LocalDate> {

    private final DateTimeFormatter formatter;
    public static final String NOT_SET= "not set";
    private LocalDate oldValue; //atribut, ktery uchovava posledni platnou hodnotu bunky


    public MyLocalDateStringConverter(DateTimeFormatter formatter){
        if (formatter == null){
            this.formatter = DateTimeFormatter.ISO_DATE_TIME; //vychozi hodnota
        } else {
            this.formatter = formatter;
        }
    }

    @Override
    public String toString(LocalDate object) {
        if (object != null) {
            oldValue = object; //predany datum do bunky si ulozim jako oldValue
            return formatter.format(object);
        } else {
            return NOT_SET;
        }
    }

    @Override
    public LocalDate fromString(String string) {
        if ((string == null) || (string.trim().length() == 0)){
            oldValue = null;
            return null;
        } else if (string.equals(NOT_SET)){
            oldValue = null;
            return null;
        } else {
            try{
                oldValue = LocalDate.from(formatter.parse(string));
                return oldValue; //tovarna LocalDate nabizi prevod z parsovaneho TemporalObject do LocalDate
            } catch(DateTimeParseException e) { //pokud se nepovede novy predany String parsovat (vyhodi vyjimku), vratim oldValue ktera byla nastavena pri poslednim toString
                return oldValue;
            }
        }
    }
}
