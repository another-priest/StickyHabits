package org.example.stickyhabits.Components;

import java.util.ArrayList;
import java.util.UUID;

public class Habit  {
 private UUID id;
 private String name;
 ArrayList<HabitHistory> history = new ArrayList<HabitHistory>();

 public Habit(String habitName){
  HabitUtils habitUtils = new HabitUtils();
  createHabit(habitName.trim());
  setHistory(habitUtils.generateHistory(this));
 }
 public Habit(String id,String habitName){
  this.id=UUID.fromString(id);
  this.name=habitName.trim();
 }
 private void createHabit(String habitName){
  id = UUID.randomUUID();
  name = habitName.trim();
 }
 public String getId()
 {
   return id.toString();
 }
 public void setHistory(ArrayList<HabitHistory> history) {
  this.history = history;
 }
 private Boolean validateHistory(){
     return !history.isEmpty();
 }
 public String getName(){
  return name;
 }
 public ArrayList<HabitHistory> getHistory(){
  return history;
 }
 public void setName(String name){
  this.name = name.trim();
 }
 public void setId(String id){
  this.id=UUID.fromString(id);
 }
}
