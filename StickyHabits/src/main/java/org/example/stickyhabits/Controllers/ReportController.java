package org.example.stickyhabits.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.example.stickyhabits.Components.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ReportController {
    private final DatabaseFunctions db = new DatabaseFunctions();
    @FXML
    private TableView<HabitReportRow> reportTable;
   // @FXML
    //private TableView <HabitReportRow> reportWeek;

    @FXML private TableColumn<HabitReportRow, String> nameColumn;
    @FXML private TableColumn<HabitReportRow, String> dateColumn;
    @FXML private TableColumn<HabitReportRow, String> doneColumn;

    @FXML private TableView<WeeklyReportRow> weeklyTable;
    @FXML private TableColumn<WeeklyReportRow, String> mondayNameColumn;
    @FXML private TableColumn<WeeklyReportRow, String> tuesdayNameColumn;
    @FXML private TableColumn<WeeklyReportRow, String> wednesdayNameColumn;
    @FXML private TableColumn<WeeklyReportRow, String> thursdayNameColumn;
    @FXML private TableColumn<WeeklyReportRow, String> fridayNameColumn;
    @FXML private TableColumn<WeeklyReportRow, String> saturdayNameColumn;
    @FXML private TableColumn<WeeklyReportRow, String> sundayNameColumn;



    @FXML
    public void initialize() {
        System.out.println("weeklyTable is " + (weeklyTable == null ? "NULL ❌" : "OK ✅"));
    showWeekHistory();
    showAllHistory();
    }
private void showAllHistory()
{
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
private void showWeekHistory(){
    ObservableList<WeeklyReportRow> weeklyData = FXCollections.observableArrayList();

    Map<String, WeeklyReportRow> rowMap = new HashMap<>();
    for (HabitHistory hh : db.getAllHabitHistory()) {
        LocalDate date = LocalDate.parse(hh.getDate());
        DayOfWeek day = date.getDayOfWeek();




        if (date.isAfter(LocalDate.now().minusDays(7-date.getDayOfWeek().getValue()))) {
            String habitName = db.getHabitById(hh.getHabitId()).getName();
            rowMap.putIfAbsent(habitName, new WeeklyReportRow(habitName));
            rowMap.get(habitName).statusProperty(day).set(hh.isDone());
        }
    }
    TableColumn<WeeklyReportRow, String> nameCol = new TableColumn<>("Habit");

    nameCol.setCellValueFactory(data -> data.getValue().habitNameProperty());
    weeklyTable.getColumns().add(0, nameCol); // dodaje jako pierwszą
    weeklyData.addAll(rowMap.values());
    weeklyTable.setItems(weeklyData);
    mondayNameColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().mondayProperty().get() ? "✓" : "✗"));
    tuesdayNameColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().tuesdayProperty().get() ? "✓" : "✗"));
    wednesdayNameColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().wednesdayProperty().get() ? "✓" : "✗"));
    thursdayNameColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().thursdayProperty().get() ? "✓" : "✗"));
    fridayNameColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().fridayProperty().get() ? "✓" : "✗"));
    saturdayNameColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().saturdayProperty().get() ? "✓" : "✗"));
    sundayNameColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().sundayProperty().get() ? "✓" : "✗"));
    applyColorCellFactory(mondayNameColumn);
    applyColorCellFactory(tuesdayNameColumn);
    applyColorCellFactory(wednesdayNameColumn);
    applyColorCellFactory(thursdayNameColumn);
    applyColorCellFactory(fridayNameColumn);
    applyColorCellFactory(saturdayNameColumn);
    applyColorCellFactory(sundayNameColumn);


}
    private void applyColorCellFactory(TableColumn<WeeklyReportRow, String> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(item.equals("✓") ? "-fx-background-color: #c8facc;" : "-fx-background-color: #f8c8c8;");
                }
            }
        });
    }
}
