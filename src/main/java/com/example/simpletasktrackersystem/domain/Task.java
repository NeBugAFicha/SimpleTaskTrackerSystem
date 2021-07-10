package com.example.simpletasktrackersystem.domain;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
@Getter
@Setter
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String taskAim;
    private boolean isDone;
    private Calendar estimationTime;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private Project project;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private User user;

    @ManyToOne(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    private Task parentTask;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "parentTask")
    private List<Task> subTasks;
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
    public void deleteTask(Task task){
        for(int i = 0; i < subTasks.size();i++) if(subTasks.get(i).getId()==task.getId()) subTasks.remove(i);
    }
}
