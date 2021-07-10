package com.example.simpletasktrackersystem.domain;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String projectAim;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REFRESH}, mappedBy = "projects")
    List<User> users;
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "project")
    private List<Task> tasks;
    public void deleteTask(Task task){
        for(int i = 0; i < tasks.size();i++) if(tasks.get(i).getId()==task.getId()) tasks.remove(i);
    }

}
