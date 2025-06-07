package org.example.stickyhabits.Components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HabitRowViewModel {
    private final StringProperty name;
    private final BooleanProperty done;
    private final String id;

    public HabitRowViewModel(String id, String name, boolean done) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.done = new SimpleBooleanProperty(done);
    }

    public String getId() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public BooleanProperty doneProperty() {
        return done;
    }

    public String getName() {
        return name.get();
    }

    public boolean isDone() {
        return done.get();
    }

    public void setDone(boolean value) {
        done.set(value);
    }

    public String getHabitId() {
        return id;
    }
}
