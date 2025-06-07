package org.example.stickyhabits.Components;

import java.util.UUID;

public class HabitHistory {
    private UUID id; // todo: potrzebne, ze względu na dalszą integralność z bazą główną
    protected String date;
    protected Boolean done;
    private String habit;

protected void setDateAndDone(String setDate, Boolean setDone){
    date = setDate;
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

    public boolean isDone() {
    return done;
    }
}
