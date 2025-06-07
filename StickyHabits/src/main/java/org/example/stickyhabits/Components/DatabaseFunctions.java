package org.example.stickyhabits.Components;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;



public class DatabaseFunctions {
   private static final String DB_URL = "jdbc:sqlite:C:\\Users\\Admin\\Desktop\\studia\\4semestr\\StickyHabits\\HabitBase";
   private final LocalDate today = LocalDate.now();
   public Habit getHabitByIdFull(String habitId) {
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
   public ObservableList<String> getHabits() {
      ObservableList<String> habits = FXCollections.observableArrayList();
      String sql = "SELECT name FROM habit";
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql);
           ResultSet rs = stmt.executeQuery()) {
         while (rs.next()) habits.add(rs.getString("name"));
      } catch (SQLException e) {
         logError(e);
      }
      return habits;
   }

   public List<String[]> getHabitsWithIds() {
      List<String[]> result = new ArrayList<>();
      List<String> ids = getHabitIds();
      List<String> names = getHabits();
      for (int i = 0; i < ids.size(); i++) {
         result.add(new String[]{ids.get(i), names.get(i)});
      }
      return result;
   }

   public void updateHabitState(String habitId, boolean newState) {
      String sql = "UPDATE habit_history SET done = ? WHERE habit_id = ? AND date = ?";
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql)) {
         stmt.setInt(1, newState ? 1 : 0);
         stmt.setString(2, habitId);
         stmt.setString(3, today.toString());
         stmt.executeUpdate();
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

   public ObservableList<ObservableList<String>> getReports() {
      ObservableList<ObservableList<String>> list = FXCollections.observableArrayList();
      String sql = "SELECT name, date, done FROM habit INNER JOIN habit_history hh ON habit.id = hh.habit_id WHERE date <= DATE()";
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql);
           ResultSet rs = stmt.executeQuery()) {
         while (rs.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(rs.getString("name"));
            row.add(rs.getString("date"));
            row.add(String.valueOf(rs.getInt("done")));
            list.add(row);
         }
      } catch (SQLException e) {
         logError(e);
      }
      return list;
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
      String sql = "INSERT INTO habit_history VALUES (?, ?, ?, ?)";
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql)) {
         for (HabitHistory h : history) {
            stmt.setString(1, h.getId());
            stmt.setString(2, h.date);
            stmt.setBoolean(3, h.done);
            stmt.setString(4, habitId);
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
      String sql = "SELECT COUNT(*) FROM habits WHERE name = ?";
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql)) {
         stmt.setString(1, name);
         ResultSet rs = stmt.executeQuery();
         if (rs.next()) {
            return rs.getInt(1);
         }
      } catch (SQLException e) {
         e.printStackTrace(); // Możesz logować lub rzucać wyjątek wyżej
      }
      return 0; // Jeśli coś poszło nie tak
   }
   private void logError(SQLException e) {
      System.err.println("Database error: " + e.getMessage());
   }

}


