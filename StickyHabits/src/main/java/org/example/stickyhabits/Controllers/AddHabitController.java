package org.example.stickyhabits.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.example.stickyhabits.Components.*;

import java.io.IOException;

public class AddHabitController {
    @FXML
    private TextField habitNameField;
    @FXML
    private Button backButton;
    @FXML
    private Label conf;
    private HabitAIService aiService;

    public void setAiService(HabitAIService svc) {
        this.aiService = svc;
    }
private final DatabaseFunctions db = new DatabaseFunctions();

    @FXML
    public void initialize(){
        habitNameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    onAddHabit();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    @FXML
    protected void onAddHabit() throws IOException{
        DataValidation validator = new DataValidation();
        if (validator.validateName(habitNameField.getText())) {
            Habit habit = new Habit(habitNameField.getText());
            db.add(habit);
            conf.setText("New habit added!!! Good luck!");
            habitNameField.clear();
            goToDashboard();
        }

    }
    @FXML
    public void goToDashboard() throws IOException {

        Stage stage = (Stage) backButton.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/stickyhabits/Dashboard.fxml"));
        Parent root = loader.load();

        DashboardController dash = loader.getController();
        dash.setAiService(aiService);           // aiService wstrzyknięty wcześniej
        stage.setScene(new Scene(root));
        stage.show();
    }
}
