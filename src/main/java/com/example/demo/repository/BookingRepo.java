package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.example.demo.domain.Booking;

public interface BookingRepo extends JpaRepository<Booking, Long>{

	List<Booking> findByName(String name);

}
