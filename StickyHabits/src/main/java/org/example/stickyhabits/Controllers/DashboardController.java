package org.example.stickyhabits.Controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.stickyhabits.Components.*;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.function.IntSupplier;

public class DashboardController {

    @FXML
    private ListView<HabitRowViewModel> habitListView;
    @FXML
    private ToolBar toolbar;
    private final DatabaseFunctions db = new DatabaseFunctions();
    private HabitAIService aiService;
    public void initialize() {




    }
    public void loadData() {
        ObservableList<HabitRowViewModel> items = FXCollections.observableArrayList();
        for (Habit h : db.getHabitsWithTodayHistory()) {
            items.add(new HabitRowViewModel(h.getId(), h.getName(), db.getHabitState(h.getId())));
        }
        habitListView.setItems(items);

        habitListView.setCellFactory(
                lv -> new HabitCell(db, aiService.getRecommender()));

    }


    public void setAiService(HabitAIService svc) {
        this.aiService = svc;
        loadData();
    }
    public ToolBar getToolbar() {
        return toolbar;
    }
    @FXML
    private Button addButton;
    @FXML
    public void goToAdd() throws IOException {
        FXMLLoader l = new FXMLLoader(
                getClass().getResource("/org/example/stickyhabits/AddHabit.fxml"));
        Parent root = l.load();

        AddHabitController add = l.getController();
        add.setAiService(aiService);
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    public void goToEdit() throws IOException {
        HabitRowViewModel selectedItem = habitListView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) return;

        Habit habit = db.getHabitByIdFull(selectedItem.getHabitId());


        FXMLLoader l = new FXMLLoader(
                getClass().getResource("/org/example/stickyhabits/EditHabit.fxml"));
        Parent root = l.load();


        EditHabitController controller = l.getController();
        controller.setEditingHabit(habit);
        controller.setAiService(aiService);

        Stage stage = (Stage) habitListView.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


    @FXML
    public void goExit(){
        System.exit(0);
    }
    @FXML
    private void openReportsWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/stickyhabits/Report.fxml"));
            Parent root = fxmlLoader.load();

            Stage reportStage = new Stage();
            reportStage.setTitle("Raporty");
            reportStage.setScene(new Scene(root));
            reportStage.initOwner(toolbar.getScene().getWindow());
            reportStage.initModality(Modality.WINDOW_MODAL);
            reportStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
