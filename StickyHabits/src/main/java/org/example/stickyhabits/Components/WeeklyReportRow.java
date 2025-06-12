package org.example.stickyhabits.Components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.Map;

public class WeeklyReportRow {
    private final StringProperty habitName = new SimpleStringProperty();
    private final BooleanProperty monday = new SimpleBooleanProperty();
    private final BooleanProperty tuesday = new SimpleBooleanProperty();
    private final BooleanProperty wednesday = new SimpleBooleanProperty();
    private final BooleanProperty thursday = new SimpleBooleanProperty();
    private final BooleanProperty friday = new SimpleBooleanProperty();
    private final BooleanProperty saturday = new SimpleBooleanProperty();
    private final BooleanProperty sunday = new SimpleBooleanProperty();

    public WeeklyReportRow(String name) {
        this.habitName.set(name);
    }

    public StringProperty habitNameProperty() { return habitName; }

    public BooleanProperty mondayProperty() { return monday; }
    public BooleanProperty tuesdayProperty() { return tuesday; }
    public BooleanProperty wednesdayProperty() { return wednesday; }
    public BooleanProperty thursdayProperty() { return thursday; }
    public BooleanProperty fridayProperty() { return friday; }
    public BooleanProperty saturdayProperty() { return saturday; }
    public BooleanProperty sundayProperty() { return sunday; }

    public BooleanProperty statusProperty(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> monday;
            case TUESDAY -> tuesday;
            case WEDNESDAY -> wednesday;
            case THURSDAY -> thursday;
            case FRIDAY -> friday;
            case SATURDAY -> saturday;
            case SUNDAY -> sunday;
        };
    }
}
