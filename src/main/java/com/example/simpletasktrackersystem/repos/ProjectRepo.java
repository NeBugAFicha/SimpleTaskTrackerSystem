package com.example.simpletasktrackersystem.repos;

import com.example.simpletasktrackersystem.domain.Project;
import com.example.simpletasktrackersystem.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {
    List<Project> findByUsers_Username(String username);

}
