package org.example.stickyhabits.Components;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HabitFormModel {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty id = new SimpleStringProperty();
    public StringProperty nameProperty() {
        return name;
    }
    public StringProperty idProperty() {
        return id;
    }
    public void setId(String value) {
        id.set(value);
    }
    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }
}
