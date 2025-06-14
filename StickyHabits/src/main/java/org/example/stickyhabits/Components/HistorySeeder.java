package org.example.stickyhabits.Components;
import java.sql.*;
import java.time.*;
import java.util.*;

    public class HistorySeeder {
        private static final String DB_URL = "jdbc:sqlite:HabitBase";

        public static void main(String[] args) {
            try {
                seedHistory();
                DatabaseFunctions db = new DatabaseFunctions();
                db.generateFuture();
                System.out.println("Gotowe! Historia wygenerowana.");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public static void seedHistory() throws SQLException {
            LocalDate start = LocalDate.of(2025, 2, 1);
            LocalDate end   = LocalDate.now().minusDays(1);
            Random rnd = new Random(); // nie stałe ziarno

            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                conn.setAutoCommit(false);


                List<String> habits = new ArrayList<>();
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT id FROM habit")) {
                    while (rs.next()) {
                        habits.add(rs.getString(1));
                    }
                }


                Map<String, Integer> baseHour = new HashMap<>();
                for (String id : habits) {
                    int bh = 8 + Math.floorMod(id.hashCode(), 13); // 8..20
                    baseHour.put(id, bh);
                }


                String checkSql  = "SELECT 1 FROM habit_history WHERE habit_id=? AND date=?";
                String insertSql = """
        INSERT INTO habit_history
          (id, date, done, habit_id, completion_timestamp)
        VALUES (?, ?, ?, ?, ?)
        """;

                try (PreparedStatement checkStmt  = conn.prepareStatement(checkSql);
                     PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

                    int batchCount = 0;
                    LocalDate d = start;
                    while (!d.isAfter(end)) {
                        String dateStr = d.toString();
                        int weekday = d.getDayOfWeek().getValue(); // 1=Mon…7=Sun

                        // modyfikator weekendowy: weekend +1h później
                        int weekendShift = (weekday >= 6 ? 2 : 0);

                        // sezonowy trend: nawyki bliżej początku miesiąca bardziej "aktywne"
                        double monthFactor = 1.0 + 0.5 * Math.cos((d.getDayOfMonth() - 1) * 2 * Math.PI / d.lengthOfMonth());

                        for (String hid : habits) {

                            checkStmt.setString(1, hid);
                            checkStmt.setString(2, dateStr);
                            try (ResultSet rs = checkStmt.executeQuery()) {
                                if (rs.next()) continue;
                            }

                            int bh = baseHour.get(hid) + weekendShift;
                            // losowe przesunięcie z sigma=1.5h
                            int hour = (int) Math.round(bh + rnd.nextGaussian() * 1.5);
                            hour = Math.max(6, Math.min(22, hour));

                            // minuta: środek 30, sigma=20
                            int minute = (int) Math.round(30 + rnd.nextGaussian() * 20);
                            minute = Math.max(0, Math.min(59, minute));


                            int dist = Math.abs(hour - bh);

                            // p(done)= monthFactor * exp(-dist^2/4) ograniczone do [0,1]
                            double p = monthFactor * Math.exp(-dist * dist / 4.0);
                            p = Math.min(1.0, Math.max(0.05, p)); // min 5%, max 100%

                            int done = (rnd.nextDouble() < p) ? 1 : 0;

                            insertStmt.setString(1, UUID.randomUUID().toString());
                            insertStmt.setString(2, dateStr);
                            insertStmt.setInt   (3, done);
                            insertStmt.setString(4, hid);
                            insertStmt.setString(5, LocalTime.of(hour, minute).toString());
                            insertStmt.addBatch();
                            batchCount++;

                            // co 500 wstawek — wykonaj batch i commit
                            if (batchCount >= 500) {
                                insertStmt.executeBatch();
                                conn.commit();
                                batchCount = 0;
                            }
                        }

                        d = d.plusDays(1);
                    }

                    // dokończ batch i commit
                    if (batchCount > 0) {
                        insertStmt.executeBatch();
                        conn.commit();
                    }
                }
            }
        }

    }

