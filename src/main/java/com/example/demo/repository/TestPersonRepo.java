package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.TestPerson;

public interface TestPersonRepo extends JpaRepository<TestPerson, Long>{

}
