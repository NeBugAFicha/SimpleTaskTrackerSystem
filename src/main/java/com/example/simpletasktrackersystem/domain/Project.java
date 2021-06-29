package com.example.simpletasktrackersystem.domain;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String projectAim;
    @ManyToMany(cascade = CascadeType.PERSIST, mappedBy = "projects")
    List<User> users;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "project")
    private List<Task> tasks;
    public Project(){
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProjectAim() {
        return projectAim;
    }

    public void setProjectAim(String projectAim) {
        this.projectAim = projectAim;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
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
