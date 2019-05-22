package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Service;

public interface ServiceRepo extends JpaRepository<Service, Long>{
	
	Optional<Service> findByDay(Long day);

}
