package model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class DataModel {
    //datovy model je v podstate kolekce TextDescriptoru (chceme vytvorit zobrazeni seznamu, takze nasim modelem nebude jeden TextDescriptor, ale cely seznam

    public ListProperty<TextDescriptor> descriptors = new SimpleListProperty<>(FXCollections.observableArrayList(TextDescriptor.extractor())); //seznam je vytvoren kolem pozorovatelneho arraylistu, diky predanemu parametru extractor (ktery vraci Callback, bude reagovat na vnitrni zmeny objektu)

    public void initializeModel(int elementNumber){
        descriptors.clear();

        for (int i = 0; i < elementNumber; i++){
            descriptors.add(TextDescriptor.generateRandomDescriptor());
        }
    }

}
