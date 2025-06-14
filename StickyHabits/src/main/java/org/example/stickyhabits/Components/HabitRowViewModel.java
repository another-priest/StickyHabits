package org.example.stickyhabits.Components;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

public class HabitRowViewModel {
    private final StringProperty name;
    private final BooleanProperty done;
    private final String id;
    private int reminderHour ;
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

    public void setReminderHour(int h) {
        this.reminderHour = h;


        LocalDateTime now = LocalDateTime.now();
        LocalDateTime target = now.withHour(h).withMinute(0).withSecond(0).withNano(0);
        if (target.isBefore(now)) {

            target = target.plusDays(1);
        }
        long delay = Duration.between(now, target).toMillis();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Czas na: " + getName());
                alert.setHeaderText("Przypomnienie StickyHabits");
                alert.showAndWait();
            });
        }, delay, TimeUnit.MILLISECONDS);
    }



    public int reminderHourProperty() {
        return reminderHour;
    }
}
