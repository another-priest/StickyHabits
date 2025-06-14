package org.example.stickyhabits.Components;

import org.example.stickyhabits.Components.AIHabitRecommender.HabitHistoryRecord;

import java.sql.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class HabitDataPreparator {

    /** Zwraca listę rekordów gotowych do trenowania **/

    public static List<HabitHistoryRecord> loadHistory(String dbUrl) {
        List<HabitHistoryRecord> out = new ArrayList<>();

        final String sql = """
SELECT
  h.id                                   AS habit,
  CASE strftime('%w', hh.date)
    WHEN '0' THEN 7
    ELSE CAST(strftime('%w', hh.date) AS INT)
  END                                      AS weekday,
  CASE WHEN hh.done = 1
       THEN CAST(substr(hh.completion_timestamp,1,2) AS INT)
       ELSE -1
  END                                      AS hour,
  hh.done                                  AS done
FROM habit_history hh
JOIN habit h ON h.id = hh.habit_id;
    """;

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String habit      = rs.getString("habit");
                int weekdayInt    = rs.getInt("weekday");  // 1–7
                int hour          = rs.getInt("hour");     // 0–23 or -1
                boolean done      = rs.getInt("done") == 1;

                out.add(new HabitHistoryRecord(
                        habit,
                        DayOfWeek.of(weekdayInt),
                        hour,
                        done
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

}
