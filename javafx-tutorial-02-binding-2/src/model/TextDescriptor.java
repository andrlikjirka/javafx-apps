package model;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TextDescriptor {
    public static int MAX_FONT_SIZE = 120;

    public static int DEFAULT_FONT_SIZE = 20;
    public static Color DEFAULT_FONT_COLOR = Color.RED;
    public static String DEFAULT_FONT_FAMILY = "Amsterdam Graffiti";

    private final ReadOnlyStringWrapper fontFamilyProperty = new ReadOnlyStringWrapper(); //specialni druh property, jednak v sobe obsahuje SimpleStringProperty, kterou mohu menit (pro vnitrni pouziti); a zaroven umoznuje vytvorit navenek viditelnou readonlyproperty zrcadlujici obsah
    private final ReadOnlyIntegerWrapper fontSizeProperty = new ReadOnlyIntegerWrapper();
    private final ReadOnlyObjectWrapper<Color> fontColorProperty = new ReadOnlyObjectWrapper<>();

    private final PropertyChangeSupport listenerManager = new PropertyChangeSupport(this); //instance tridy, ktera slouzi pro registraci vsech posluchacu teto tridy (modelu)

    //StringBinding muze byt public, protoze nejde zmenit zvenku (meni se pouze pokud se zmeni nejaka z provazanych property)
    public StringBinding previewText = new StringBinding() {
        {
            bind(fontFamilyProperty, fontSizeProperty);
        }
        @Override
        protected String computeValue() {
            return fontFamilyProperty.get() + " (" + fontSizeProperty.get() + ")";
        }
    };
    
    //metoda slouzi pro registraci posluchace tridy modelu do manageru posluchacu, je nutne zaridit, aby posluchac ktery se chce zaregistrovat implementoval rozhrani PropertyChangeListener
    public void addTextDescriptorListener(PropertyChangeListener listener){
        listenerManager.addPropertyChangeListener(listener);
    }

    //metoda slouzi pro odebrani posluchace tridy modelu z manageru posluchacu
    public void removeTextDescriptorListener(PropertyChangeListener listener){
        listenerManager.removePropertyChangeListener(listener);
    }

    public TextDescriptor(){
        this(DEFAULT_FONT_FAMILY, DEFAULT_FONT_SIZE, DEFAULT_FONT_COLOR);
    }

    public TextDescriptor(String fontFamily, int fontSize, Color fontColor){
        setFontFamily(fontFamily);
        setFontSize(fontSize);
        setFontColor(fontColor);

        //v moment zmeny property trida zahlasi zmenu vsem posluchacum (a preda starou a novou hodnotu)
        fontFamilyProperty.addListener((observable, oldValue, newValue) -> listenerManager.firePropertyChange("Font", null, this));
        fontColorProperty.addListener((observable, oldValue, newValue) -> listenerManager.firePropertyChange("Color", null, this));
        fontSizeProperty.addListener((observable, oldValue, newValue) -> listenerManager.firePropertyChange("Size", null, this));
    }

    public ReadOnlyObjectProperty<Color> fontColorProperty(){ //navenek vracim pouze readonly property, ktera pochazi z wrapperu (property nemohu zvnenku zmenit)
        return fontColorProperty.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty fontFamilyProperty(){
        return fontFamilyProperty.getReadOnlyProperty();
    }

    public ReadOnlyIntegerProperty fontSizeProperty(){
        return fontSizeProperty.getReadOnlyProperty();
    }

    public void setFontColor(Color fontColor) {
        if (fontColor == null){
            throw new NullPointerException("Font color has to be provided, cannnot be null");
        } 
        this.fontColorProperty.set(fontColor);
    }

    public void setFontSize(int fontSize) {
        if ((fontSize <= 0) || fontSize > MAX_FONT_SIZE) {
            throw new IllegalArgumentException("Font size " + fontSize + " is not allowed, must be between " + 0 + " and " + MAX_FONT_SIZE);
        }
        this.fontSizeProperty.set(fontSize);
    }

    public void setFontFamily(String fontFamily) {
        if (fontFamily == null){
            throw new NullPointerException("Font family has to be provided, cannot be null!");
        } else {
            if (fontFamily.length() <= 0){
                throw new IllegalArgumentException("Font family has to be provided, cannot be empty string!");
            }
        }
        this.fontFamilyProperty.set(fontFamily);
    }

    public int getFontSize(){
        return fontSizeProperty.get();
    }

    public String getFontFamily() {
        return fontFamilyProperty.get();
    }

    public Color getFontColor() {
        return fontColorProperty.get();
    }

    public String toString() {
        return "Text format descriptor: Font: " + getFontFamily() + ", size: " + getFontSize() + ", Color " + getFontColor();
    }

}
