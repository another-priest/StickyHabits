package org.example.stickyhabits.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.stickyhabits.Components.*;

import java.io.IOException;

public class EditHabitController {
    @FXML
    private Button backButton;
    @FXML
    private TextField habitNameField;
    @FXML
    private Label conf;
    private Habit habit;
private final DatabaseFunctions db = new DatabaseFunctions();
    private final HabitFormModel formModel = new HabitFormModel();
    private HabitAIService aiService;

    public void setAiService(HabitAIService svc) {
        this.aiService = svc;
    }

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
        DataValidation validator = new DataValidation();
        if(validator.validateName(habitNameField.getText())){
            habit.setName(habitNameField.getText());
            db.updateHabit(habit);
            conf.setText("Habit Updated!!! Good luck!");
        }

    }
    @FXML
    public void deleteHabit() throws IOException
    {


        db.deleteHabit(habit);
        goToDashboard();
    }
    @FXML
    public void goToDashboard() throws IOException {

        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/stickyhabits/Dashboard.fxml"));
        Parent root = loader.load();

        DashboardController dash = loader.getController();
        dash.setAiService(aiService);
        stage.setScene(new Scene(root));
        stage.show();
    }

}
