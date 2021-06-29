package com.example.simpletasktrackersystem.repos;

import com.example.simpletasktrackersystem.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User>  findByProjects_Id(Long id);
}
