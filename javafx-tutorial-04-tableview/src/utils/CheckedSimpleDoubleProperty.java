package utils;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Parent;

public class CheckedSimpleDoubleProperty extends SimpleDoubleProperty { //potomek SimpleDoubleProperty musi navic zajistovat kontrolu

    @Override
    public void set(double newValue) {
        setInternal(newValue);
    }

    @Override
    public void setValue(Number v) {
        if (v == null){
            throw new NullPointerException("You have to provide a real value, not null");
        }
        setInternal(v.doubleValue());
    }

    private void setInternal(double v){
        if (v < 0){
            throw new IllegalArgumentException("It is not allowed to set negative value");
        } else {
            super.set(v);
        }
    }
}
