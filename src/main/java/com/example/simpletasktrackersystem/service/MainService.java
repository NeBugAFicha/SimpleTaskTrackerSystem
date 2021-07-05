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

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MainService {
    List<User> showUserTasks = new ArrayList<User>();
    List<User> showUserProjects = new ArrayList<User>();
    List<Task> newTasks = new ArrayList<Task>();
    List<User> assignedUsers = new ArrayList<User>();
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
        for(int i = 0;i < showUserTasks.size();i++) {
            if (showUserTasks.get(i).getId() == user.getId()) {
                showUserTasks.remove(i);
                return;
            }
        }
        showUserTasks.add(user);
    }

    public void showUserProjects(User user){
        for(int i = 0;i < showUserProjects.size();i++) {
            if (showUserProjects.get(i).getId() == user.getId()) {
                showUserProjects.remove(i);
                return;
            }
        }
        showUserProjects.add(user);
    }

    public void addNewTask() {
        Task task = new Task();
        taskRepo.save(task);
        newTasks.add(task);
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
        newTasks.stream().forEach(task1 -> {
            if(task.getParentTask()!=null&&task1.getId()==task.getParentTask().getId()) newTasks.set(newTasks.indexOf(task1),taskRepo.findById(task1.getId()).get());
            if(task1.getId()==task.getId()) newTasks.set(newTasks.indexOf(task1),task);
        });
    }
    @Transactional(rollbackFor = NullPointerException.class, propagation = Propagation.REQUIRED)
    public void addSubTask(Task parentTask){
        if(parentTask.getUser()==null) return;
        Task subTask = new Task();
        subTask.setParentTask(parentTask);
        subTask.setUser(parentTask.getUser());
        taskRepo.save(subTask);
        newTasks.add(subTask);
    }

    public void assignUserToProject(Map<String,String> form){
        List<String> userNames = assignedUsers.stream().map(user -> user.getUsername()).collect(Collectors.toList());
        for(String key: form.keySet()) {
            if(!userNames.contains(key)) assignedUsers.add(userRepo.findByUsername(key));
        }
    }

    public void addProject(String projectAim){
        newTasks = taskRepo.findAllByProject(null);
        if(assignedUsers.size()==0||newTasks.size()==0||projectAim.trim().isEmpty()) return;
        for(int i = 0; i < newTasks.size(); i++){
            if(newTasks.get(i).getUser()==null||newTasks.get(i).getTaskAim()==null||newTasks.get(i).getEstimationTime()==null) return;
        }
        Project project = new Project();
        project.setProjectAim(projectAim.trim());
        projectRepo.save(project);
        assignedUsers.stream().forEach(user -> {
            List<Project> projects = projectRepo.findByUsers_Username(user.getUsername());;
            projects.add(project);
            user.setProjects(projects);
            userRepo.save(user);
        });
        newTasks.stream().forEach(task -> {
            task.setProject(project);
            task.setUser(userRepo.findById(task.getUser().getId()).get());
        });
        taskRepo.saveAll(newTasks);
        assignedUsers.clear();
        newTasks.clear();
    }

    public void deleteUser(User user){
        assignedUsers = assignedUsers.stream().filter(user1 -> user1.getId()!=user.getId()).collect(Collectors.toList());
        showUserTasks = showUserTasks.stream().filter(user1 -> user1.getId()!=user.getId()).collect(Collectors.toList());
        showUserProjects = showUserProjects.stream().filter(user1 -> user1.getId()!=user.getId()).collect(Collectors.toList());
        newTasks = newTasks.stream().filter(task -> task.getUser().getId()!=user.getId()).collect(Collectors.toList());
        taskRepo.findAllByUser(user).stream().forEach(task -> {
            if(task.getProject()!=null){
                task.getProject().deleteTask(task);
                projectRepo.save(task.getProject());
            }
        });
        userRepo.delete(user);
    }

    public void deleteProject(Project project){
        newTasks = newTasks.stream().filter(task -> task.getProject().getId()!=project.getId()).collect(Collectors.toList());
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
        assignedUsers = assignedUsers.stream().filter(user1 -> user1.getId()!=user.getId()).collect(Collectors.toList());
        newTasks =  newTasks.stream()
                .filter(task -> task.getUser()==null||task.getUser().getId()!=user.getId())
                .collect(Collectors.toList());
        user.getTasks().clear();
        userRepo.save(user);
    }

    public void deleteTask(Task task){
        if(task.getUser()==null) taskRepo.delete(task);
        //User user = userRepo.findByUsername(task.getUser().getUsername());
        User user = task.getUser();
        user.deleteTask(task);
        for(int i = 0;i < newTasks.size();i++)
            if(newTasks.get(i).getId()==task.getId()) {
                newTasks.remove(i);
                i--;
            }else if(newTasks.get(i).getParentTask()!=null&&newTasks.get(i).getId()==task.getParentTask().getId()) {
                task.getParentTask().deleteTask(task);
            }
        if(!task.getSubTasks().isEmpty()) deleteSubTasksFromNewTask(task.getSubTasks(),user);
        userRepo.save(user);
        for(int i = 0;i < newTasks.size();i++)
            if(newTasks.get(i).getId()==task.getParentTask().getId()) {
                task.getParentTask().deleteTask(task);
                taskRepo.save(task.getParentTask());
                newTasks.set(i, taskRepo.findById(newTasks.get(i).getId()).get());
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
           for (int i = 0; i < newTasks.size(); i++)
                if (newTasks.get(i).getId() == task.getId()) {
                    newTasks.remove(i);
                    break;
                }
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


    public List<User> getShowUserTasks() {
        return showUserTasks;
    }

    public List<User> getShowUserProjects() {
        return showUserProjects;
    }

    public List<Task> getNewTasks() {
        return newTasks;
    }

    public List<User> getAssignedUsers() {
        return assignedUsers;
    }
}
