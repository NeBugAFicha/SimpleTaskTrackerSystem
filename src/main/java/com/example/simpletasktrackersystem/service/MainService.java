package com.example.simpletasktrackersystem.service;


import com.example.simpletasktrackersystem.domain.Project;
import com.example.simpletasktrackersystem.domain.Task;
import com.example.simpletasktrackersystem.domain.User;
import com.example.simpletasktrackersystem.repos.ProjectRepo;
import com.example.simpletasktrackersystem.repos.TaskRepo;
import com.example.simpletasktrackersystem.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MainService {
    private List<Long> showUserTasks = new ArrayList<>();
    private List<Long> showUserProjects = new ArrayList<>();
    private List<Long> newTasks = new ArrayList<>();
    private Set<Long> assignedUsers = new HashSet<>();
    @Autowired
    UserRepo userRepo;
    @Autowired
    ProjectRepo projectRepo;
    @Autowired
    TaskRepo taskRepo;
    public void addUser(String username){
        if(userRepo.findByUsername(username)==null) userRepo.save(new User(username));
    }
    public void showUserTasks(User user){
        if(showUserTasks.contains(user.getId())) showUserTasks.remove(user.getId());
        else showUserTasks.add(user.getId());
    }

    public void showUserProjects(User user){
        if(showUserProjects.contains(user.getId())) showUserProjects.remove(user.getId());
        else showUserProjects.add(user.getId());
    }

    public void addNewTask() {
        Task task = new Task();
        taskRepo.save(task);
        newTasks.add(task.getId());
    }

    public void addTask(String taskAimTemp, String estimationTime, Map<String, String> form, Task task){
        if(taskAimTemp.trim().isEmpty()||assignedUsers.size()==0||estimationTime.trim().isEmpty()||! estimationTime.trim().matches("[0-9]+")) return;
        if(task.getParentTask()==null) {
            if (!form.keySet().contains("singleUser")) return;
            task.setUser(userRepo.findByUsername(form.get("singleUser")));
        }else setEstimationTimeToMainTask(task,Integer.parseInt(estimationTime));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, Integer.parseInt(estimationTime));
        task.setEstimationTime(calendar);
        task.setTaskAim(taskAimTemp.trim());
        taskRepo.save(task);
    }
    @Transactional(rollbackFor = NullPointerException.class, propagation = Propagation.REQUIRED)
    public void addSubTask(Task parentTask){
        if(parentTask.getUser()==null) return;
        Task subTask = new Task();
        subTask.setParentTask(parentTask);
        subTask.setUser(parentTask.getUser());
        taskRepo.save(subTask);
        newTasks.add(subTask.getId());
    }

    public void assignUserToProject(Map<String,String> form){
        for(String key: form.keySet()) {
            if(!assignedUsers.contains(Long.parseLong(form.get(key)))) assignedUsers.add(Long.parseLong(form.get(key)));
        }
    }

    public void addProject(String projectAim){
        List<Task> newTasksToSave = taskRepo.findAllById(newTasks);
        if(assignedUsers.size()==0||newTasks.size()==0||projectAim.trim().isEmpty()) return;
        for(int i = 0; i < newTasks.size(); i++){
            if(newTasksToSave.get(i).getUser()==null||newTasksToSave.get(i).getTaskAim()==null||newTasksToSave.get(i).getEstimationTime()==null) return;
        }
        Project project = new Project();
        project.setProjectAim(projectAim.trim());
        projectRepo.save(project);
        userRepo.saveAll(
                userRepo.findAllById(assignedUsers).stream()
                .map(user -> {
                    user.getProjects().add(project);
                    return user;
                }).collect(Collectors.toList())
        );
        newTasksToSave.stream().forEach(task -> {
            task.setProject(project);
            task.setUser(userRepo.findById(task.getUser().getId()).get());
        });
        taskRepo.saveAll(newTasksToSave);
        assignedUsers.clear();
        newTasks.clear();
    }

    public void deleteUser(User user){
        assignedUsers.remove(user.getId());
        showUserTasks.remove(user.getId());
        showUserProjects.remove(user.getId());
        newTasks.removeAll(user.getTasks().stream().map(task -> task.getId()).collect(Collectors.toList()));
        taskRepo.findAllByUser(user).stream().forEach(task -> {
            if(task.getProject()!=null){
                task.getProject().deleteTask(task);
                projectRepo.save(task.getProject());
            }
        });
        userRepo.delete(user);
    }

    public void deleteProject(Project project){
        newTasks.removeAll(project.getTasks().stream().map(task -> task.getId()).collect(Collectors.toList()));
        userRepo.saveAll(
                userRepo.findByProjects_Id(project.getId()).stream().map(user -> {
                    List<Project> projects = projectRepo.findByUsers_Username(user.getUsername());
                    for(int i = 0; i < projects.size();i++) if(projects.get(i).getId()==project.getId()) projects.remove(i);
                    for(int i = 0; i < project.getTasks().size(); i++) user.deleteTask(project.getTasks().get(i));
                    user.setProjects(projects);
                    return user;
                }).collect(Collectors.toList())
        );
        projectRepo.delete(project);
    }

    public void unsignUser(User user){
        assignedUsers.remove(user.getId());
        newTasks.removeAll(user.getTasks().stream().map(task -> task.getId()).collect(Collectors.toList()));
        user.getTasks().clear();
        userRepo.save(user);
    }

    public void deleteTask(Task task){
        if(task.getUser()==null) taskRepo.delete(task);
        User user = task.getUser();
        user.deleteTask(task);
        newTasks.remove(task.getId());
        if(!task.getSubTasks().isEmpty()) deleteSubTasksFromNewTask(task.getSubTasks(),user);
        userRepo.save(user);
        for(int i = 0;i < newTasks.size();i++)
            if(newTasks.get(i)==task.getParentTask().getId()) {
                task.getParentTask().deleteTask(task);
                taskRepo.save(task.getParentTask());
                break;
            }
    }
    public void setEstimationTimeToMainTask(Task task, int estimationTime){
        if(task.getParentTask()!=null) setEstimationTimeToMainTask(task.getParentTask(),estimationTime);
        else {
            Calendar calendar = task.getEstimationTime();
            calendar.add(Calendar.HOUR,estimationTime);
            task.setEstimationTime(calendar);
        };
    }
    public void deleteSubTasksFromNewTask(List<Task> subTasks,User user) {
        subTasks.stream().forEach(task -> {
             newTasks.remove(task.getId());
             user.deleteTask(task);
            if (!task.getSubTasks().isEmpty()) deleteSubTasksFromNewTask(task.getSubTasks(),user);
        });
    }

    public void generateReport(Project project, User user){
        taskRepo.saveAll(
                user.getTasks().stream().map(task -> {
                    if(task.getProject().getId()==project.getId()) task.setDone(true);
                    return task;
                }).collect(Collectors.toList())
        );
    }


    public List<Long> getShowUserTasks() { return showUserTasks; }

    public List<Long> getShowUserProjects() {
        return showUserProjects;
    }

    public List<Long> getNewTasks() {
        return newTasks;
    }

    public Set<Long> getAssignedUsers() {
        return assignedUsers;
    }
}
