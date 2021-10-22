package model;

public enum ShipType {
    //razeni v tabulce se dela podle poradi zde v enum (v tomto pripade razeni podle velikosti lodi)
    CARRIER ("Carrier"),
    BATTLESHIP ("Battleship"),
    CRUISER ("Cruiser"),
    DESTROYER ("Destroyer"),
    SUBMARINE ("Submarine");

    ShipType(String value){
        this.enumValue = value;
    }

    private String enumValue;

    public String toString(){
        return enumValue;
    }

}
