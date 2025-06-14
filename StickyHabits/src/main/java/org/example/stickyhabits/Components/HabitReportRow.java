package org.example.stickyhabits.Components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HabitReportRow {
    private final StringProperty name;
    private final StringProperty date;
    private final BooleanProperty done;


    public HabitReportRow(String name, String date, boolean done) {
        this.name = new SimpleStringProperty(name);
        this.date = new SimpleStringProperty(date);
        this.done = new SimpleBooleanProperty(done);

    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty dateProperty() {
        return date;
    }

    public BooleanProperty doneProperty() {
        return done;
    }


    public boolean isDone() {
        return done.get();
    }
}
