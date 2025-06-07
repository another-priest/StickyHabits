package org.example.stickyhabits.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.stickyhabits.Components.DatabaseFunctions;
import org.example.stickyhabits.Components.Habit;
import org.example.stickyhabits.Components.HabitRowViewModel;

import java.io.IOException;

public class DashboardController {

    @FXML
    private ListView<HabitRowViewModel> habitListView;
    @FXML
    private ToolBar toolbar;
    private DatabaseFunctions db = new DatabaseFunctions();
public void initialize() {


    ObservableList<HabitRowViewModel> items = FXCollections.observableArrayList();
    for (Habit habit : db.getHabitsWithTodayHistory()) {
        items.add(new HabitRowViewModel(habit.getId(), habit.getName(), db.getHabitState(habit.getId())));
    }
    habitListView.setItems(items);
    habitListView.setCellFactory(list -> new ListCell<>() {
        @Override
        protected void updateItem(HabitRowViewModel item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
            } else {
                CheckBox checkBox = new CheckBox(item.getName());
                checkBox.setSelected(item.isDone());

                checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    item.setDone(newVal);
                    db.updateHabitState(item.getHabitId(), newVal);
                });

                setGraphic(checkBox);
            }
        }
    });
}

    public ToolBar getToolbar() {
        return toolbar;
    }
    @FXML
    private Button addButton;
    @FXML
    public void goToAdd() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stickyhabits/AddHabit.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    public void goToEdit() throws IOException {
        HabitRowViewModel selectedItem = habitListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;

        Habit habit = db.getHabitByIdFull(selectedItem.getHabitId()); // ← potrzebna taka metoda

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/stickyhabits/EditHabit.fxml"));
        Parent root = loader.load();

        EditHabitController controller = loader.getController();
        controller.setEditingHabit(habit);

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
            reportStage.initOwner(toolbar.getScene().getWindow()); // Ustawienie okna nadrzędnego
            reportStage.initModality(Modality.WINDOW_MODAL); // lub NONE, jeśli nie chcesz modalnego
            reportStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
