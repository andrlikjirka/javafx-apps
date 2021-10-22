package model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.time.LocalDate;

public class DataModel { //vsechny lode v tabulce

    public ListProperty<Ship> ships = new SimpleListProperty<>(FXCollections.observableArrayList());

    public void populate() {
        ships.clear();

        ships.add(new Ship("Enterprise", 21000d,
                LocalDate.of(1934, 5, 12), LocalDate.of(1947, 2, 17),
                ShipType.CARRIER));
        ships.add(new Ship("Hornet", 20000d,
                LocalDate.of(1941, 10, 20), LocalDate.of(1942, 10, 27),
                ShipType.CARRIER));
        ships.add(new Ship("Bismarck", 21000d,
                LocalDate.of(1940, 8, 24), LocalDate.of(1941, 5, 27),
                ShipType.BATTLESHIP));
        ships.add(new Ship("Cassin Young", 2050d,
                LocalDate.of(1943, 12, 31), LocalDate.of(1960, 4, 29),
                ShipType.DESTROYER));
        ships.add(new Ship("Joseph P. Kennedy Jr.\n", 3479d,
                LocalDate.of(1945, 12, 15), LocalDate.of(1973, 7, 2),
                ShipType.DESTROYER));
        ships.add(new Ship("Red October", 48000d,
                LocalDate.of(1984, 12, 3), LocalDate.of(1985, 7, 30),
                ShipType.SUBMARINE));
        ships.add(new Ship("Still in service", 20000d,
                LocalDate.of(2015, 12, 3), null,
                ShipType.CRUISER));
        ships.add(new Ship("Strange data", 22000d,
                null, LocalDate.of(1950, 3, 2),
                ShipType.SUBMARINE));
    }


}
