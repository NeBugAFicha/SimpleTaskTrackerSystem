package com.example.simpletasktrackersystem.domain;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String username;
    public User(){}
    public User(String username){
        this.username=username;
    }
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Project> projects;

    @OneToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE}, mappedBy = "user", orphanRemoval = true)
    private List<Task> tasks;

    public void deleteTask(Task task){
        for(int i = 0; i < tasks.size();i++) if(tasks.get(i).getId()==task.getId()) tasks.remove(i);
    }
    public void deleteProject(List<Project> projects, Project project){
        for(int i = 0; i < projects.size();i++) if(projects.get(i).getId()==project.getId()) projects.remove(i);
    }
}
