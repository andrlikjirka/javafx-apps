package dataModel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Animal {
    public StringProperty name; //misto String vyuzijeme pozorovatelnou tridu StringProperty, ktery je Observable (umozni automaticke pozorovani a prepisovani hodnot v gui)
    public IntegerProperty energy;
    public IntegerProperty xCoord;
    public IntegerProperty yCoord;

    public Animal(String name, int energy, int xCoord, int yCoord){
        this.name = new SimpleStringProperty(name); //obycejny String se pouzije jako inicializacni hodnota pro vytvoreni nove instance StringProperty, SimpleStringProperty je zjednoduseny StringProperty (umi vse potrebne)
        this.energy = new SimpleIntegerProperty(energy);
        this.xCoord = new SimpleIntegerProperty(xCoord);
        this.yCoord = new SimpleIntegerProperty(yCoord);
    }

    public boolean eat(int energy){
        if (energy > 0) {
            this.energy.set(this.energy.get() + energy);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean moveTo(int xCoord, int yCoord){
        if (energy.get() > 0){
            energy.set(energy.get() - 1);
            this.xCoord.set(this.xCoord.get() + xCoord);
            this.yCoord.set(this.yCoord.get() + yCoord);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Animal{" +
                "name='" + name + '\'' +
                ", energy=" + energy +
                ", xCoord=" + xCoord +
                ", yCoord=" + yCoord +
                '}';
    }
}


