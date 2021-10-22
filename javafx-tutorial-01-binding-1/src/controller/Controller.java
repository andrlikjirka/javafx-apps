package controller;

import dataModel.Animal;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

public class Controller {
    private final Animal myAnimal;

    public Controller(Animal myAnimal){
        this.myAnimal = myAnimal;
    }

    /*
    private void changeName(ActionEvent event) {
        myAnimal.name.set("Animal");
    }
    */

    public void printAction(ActionEvent event) {
        System.out.println(myAnimal.toString());
    }

    public void moveAction(MouseEvent event) {
        myAnimal.moveTo(1,1);
    }

    public void eatAcion(ActionEvent event) {
        myAnimal.eat(5);
    }

    public void terminateApp(ActionEvent event) {
        Platform.exit();
    }

}
