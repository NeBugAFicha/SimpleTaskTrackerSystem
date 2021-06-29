package com.example.simpletasktrackersystem.domain;



import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
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
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Project> projects;

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Task> tasks;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    public void deleteTask(Task task){
        for(int i = 0; i < tasks.size();i++) if(tasks.get(i).getId()==task.getId()) tasks.remove(i);
    }
}
