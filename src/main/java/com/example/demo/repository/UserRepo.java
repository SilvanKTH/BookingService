package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long>{

	List<User> findByName(String name);
}
