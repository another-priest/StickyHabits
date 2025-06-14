package org.example.stickyhabits.Components;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import smile.data.DataFrame;
import smile.data.vector.IntVector;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;



public class HabitCell extends ListCell<HabitRowViewModel> {

    private final DatabaseFunctions db = new DatabaseFunctions();
    private final AIHabitRecommender rec;

    private final CheckBox cb = new CheckBox();
    private final Label ai   = new Label();
    private final Button btn = new Button("✓ Set reminder");
    private final HBox root  = new HBox(10, cb, ai, btn);

    private final ChangeListener<Boolean> checker = (obs, oldV, newV) -> {
        HabitRowViewModel r = getItem();
        if (r == null) return;
        r.setDone(newV);
        db.updateHabitState(r.getHabitId(), newV);
        cb.setDisable(true);
    };

    public HabitCell(DatabaseFunctions db, AIHabitRecommender rec) {
        this.rec = rec;

        btn.setOnAction(e -> {
            HabitRowViewModel r = getItem();
            if (r != null) {
                int h = bestHour(r.getId());
                r.setReminderHour(h);
            }
        });

        btn.managedProperty().bind(btn.visibleProperty());   // usuwa „dziurę”
    }

    @Override
    protected void updateItem(HabitRowViewModel row, boolean empty) {
        super.updateItem(row, empty);

        if (empty || row == null) { setGraphic(null); return; }

        cb.selectedProperty().removeListener(checker);

        cb.setText(row.getName());
        cb.setSelected(row.isDone());
        cb.setDisable(row.isDone());

        cb.selectedProperty().addListener(checker);

        int best = bestHour(row.getId());
        ai.setText(best + ":00");
        ai.setStyle("-fx-font-weight:bold;-fx-text-fill:#2ecc71;");

        btn.setVisible(!row.isDone());

        setGraphic(root);
    }

    private int bestHour(String habitName) {
        if (rec == null) {
            return LocalTime.now().getHour()+1;
        }
        Integer code = rec.getHabitDict().get(habitName);
        if (code == null) {
            return LocalTime.now().getHour()+1;
        }

        DayOfWeek dow = LocalDate.now().getDayOfWeek();
        final int FROM = 5, TO = 23;
        int N = TO - FROM + 1;

        // przygotuj kolumny dla DF
        int[] habitCol = new int[N];
        int[] dowCol   = new int[N];
        int[] hourCol  = new int[N];
        int[] labelCol = new int[N];

        for (int i = 0; i < N; i++) {
            habitCol[i] = code;
            dowCol[i]   = dow.getValue();
            hourCol[i]  = FROM + i;
            labelCol[i] = 0;
        }

        DataFrame df = DataFrame.of(
                IntVector.of("habit", habitCol),
                IntVector.of("dow",   dowCol),
                IntVector.of("hour",  hourCol),
                IntVector.of("label", labelCol)
        );

        // lista na rozkłady posterior dla każdej próbki
        List<double[]> posterior = new ArrayList<>(N);
        // uzyskaj predykcje + posteriory
        rec.getModel().predict(df, posterior);

        // wybierz najlepsze h
        double bestP = -1;
        int bestH = FROM;
        for (int i = 0; i < N; i++) {
            double p1 = posterior.get(i)[1];
            if (p1 > bestP) {
                bestP = p1;
                bestH = FROM + i;
            }
        }
        return bestH;
    }



}
