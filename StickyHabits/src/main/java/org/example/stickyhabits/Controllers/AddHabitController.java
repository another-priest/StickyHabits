package org.example.stickyhabits.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.stickyhabits.Components.DatabaseFunctions;
import org.example.stickyhabits.Components.Habit;
import org.example.stickyhabits.Components.HabitFormModel;

import java.io.IOException;

public class AddHabitController {
    @FXML
    private TextField habitNameField;
    @FXML
    private Button backButton;
    @FXML
    private Label conf;
private final DatabaseFunctions db = new DatabaseFunctions();

    public void initialize() {
    }


    @FXML
    protected void onAddHabit() {
        Habit habit = new Habit(habitNameField.getText());
        db.add(habit);
        conf.setText("New habit added!!! Good luck!");
        habitNameField.clear();
    }
    @FXML
    public void goToDashboard() throws IOException {
        System.out.println("backButton: " + backButton);
        Stage stage = (Stage) backButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/stickyhabits/Dashboard.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
}
