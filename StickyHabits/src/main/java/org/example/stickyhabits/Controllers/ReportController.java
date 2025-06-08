package org.example.stickyhabits.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.example.stickyhabits.Components.DatabaseFunctions;
import org.example.stickyhabits.Components.Habit;
import org.example.stickyhabits.Components.HabitHistory;
import org.example.stickyhabits.Components.HabitReportRow;

public class ReportController {
    private final DatabaseFunctions db = new DatabaseFunctions();
    @FXML
    private TableView<HabitReportRow> reportTable;

    @FXML private TableColumn<HabitReportRow, String> nameColumn;
    @FXML private TableColumn<HabitReportRow, String> dateColumn;
    @FXML private TableColumn<HabitReportRow, String> doneColumn;

    @FXML
    public void initialize() {
        ObservableList<HabitReportRow> rows = FXCollections.observableArrayList();
        for (HabitHistory row : db.getAllHabitHistory()) {
            Habit habit = db.getHabitById(row.getHabitId());
            rows.add(new HabitReportRow(habit.getName(), row.getDate(), row.isDone()));
        }
        reportTable.setItems(rows);

        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        doneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().doneProperty().get() ? "✓" : "✗"));
        reportTable.setRowFactory(tableView -> new TableRow<>() {
            @Override
            protected void updateItem(HabitReportRow item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle(""); // reset stylu
                } else {
                    if (item.doneProperty().get()) {
                        setStyle("-fx-background-color: #d4fcd4;"); // zielony dla wykonanych
                    } else {
                        setStyle("-fx-background-color: #fcd4d4;"); // czerwony dla niewykonanych
                    }
                }
            }
        });
    }
}
