package org.example.stickyhabits.Components;



import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.Date;




public class DatabaseFunctions {
   private static final String DB_URL = "jdbc:sqlite:HabitBase";
   private final LocalDate today = LocalDate.now();

   public void add(Habit habit) {
      String sql = "INSERT INTO habit VALUES (?, ?)";
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql)) {
         stmt.setString(1, habit.getId());
         stmt.setString(2, habit.getName());
         stmt.executeUpdate();
         generateFuture();
      } catch (SQLException e) {
         logError(e);
      }
   }

   public void updateHabit(Habit habit) {
      String sql = "UPDATE habit SET name = ? WHERE id = ?";
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql)) {
         stmt.setString(1, habit.getName());
         stmt.setString(2, habit.getId());
         stmt.executeUpdate();
         generateFuture();
      } catch (SQLException e) {
         logError(e);
      }
   }

   public Habit getHabitById(String habitId) {
      String sql = "SELECT id, name FROM habit WHERE id = ?";

      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql)) {

         stmt.setString(1, habitId);
         try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
               return new Habit(rs.getString("id"), rs.getString("name"));
            }
         }

      } catch (SQLException e) {
         System.err.println("Database error: " + e.getMessage());
      }

      return null;
   }

   public List<Habit> getHabitsWithTodayHistory() {
      List<Habit> list = new ArrayList<>();
      String today = LocalDate.now().toString();
      String sql = """
        SELECT DISTINCT h.id, h.name
        FROM habit h
        JOIN habit_history hh ON h.id = hh.habit_id
        WHERE hh.date = ?
    """;

      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql)) {

         stmt.setString(1, today);
         try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
               list.add(new Habit(rs.getString("id"), rs.getString("name")));
            }
         }

      } catch (SQLException e) {
         System.err.println("Database error: " + e.getMessage());
      }

      return list;
   }
   public void deleteHabit(Habit habit) {
      String sql = "DELETE FROM habit WHERE id = ?";

      try (Connection conn = DriverManager.getConnection(DB_URL)) {
         try (Statement pragmaStmt = conn.createStatement()) {
            pragmaStmt.execute("PRAGMA foreign_keys = ON");
         }

         try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, habit.getId());
            stmt.executeUpdate();
         }

      } catch (SQLException e) {
         logError(e);
      }
   }

   public List<HabitHistory> getAllHabitHistory() {
      List<HabitHistory> list = new ArrayList<>();
      String sql = "SELECT id, date, done, habit_id FROM habit_history where date<=DATE()";

      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql);
           ResultSet rs = stmt.executeQuery()) {

         while (rs.next()) {
            HabitHistory history = new HabitHistory(
                    rs.getString("id"),
                    rs.getString("date"),
                    rs.getInt("done") == 1,
                    rs.getString("habit_id")
            );
            list.add(history);
         }

      } catch (SQLException e) {
         System.err.println("Database error: " + e.getMessage());
      }
      return list;
   }

   public void updateHabitState(String habitId, boolean newState) {
     // if (!newState) return;

      String sql = """
        UPDATE habit_history
        SET    done = ?, completion_timestamp = ?
        WHERE  habit_id = ? AND date = ?
        """;

      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql)) {

         stmt.setInt   (1, newState ? 1 : 0);
         stmt.setString(2, LocalTime.now().toString());
         stmt.setString(3, habitId);
         stmt.setString(4, LocalDate.now().toString()); // **ten sam format** co przy INSERT

         int rows = stmt.executeUpdate();
         if (rows == 0) {
            System.err.println("⚠️  UPDATE nie trafił w żaden wiersz " +
                    "(habitId=" + habitId + ", date=today)");
         }

      } catch (SQLException e) {
         logError(e);
      }
   }



   public boolean getHabitState(String habitId) {
      String sql = "SELECT done FROM habit_history WHERE habit_id = ? AND date = ?";
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql)) {
         stmt.setString(1, habitId);
         stmt.setString(2, today.toString());
         try (ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt("done") == 1;
         }
      } catch (SQLException e) {
         logError(e);
         return false;
      }
   }


   public void generateFuture() {
      Map<String, String> lastDates = getLastDates();
      List<String> habitIds = getHabitIds();
      HabitUtils utils = new HabitUtils();
      String todayStr = new Date().toInstant().toString().substring(0, 10);

      for (String habitId : habitIds) {
         List<HabitHistory> history = utils.autoGenerateFuture(habitId, lastDates.get(habitId));
         if (!history.isEmpty()) {
            addHistory(habitId, history);
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO habitTime VALUES (?, ?, ?)")) {
               stmt.setString(1, UUID.randomUUID().toString());
               stmt.setString(2, todayStr);
               stmt.setString(3, habitId);
               stmt.executeUpdate();
            } catch (SQLException e) {
               logError(e);
            }
         }
      }
   }

   private void addHistory(String habitId, List<HabitHistory> history) {
      String sql = "INSERT INTO habit_history VALUES (?, ?, ?, ?,?)";
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql)) {
         for (HabitHistory h : history) {
            stmt.setString(1, h.getId());
            stmt.setString(2, h.date);
            stmt.setBoolean(3, h.done);
            stmt.setString(4, habitId);
            stmt.setString(5, null);
            stmt.executeUpdate();
         }
      } catch (SQLException e) {
         logError(e);
      }
   }

   private List<String> getHabitIds() {
      List<String> ids = new ArrayList<>();
      String sql = "SELECT id FROM habit";
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql);
           ResultSet rs = stmt.executeQuery()) {
         while (rs.next()) ids.add(rs.getString("id"));
      } catch (SQLException e) {
         logError(e);
      }
      return ids;
   }

   private Map<String, String> getLastDates() {
      Map<String, String> lastDates = new HashMap<>();
      String sql = "SELECT habit_id, MAX(last_generated) as last_date FROM habitTime GROUP BY habit_id";
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql);
           ResultSet rs = stmt.executeQuery()) {
         while (rs.next()) {
            lastDates.put(rs.getString("habit_id"), rs.getString("last_date"));
         }
      } catch (SQLException e) {
         logError(e);
      }
      return lastDates;
   }
   public int getHabitCountByName(String name) {
      String sql = "SELECT COUNT(*) FROM habit WHERE name = ?";
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql)) {
         stmt.setString(1, name);
         ResultSet rs = stmt.executeQuery();
         if (rs.next()) {
            return rs.getInt(1);
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return 0; // Jeśli coś poszło nie tak
   }


   private void logError(SQLException e) {
      System.err.println("Database error: " + e.getMessage());
   }
}
