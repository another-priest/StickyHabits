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

public class EditHabitController {
    @FXML
    private Button backButton;
    @FXML
    private TextField habitNameField;
    @FXML
    private Label conf;
    private Habit habit;
    //@FXML private TextField nameField;
private final DatabaseFunctions db = new DatabaseFunctions();
    private final HabitFormModel formModel = new HabitFormModel();

    public void initialize() {
        habitNameField.textProperty().bindBidirectional(formModel.nameProperty());
    }

    public void setEditingHabit(Habit habit) {
        this.habit = habit;
        formModel.setName(habit.getName());
        formModel.setId(habit.getId());
    }
    @FXML
    public void updateHabit()
    {
        habit.setName(habitNameField.getText());
        db.updateHabit(habit);
        conf.setText("Habit Updated!!! Good luck!");
    }
    @FXML
    public void goToDashboard() throws IOException {
        System.out.println("backButton: " + backButton);
        Stage stage = (Stage) backButton.getScene().getWindow();///org/example/stickyhabits/
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/stickyhabits/Dashboard.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

}
