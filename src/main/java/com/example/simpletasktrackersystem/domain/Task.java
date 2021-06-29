package com.example.simpletasktrackersystem.domain;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String taskAim;
    private boolean isDone;
    private Calendar estimationTime;
    public Task(){
    }
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    private Project project;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    private User user;

    @ManyToOne(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    private Task parentTask;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "parentTask")
    private List<Task> subTasks;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTaskAim() {
        return taskAim;
    }

    public void setTaskAim(String taskAim) {
        this.taskAim = taskAim;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    public List<Task> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Task> subTasks) {
        this.subTasks = subTasks;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public Calendar getEstimationTime() {
        return estimationTime;
    }
    public String performEstimationTime(){
        return new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(estimationTime.getTime());
    }
    public String getRemainingTime(Calendar calendar){
        long allMillis = estimationTime.getTimeInMillis()-calendar.getTimeInMillis();
        long days = allMillis/1000/60/60/24,
                hours = (allMillis-(days*1000*60*60*24))/1000/60/60,
                minutes = (allMillis-(days*1000*60*60*24+hours*1000*60*60))/1000/60,
                seconds = (allMillis-(days*1000*60*60*24+hours*1000*60*60+minutes*1000*60))/1000;
        return days+":"+hours+":"+minutes+":"+seconds;

    }
    public void setEstimationTime(Calendar estimationTime) {
        this.estimationTime = estimationTime;
    }
    public void deleteTask(Task task){
        for(int i = 0; i < subTasks.size();i++) if(subTasks.get(i).getId()==task.getId()) subTasks.remove(i);
    }
}
