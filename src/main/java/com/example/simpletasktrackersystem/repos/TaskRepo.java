package com.example.simpletasktrackersystem.repos;

import com.example.simpletasktrackersystem.domain.Project;
import com.example.simpletasktrackersystem.domain.Task;
import com.example.simpletasktrackersystem.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    List<Task> findAllByUser(User user);
    List<Task> findAllByProject(Project project);

}
