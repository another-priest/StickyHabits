package org.example.stickyhabits.Components;

import java.time.LocalTime;
import java.util.UUID;

public class HabitHistory {
    private UUID id; // todo: potrzebne, ze względu na dalszą integralność z bazą główną
    protected String date;
    protected Boolean done;
    protected LocalTime time;
    private String habit;

protected void setDateAndDone(String setDate, Boolean setDone){
    date = setDate;
    time = LocalTime.now();
    done = setDone;
}
private void setParent(String parentId)
{
    habit = parentId;
}
HabitHistory(Habit parent)
{
    id = UUID.randomUUID();
    setParent(parent.getId());
}
HabitHistory(String parent)
{
    id = UUID.randomUUID();
    setParent(parent);
}
HabitHistory(String id,String date,Boolean done,String habit)
{
    this.id= UUID.fromString(id);
    this.date = date;
    this.done = done;
    this.time = LocalTime.now();
    this.habit = habit;
}
public String getId(){
    return id.toString();
}
public String getHabitId()
{
    return habit;
}

    public String getDate() {
    return date;
    }
    public LocalTime getTime() {
        return time;
    }

    public boolean isDone() {
    return done;
    }
}
