package com.example.simpletasktrackersystem.controller;

import com.example.simpletasktrackersystem.domain.Project;
import com.example.simpletasktrackersystem.domain.Task;
import com.example.simpletasktrackersystem.domain.User;
import com.example.simpletasktrackersystem.repos.ProjectRepo;
import com.example.simpletasktrackersystem.repos.TaskRepo;
import com.example.simpletasktrackersystem.repos.UserRepo;
import com.example.simpletasktrackersystem.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class MainController {
    private boolean showUsers = false;
    private boolean showProjects = false;
    @Autowired
    UserRepo userRepo;
    @Autowired
    ProjectRepo projectRepo;
    @Autowired
    TaskRepo taskRepo;
    @Autowired
    MainService mainService;
    @GetMapping
    public String main(Model model){
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("projects",projectRepo.findAll());
        model.addAttribute("showUserTasks", mainService.getShowUserTasks());
        model.addAttribute("showUserProjects", mainService.getShowUserProjects());
        model.addAttribute("tasks", mainService.getNewTasks());
        model.addAttribute("assignedUsers", mainService.getAssignedUsers());
        model.addAttribute("showUsers", showUsers);
        model.addAttribute("showProjects", showProjects);
        model.addAttribute("currentTime",Calendar.getInstance());
        return "main";
    }
    @PostMapping("/addUser")
    public String addUser(@RequestParam String username){
        mainService.addUser(username);
        return "redirect:/";
    }
    @GetMapping("/showAllUsers")
    public String showAllUsers(){
        if(!showUsers) showUsers = true;
        else showUsers = false;
        return "redirect:/";
    }
    @GetMapping("/showAllProjects")
    public String showAllProjects(){
        if(!showProjects) showProjects = true;
        else showProjects = false;
        return "redirect:/";
    }
    @GetMapping("/showUserTasks/{user}")
    public String showUserTasks(@PathVariable User user){
        mainService.showUserTasks(user);
        return "redirect:/";
    }
    @GetMapping("/showUserProjects/{user}")
    public String showUserProjects(@PathVariable User user){
        mainService.showUserProjects(user);
        return "redirect:/";
    }
    @GetMapping("/addNewTask")
    public String addNewTask(){
        mainService.addNewTask();
        return "redirect:/";
    }
    @PostMapping("/addTask")
    public String addTask(@RequestParam String taskAimTemp, @RequestParam String estimationTime, @RequestParam Map<String, String> form, @RequestParam Task task){
        mainService.addTask(taskAimTemp, estimationTime, form, task);
        return "redirect:/";
    }
    @GetMapping("/addSubTask/{parentTask}")
    public String addSubTask(@PathVariable Task parentTask){
        mainService.addSubTask(parentTask);
        return "redirect:/";
    }
    @PostMapping("/assignUsersToProject")
    public String assignUserToProject(@RequestParam Map<String,String> form){
        mainService.assignUserToProject(form);
        return "redirect:/";
    }
    @PostMapping("/addProject")
    public String addProject(@RequestParam String projectAim){
        mainService.addProject(projectAim);
        return "redirect:/";
    }

    @GetMapping("/deleteUser/{user}")
    public String deleteUser(@PathVariable User user){
        mainService.deleteUser(user);
        return "redirect:/";
    }
    @GetMapping("/deleteProject/{project}")
    public String deleteProject(@PathVariable Project project){
        mainService.deleteProject(project);
        return "redirect:/";
    }
    @GetMapping("/unsignUser/{user}")
    public String unsignUser(@PathVariable User user){
        mainService.unsignUser(user);
        return "redirect:/";
    }

    @GetMapping("/generateReport/{project}/{user}")
    public String generateReport(@PathVariable Project project, @PathVariable User user){
        mainService.generateReport(project, user);
        return "redirect:/";
    }

    @GetMapping("/deleteTask/{task}")
    public String deleteTask(@PathVariable Task task){
        mainService.deleteTask(task);
        return "redirect:/";
    }
}
