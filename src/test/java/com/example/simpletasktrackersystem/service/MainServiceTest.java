package com.example.simpletasktrackersystem.service;

import com.example.simpletasktrackersystem.domain.Task;
import com.example.simpletasktrackersystem.domain.User;
import com.example.simpletasktrackersystem.repos.ProjectRepo;
import com.example.simpletasktrackersystem.repos.TaskRepo;
import com.example.simpletasktrackersystem.repos.UserRepo;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class MainServiceTest {
    @Autowired
    MainService mainService;
    @Autowired
    UserRepo userRepo;
    @Autowired
    ProjectRepo projectRepo;
    @Autowired
    TaskRepo taskRepo;
    @Test
    void addUser() {
        int userCount = userRepo.findAll().size();
        mainService.addUser("Stas");
        mainService.addUser("egor");
        int userCount2 = userRepo.findAll().size();
        Assert.assertEquals(userCount2, userCount+1);
    }

    @Test
    void showUserTasks() {
        List<User> users = userRepo.findAll();
        mainService.showUserTasks(users.get(0));
        mainService.showUserTasks(users.get(1));
        Assert.assertEquals(mainService.getShowUserTasks().size(),2);
        mainService.showUserTasks(users.get(0));
        Assert.assertEquals(mainService.getShowUserTasks().size(),1);
    }

    @Test
    void showUserProjects() {
        List<User> users = userRepo.findAll();
        mainService.showUserProjects(users.get(0));
        mainService.showUserProjects(users.get(1));
        Assert.assertEquals(mainService.getShowUserProjects().size(), 2);
        mainService.showUserProjects(users.get(0));
        Assert.assertEquals(mainService.getShowUserProjects().size(), 1);
    }

    @Test
    void addNewTask() {
        int taskCount = taskRepo.findAll().size();
        mainService.addNewTask();
        int taskCount2 = taskRepo.findAll().size();
        Assert.assertEquals(taskCount2, taskCount+1);
    }
    @Test
    void assignUserToProject() {
        List<User> users = userRepo.findAll();
        Map<String, String> form = new HashMap<>(){{
            put(users.get(0).getUsername(),users.get(0).getUsername());
            put(users.get(1).getUsername(),users.get(1).getUsername());
        }};
        mainService.assignUserToProject(form);
        Assert.assertEquals(mainService.getAssignedUsers().size(),2);
        Map<String, String> form2 = new HashMap<>(){{
                put(users.get(0).getUsername(), users.get(0).getUsername());
        }};
        mainService.assignUserToProject(form2);
        Assert.assertEquals(mainService.getAssignedUsers().size(),2);
    }
    void add4newTasks(){
        mainService.addNewTask();
        mainService.addNewTask();
        mainService.addNewTask();
        mainService.addNewTask();
    }
    void assignUsersToProject(){
        List<User> users = userRepo.findAll();
        Map<String, String> form = new HashMap<>(){{
            put(users.get(0).getUsername(),users.get(0).getUsername());
            put(users.get(1).getUsername(),users.get(1).getUsername());
        }};
        mainService.assignUserToProject(form);
    }
    void addTasks(){
        add4newTasks();
        assignUsersToProject();
        List<Task> tasks = taskRepo.findAllByUser(null);
        Map<String, String> form = new HashMap<>();
        form.put("singleUser", "egor");
        Map<String, String> form2 = new HashMap<>();
        form2.put("singleUser", "admin");
        mainService.addTask("task","1",form, tasks.get(0));
        mainService.addTask("task2","2",form, tasks.get(1));
        mainService.addTask("task3","3",form2, tasks.get(2));
        mainService.addTask("task4","4",form2, tasks.get(3));
    }
    @Test
    void addTask() {
        add4newTasks();
        assignUsersToProject();
        //Valid data
        List<Task> tasks = taskRepo.findAllByUser(null);
        Map<String, String> form2 = new HashMap<>();
        form2.put("singleUser", "egor");
        Map<String, String> form3 = new HashMap<>();
        form3.put("singleUser", "admin");
        mainService.addTask("task0","5",form2, tasks.get(0));
        mainService.addTask("","3",form2, tasks.get(1));
        mainService.addTask("task3","-1",form3, tasks.get(2));
        mainService.addTask("task4","7",new HashMap<>(), tasks.get(3));
        Assert.assertEquals(taskRepo.findAllByUser(null).size(),3);
        //Invalid datas
    }

    @Test
    void addSubTask() {
        addTasks();
        List<Task> tasks = taskRepo.findAllByProject(null);
        mainService.addSubTask(tasks.get(0));
        mainService.addSubTask(tasks.get(0));
    }



    @Test
    void addProject() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void deleteProject() {
    }

    @Test
    void unsignUser() {
    }

    @Test
    void deleteTask() {
    }

    @Test
    void deleteSubTasksFromNewTask() {
    }
}