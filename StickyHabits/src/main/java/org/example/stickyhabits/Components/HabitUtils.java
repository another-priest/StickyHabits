package org.example.stickyhabits.Components;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;

public class HabitUtils {

   protected ArrayList<HabitHistory> generateHistory(Habit parent) {
       ArrayList<HabitHistory> history = new ArrayList<HabitHistory>();
       Date date = new Date();
       for (int i = 0; i < 7 ; i++) {
           HabitHistory item = new HabitHistory(parent);
           String dateString = date.toInstant().plus(i,DAYS).toString().substring(0,10);
           item.setDateAndDone(dateString,false);
           history.add(item);
       }
       return history;
   }
    protected ArrayList<HabitHistory> autoGenerateFuture(String parent, String lastdate) {
        ArrayList<HabitHistory> history = new ArrayList<>();

        // Jeśli nie ma daty – generujemy nową historię od dziś
        if (lastdate == null || lastdate.isEmpty()) {
            LocalDate start = LocalDate.now();
            for (int i = 0; i < 7; i++) {
                LocalDate newDate = start.plusDays(i);
                HabitHistory item = new HabitHistory(parent);
                item.setDateAndDone(newDate.toString(), false);
                history.add(item);
            }
            return history;
        }

        LocalDate last = LocalDate.parse(lastdate);
        LocalDate today = LocalDate.now();

        if (!today.isBefore(last.plusDays(7))) {
            for (int i = 1; i <= 7; i++) {
                LocalDate newDate = last.plusDays(i);
                HabitHistory item = new HabitHistory(parent);
                item.setDateAndDone(newDate.toString(), false);
                history.add(item);
            }
        }
        return history;
    }

}
