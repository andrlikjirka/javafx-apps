package model;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import utils.CheckedSimpleDoubleProperty;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Ship { //odpovida jedne radce tabulky (jedne lodi)
    private StringProperty name = new SimpleStringProperty(); //bez kontroly, staci jednoduchy binding
    private DoubleProperty displacement = new CheckedSimpleDoubleProperty(); //property, ktera zajisti kladne cislo
    //diky tomu ze commmissioned a retired jsou property, tak jejich zmena se automaticky objevi v tabulce (diky bindingu pres cellValueFactory)
    private ReadOnlyObjectWrapper<LocalDate> commissioned = new ReadOnlyObjectWrapper<>(); //kontrola pres seter
    private ReadOnlyObjectWrapper<LocalDate> retired = new ReadOnlyObjectWrapper<>();
    private ObjectProperty<ShipType> type = new SimpleObjectProperty(); //bez kontroly

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy");//zpusob jakym formatujeme datum v celem programu (1 nebo 2 cifry pro den, 1 nebo 2 cifry pro mesic, 4 cifry pro rok)

    //Period vs Duration:
    //Period = doba v poctu let, mesicu a dni
    //Duration = rozdil casovych znamek
    private ObjectBinding<Period> serviceTime = new ObjectBinding<Period>() { //serviceTimeBinding pozoruje dve property (jakakoliv jejich zmena zpusobi prepocet)
        {
            bind(commissioned.getReadOnlyProperty(), retired.getReadOnlyProperty());
        }
        @Override
        protected Period computeValue() {
            if ((commissioned.getValue() != null) && (retired.getValue() != null)){
                return commissioned.getValue().until(retired.getValue()); //vypocte se rozdil commissioned a retired a vrati ji jako Period
            } else {
                return null;
            }
        }
    };

    public Ship(String name, double displacement, LocalDate commissioned, LocalDate retired, ShipType type){
        setName(name);
        setDisplacement(displacement);
        setCommissioned(commissioned);
        setRetired(retired);
        setType(type);
    }

    public void setName(String newName){
        name.set(newName);
    }

    public String getName(){
        return name.get();
    }

    public StringProperty nameProperty(){
        return name;
    }

    public void setDisplacement(Double newDisplacement){
        displacement.set(newDisplacement);
    }

    public double getDisplacement(){
        return displacement.getValue();
    }

    public DoubleProperty displacementProperty(){
        return displacement; //pri zavolani a ziskani property chci umoznit bindingem primo menit hodnotu (kontrola spravnosti tedy nesmi byt v setru - obesla by se), ale musi ji zarizovat sama trida Displacement)
    }

    public LocalDate getCommissioned(){
        return commissioned.getValue(); //datum je immutable, neporusim zapouzdreni
    }

    public LocalDate getRetired(){
        return retired.getValue();
    }

    public ReadOnlyObjectProperty<LocalDate> commissionedProperty(){ //takto ziskanou property lze pozorovat a naslouchat ji (nelze ji menit)
        return commissioned.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<LocalDate> retiredProperty(){
        return retired.getReadOnlyProperty();
    }

    public void setCommissioned(LocalDate newCommissionedDate){
        if ((newCommissionedDate != null) && (getRetired() != null)){ //pokud ani commissionedDate a retiredDate nejsou null, musim otestovat poradi
            if (newCommissionedDate.isBefore(getRetired())){
                this.commissioned.set(null); //nejprve property vynuluji, pokud bych ji nevynuloval a newCommissionedDate byl stejny jako old, tak nedojde k zahlaseni zmeny a tableCell se neprekresli
                this.commissioned.set(newCommissionedDate);
            } else { //datum ve spatnem poradi
                throw new IllegalArgumentException("Commissioned Date " + FORMATTER.format(newCommissionedDate) + " has to be before " + FORMATTER.format(getRetired()));
            }
        } else {
            commissioned.set(newCommissionedDate);
        }
    }

    public void setRetired(LocalDate newRetiredDate){
        if ((newRetiredDate != null) && (getCommissioned() != null)){
            if (newRetiredDate.isAfter(getCommissioned())){
                this.retired.set(null);
                this.retired.set(newRetiredDate);
            } else {
                throw new IllegalArgumentException("Commissioned Date " + FORMATTER.format(getCommissioned()) + "has to be before " + FORMATTER.format(newRetiredDate));
            }
        } else {
            retired.set(newRetiredDate);
        }
    }

    public ShipType getType(){
        return type.getValue();
    }

    public void setType(ShipType newType){
        type.set(newType);
    }

    public ObjectProperty<ShipType> typeProperty(){
        return type; //jelikoz se jedna o enum, nemuze se nic neopravneneho stat a neni co kontrolovat, mohu ji zpristupnit
    }

    public Period getServiceTime(){
        return serviceTime.getValue(); //pri zavolani getru ObjectBinding se podiva na prislusne property, spocte a vrati hodnotu doby (immutable)
    }

    public ObjectBinding<Period> serviceTimeProperty(){
        return serviceTime; //property je jen Binding, nelze menit, ale bude schopen upozornit na zmeny property
    }

    @Override
    public String toString() {
        String commissioned = getCommissioned() != null ? FORMATTER.format(getCommissioned()) : "not set";
        String retired = getRetired() != null ? FORMATTER.format(getRetired()) : "not set";
        String serviceTime = getServiceTime() != null ? getServiceTime().getYears()  + " years, " + getServiceTime().getMonths() + " months, " + getServiceTime().getDays() + " days" : "still in service";

        return getName() + ", " + getDisplacement() + ", Commissioned: " + commissioned + ", Retired: " + retired + ", service time: " + serviceTime + ", type: " + getType();
    }

}
