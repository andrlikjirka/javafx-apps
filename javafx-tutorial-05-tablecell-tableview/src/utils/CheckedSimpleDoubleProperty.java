package utils;

import javafx.beans.property.SimpleDoubleProperty;

import java.util.function.Predicate;

public class CheckedSimpleDoubleProperty extends SimpleDoubleProperty { //potomek SimpleDoubleProperty musi navic zajistovat kontrolu
    //vlastni predikat pro genericky test hodnot
    public static final MinMaxPredicate POSITIVE_DOUBLE_PREDICATE = new MinMaxPredicate(0, Double.MAX_VALUE);

    public static class MinMaxPredicate implements Predicate<Number> {
        private final Number min, max;

        public MinMaxPredicate(Number min, Number max) {
            if (min.doubleValue() > max.doubleValue()){
                throw new IllegalArgumentException("Min has to be lower than max");
            }
            this.min = min;
            this.max = max;
        }

        @Override
        public boolean test(Number number) {
            return ((number.doubleValue() >= min.doubleValue()) && (number.doubleValue() <= max.doubleValue()));
        }
    }

    private final Predicate<Number> checker;

    public CheckedSimpleDoubleProperty(Predicate<Number> checker) {
        this.checker = checker;
    }

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

    private void setInternal(double newValue){
        if (checker == null){
            super.set(newValue);
        } else {
            if (checker.test(newValue)){ //test splnen
                super.set(newValue);
            } else {
                throw new IllegalArgumentException("The value " + newValue + " is not allowed for this property!");
            }
        }
    }
}
