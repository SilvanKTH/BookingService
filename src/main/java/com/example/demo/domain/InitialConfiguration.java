package com.example.demo.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.FileNotFoundException;

import com.example.demo.constants.LocalConstants;
import com.example.demo.repository.BookingRepo;
import com.example.demo.repository.ServiceRepo;
import com.example.demo.repository.UserRepo;

@Configuration
public class InitialConfiguration {
	
	@Autowired
	ServiceRepo serviceRepo;
	@Autowired
	UserRepo userRepo;
	@Autowired
	BookingRepo bookingRepo;
	
	public static Long timeReference;
	
	public InitialConfiguration () {}
	
	@Bean
	void initServiceDB() {
		Integer rooms = LocalConstants.NUMBER_ROOMS;
		Integer timeUnits = LocalConstants.INITIAL_PERIOD;
		
		for (int i = 0; i < timeUnits; i++) {
			Long day = Long.valueOf(i);
			Service s = new Service(day, rooms);
			serviceRepo.save(s);
		}
	}
	
	@Bean
	void initUserDBTest() {
		User u1 = new User("a", "1133557799");
		User u2 = new User("b", "2244668800");
		userRepo.save(u1);
		userRepo.save(u2);
	}
	
	@Bean 
	void initBookingDBTest() {
		Booking b1 = new Booking("a", 1, 10, 12, 9);
		Booking b2 = new Booking("b", 2, 2, 3, 1);
		bookingRepo.save(b1);
		bookingRepo.save(b2);
	}
	
	
	@Bean
	void initUserDB() throws FileNotFoundException {
		// READ CLIENTS FROM LOCAL FILE IF PROVIDED
	}
	
	@Bean
	public void setTime() {
		System.out.println("+++ SETTING THE TIME REFERENCE +++");
		InitialConfiguration.timeReference = System.currentTimeMillis();
		System.out.println(timeReference);
	}

}