/*import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;



public class DatabaseFunctions {
   private static final String DB_URL = "jdbc:sqlite:identifier.sqlite"; // lub inny JDBC URL
private final LocalDate today = LocalDate.now();
   public void add(Habit habitName) {
      String sql = "INSERT INTO habit values(?,?)";

      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement habitStatement = conn.prepareStatement(sql)) {

         habitStatement.setString(1, habitName.getId());
         habitStatement.setString(2, habitName.getName());
         habitStatement.executeUpdate();

         //addHistory(habitName.getId(),habitName.getHistory());
         generateFuture();

      } catch (SQLException e) {
         System.err.println("Database error: " + e.getMessage());
      }
   }
   private void addHistory(String habit,ArrayList<HabitHistory> history) {

      String sql = "insert into habit_history values(?,?,?,?)";
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement historyStatement = conn.prepareStatement(sql)) {

         for (HabitHistory habitHistory : history) {
            historyStatement.setString(1, habitHistory.getId());
            historyStatement.setString(2, habitHistory.date);
            historyStatement.setBoolean(3,habitHistory.done);
            historyStatement.setString(4, habit);
            historyStatement.executeUpdate();
         }
      } catch (SQLException e) {
         System.err.println("Database error: " + e.getMessage());
      }
   }

   private ArrayList<String> getHabitsIds() {
      ArrayList<String> ids = new ArrayList<>();
      String sql = "select id from habit";
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement habitStatement = conn.prepareStatement(sql);
           ResultSet resultSet = habitStatement.executeQuery())
      {
         while (resultSet.next()) {
            ids.add(resultSet.getString("id"));
         }
         return ids;

      } catch (SQLException e) {
         System.err.println("Database error: " + e.getMessage());
      }
      return ids;
   }

   private HashMap<String, String> getLastDates() {
      String sql = "SELECT habit_id, MAX(last_generated) as last_date FROM habitTime GROUP BY habit_id";
      HashMap<String, String> lastDates = new HashMap<>();
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql);
           ResultSet rs = stmt.executeQuery()) {

         while (rs.next()) {
            lastDates.put(rs.getString("habit_id"), rs.getString("last_date"));
         }
      } catch (SQLException e) {
         System.err.println("Database error: " + e.getMessage());
      }
      return lastDates;
   }
   public ObservableList<String> getHabits() {
      ObservableList<String> name = FXCollections.observableArrayList();
      String sql = "select name from habit";
      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement habitStatement = conn.prepareStatement(sql);
           ResultSet resultSet = habitStatement.executeQuery())
      {
         while (resultSet.next()) {
            name.add(resultSet.getString("name"));
         }
         return name;

      } catch (SQLException e) {
         System.err.println("Database error: " + e.getMessage());
      }
      return name;
   }

   public void generateFuture() {
      HashMap<String,String> lastDates = getLastDates();
      ArrayList<String> habitsIds = getHabitsIds();
      HabitUtils habitUtils = new HabitUtils();
      Date date = new Date();
      for (String habitId : habitsIds) {
         ArrayList<HabitHistory> history;

         String lastDate = lastDates.get(habitId);
         history = habitUtils.autoGenerateFuture(habitId, lastDate); // może być null

         if (!history.isEmpty()) {
            addHistory(habitId, history);
            String sql = "INSERT INTO habitTime values(?,?,?)";

            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement habitTimeStatement = conn.prepareStatement(sql)) {

               habitTimeStatement.setString(1, UUID.randomUUID().toString() );
               habitTimeStatement.setString(2, date.toInstant().toString().substring(0,10));
               habitTimeStatement.setString(3, habitId);
               habitTimeStatement.executeUpdate();
            } catch (SQLException e) {
               System.err.println("Database error: " + e.getMessage());
            }
         }
      }
   }
  public List<String[]> getHabitsWithIds()
  {
     List<String[]> list =new ArrayList<>();
     ObservableList<String> names = getHabits();
     int i = 0;
     for (String habitId : getHabitsIds())
     {
        list.add(new String[]{habitId, names.get(i)});
        i++;
     }
     return list;
  }
  public void updateHabit(String habitId, String newName) {
     String sql = "Update habit set  name=? where id=?";

     try (Connection conn = DriverManager.getConnection(DB_URL);
          PreparedStatement habitStatement = conn.prepareStatement(sql)) {

        habitStatement.setString(1, newName);
        habitStatement.setString(2, habitId);
        habitStatement.executeUpdate();

        generateFuture();

     } catch (SQLException e) {
        System.err.println("Database error: " + e.getMessage());
     }

  }
   public String getHabitById(String habitId) {
      String name = "";
      String sql = "SELECT name FROM habit WHERE id = ?";

      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement habitStatement = conn.prepareStatement(sql)) {

         habitStatement.setString(1, habitId); // ← najpierw ustawiamy parametr
         try (ResultSet resultSet = habitStatement.executeQuery()) { // potem dopiero wykonujemy zapytanie
            if (resultSet.next()) {
               name = resultSet.getString("name");
            }
         }

      } catch (SQLException e) {
         System.err.println("Database error: " + e.getMessage());
      }

      return name;
   }

   public void updateHabitState(String habitId, Boolean newState)
   {
      String sql = "Update habit_history set  done=? where habit_id=? and date=?";

      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement habitStatement = conn.prepareStatement(sql)) {

         habitStatement.setInt(1, newState ? 1 : 0);
         habitStatement.setString(2, habitId);
         habitStatement.setString(3,today.toString());
         habitStatement.executeUpdate();


      } catch (SQLException e) {
         System.err.println("Database error: " + e.getMessage());
      }
   }
   public boolean getHabitState(String habitId)
   {
      int done = 0;
      String today = LocalDate.now().toString();
      String sql = "SELECT done FROM habit_history WHERE habit_id = ? and date = ?";

      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement habitStatement = conn.prepareStatement(sql)) {

         habitStatement.setString(1, habitId);
         habitStatement.setString(2, today);

         System.out.println("Sprawdzam: id=" + habitId + " date=" + today);

         try (ResultSet resultSet = habitStatement.executeQuery()) {
            if (resultSet.next()) {
               done = resultSet.getInt("done");
            } else {
               System.out.println("⚠ Brak rekordu w bazie dla tego id i daty");
            }
         }

      } catch (SQLException e) {
         System.err.println("Database error: " + e.getMessage());
      }
      return done == 1;
   }
   public ObservableList<ObservableList<String>> getReports() {
      ObservableList<ObservableList<String>> list = FXCollections.observableArrayList();
      String sql = "SELECT name, date, done FROM habit INNER JOIN habit_history hh ON habit.id = hh.habit_id WHERE date <= DATE()";

      try (Connection conn = DriverManager.getConnection(DB_URL);
           PreparedStatement stmt = conn.prepareStatement(sql);
           ResultSet rs = stmt.executeQuery()) {

         while (rs.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(rs.getString("name"));
            row.add(rs.getString("date"));
            row.add(String.valueOf(rs.getInt("done")));
            list.add(row);
         }

      } catch (SQLException e) {
         System.err.println("Database error: " + e.getMessage());
      }
      return list;
   }


}
*/