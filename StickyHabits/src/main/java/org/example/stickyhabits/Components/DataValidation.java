package org.example.stickyhabits.Components;

import javafx.scene.control.Alert;

public class DataValidation {
    //private static final String DB_URL = "jdbc:sqlite:C:\\Users\\Admin\\Desktop\\studia\\4semestr\\StickyHabits\\HabitBase";
    private final DatabaseFunctions db = new DatabaseFunctions();
    public boolean validateName(String name){
        if(name.isEmpty()|| name.trim().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Empty name");
            alert.setContentText("Name should not be empty");
            alert.showAndWait();
            return false;
        }
        if(db.getHabitCountByName(name)>0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Double name");
            alert.setContentText("Habit name should be unique");
            alert.showAndWait();
            return false;
        }
        if(name.length()>100){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Name is too long");
            alert.setContentText("Name should not be longer than 100 characters");
            alert.showAndWait();
            return false;
        }
        return true;


    }

}
