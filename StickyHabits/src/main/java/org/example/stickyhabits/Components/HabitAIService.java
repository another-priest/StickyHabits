package org.example.stickyhabits.Components;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class HabitAIService {
    private final DatabaseFunctions db;
    private final AIHabitRecommender rec;
private HabitDataPreparator habitDataPreparator= new HabitDataPreparator();
    public HabitAIService(DatabaseFunctions db) {
        this.db = db;
        this.rec = new AIHabitRecommender();
    }
    public void retrain() throws IOException {
        List<AIHabitRecommender.HabitHistoryRecord> all = HabitDataPreparator.loadHistory("jdbc:sqlite:HabitBase");

        rec.trainModel(all);
    }

    public AIHabitRecommender getRecommender() {
        return rec;
    }
}
