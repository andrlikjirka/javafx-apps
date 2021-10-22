package gui;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import model.TextDescriptor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

//specialni trida labelu jako posluchace modelu, ktera se bude moci zaregistrovat u manageru posluchacu
//musi implementovat rozhrani PropertyChangeListener aby se mohl zaregistrovat jako posluchac
//specialni trida bude dedit od klasicke tridy Label
public class ListeningLabel extends Label implements PropertyChangeListener {

    //v nejakem okamziku nekdo zavola ze doslo ke zmene vlastnosti, musim na to zareagovat
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        TextDescriptor tmp = (TextDescriptor) evt.getNewValue();

        this.setText(tmp.previewText.get());
        this.setFont(Font.font(tmp.getFontFamily(), tmp.getFontSize()));
        this.setTextFill(tmp.getFontColor());
    }
}
