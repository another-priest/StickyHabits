package org.example.stickyhabits.Components;

import smile.classification.RandomForest;
import smile.data.DataFrame;
import smile.data.Tuple;
import smile.data.formula.Formula;
import smile.data.type.DataTypes;
import smile.data.type.StructField;
import smile.data.type.StructType;
import smile.data.vector.IntVector;
import smile.io.Read;
import smile.io.Write;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.util.*;

/** AI-moduł: uczy RandomForest i rekomenduje optymalną godzinę dla nawyku */
public class AIHabitRecommender {


    private final Path modelPath =
            Paths.get("StickyHabits\\habit-model.rf");
    private final Path dictPath  =
            modelPath.resolveSibling("habit-dict.ser");
    private RandomForest matrixModel;
    /* ---------- model & słownik ---------- */
    private RandomForest model;
    private Map<String,Integer> habitDict;

    /* ---------- pełny schemat (łącznie z kolumną label) ---------- */
    private static final StructType PREDICT_SCHEMA = DataTypes.struct(
            new StructField("habit", DataTypes.IntegerType),
            new StructField("dow",   DataTypes.IntegerType),
            new StructField("hour",  DataTypes.IntegerType),
            new StructField("label", DataTypes.IntegerType)   // placeholder przy predykcji
    );


    public RandomForest getModel() {
        return model;
    }
    public Map<String,Integer> getHabitDict() {
        return habitDict;
    }

    public record HabitHistoryRecord(String habitName,
                                     DayOfWeek day,
                                     int hour,
                                     boolean done) {}


    public void trainModel(List<HabitHistoryRecord> history) throws IOException {
        System.out.println(">>> TRAIN MODEL: historia ma " + history.size() + " rekordów");
        if (history == null || history.size() < 2) return;   // potrzebne 0 i 1

        /* 1) słownik habit → kod ------------------------------------------------ */
        habitDict = new HashMap<>();
        int next = 0;
        for (HabitHistoryRecord r : history)
            habitDict.putIfAbsent(r.habitName(), next++);

        /* 2) DataFrame z kolumnami IntVector ------------------------------------ */
        int n = history.size();
        int[] habit = new int[n];
        int[] dow   = new int[n];
        int[] hour  = new int[n];
        int[] label = new int[n];

        for (int i = 0; i < n; i++) {
            HabitHistoryRecord r = history.get(i);
            habit[i] = habitDict.get(r.habitName());
            dow[i]   = r.day().getValue();
            hour[i]  = r.hour();
            label[i] = r.done() ? 1 : 0;
        }

        DataFrame df = DataFrame.of(
                IntVector.of("habit", habit),
                IntVector.of("dow",   dow),
                IntVector.of("hour",  hour),
                IntVector.of("label", label)    // kolumna celu
        );

        /* 3) RandomForest.fit(Formula, DataFrame, Properties) ------------------- */
        Properties p = new Properties();
        p.setProperty("ntrees", "100");         // 100 drzew
        model = RandomForest.fit(Formula.lhs("label"), df, p);

        /* 4) zapis na dysk (obiekt, Path) --------------------------------------- */
        Files.createDirectories(modelPath.getParent());
        Write.object(model, modelPath);
        Write.object((Serializable) habitDict, dictPath);
        model = RandomForest.fit(Formula.lhs("label"), df, p);
        System.out.println(">>> Model przetrenowany, feature importances:");
        System.out.println(Arrays.toString(model.importance()));
    }



    @SuppressWarnings("unchecked")
    public boolean tryLoadModel() {
        try {
            model     = (RandomForest) Read.object(modelPath);
            habitDict = (Map<String,Integer>) Read.object(dictPath);
            return true;
        } catch (Exception ex) {
            return false;   // brak pliku lub niezgodny → trzeba trenować
        }
    }


  /*  public boolean recommend(String habit, DayOfWeek day, int hour) {
        if (model == null || habitDict == null) return false;

        Integer code = habitDict.get(habit);
        if (code == null) return false;

        Tuple t = Tuple.of(new Object[]{ code, day.getValue(), hour, 0 },
                PREDICT_SCHEMA); // 0 = placeholder label

        return model.predict(t) == 1;
    }*/
}
