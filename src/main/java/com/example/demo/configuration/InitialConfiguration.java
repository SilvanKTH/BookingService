package com.example.demo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.example.demo.constants.LocalConstants;
import com.example.demo.controllers.WebController;
import com.example.demo.domain.Booking;
import com.example.demo.domain.Service;
import com.example.demo.domain.User;
import com.example.demo.repository.BookingRepo;
import com.example.demo.repository.ServiceRepo;
import com.example.demo.repository.UserRepo;

@Configuration
@EnableAspectJAutoProxy  
@EnableAsync
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
			Service s = new Service(day, rooms, rooms);
			serviceRepo.save(s);
		}
	}
	
	@Bean
	void initUserDBTest() {
		/*
		User u1 = new User("a", "1133557799");
		User u2 = new User("b", "2244668800");
		userRepo.save(u1);
		userRepo.save(u2);
		*/
	}
	
	@Bean 
	void initBookingDBTest() {
		/*
		Booking b1 = new Booking("a", 1, 10, 12, 9);
		Booking b2 = new Booking("b", 2, 2, 3, 1);
		bookingRepo.save(b1);
		bookingRepo.save(b2);
		*/
	}
	
	
	@Bean
	void initUserDB() throws FileNotFoundException {
		// READ USER DATA FROM LOCAL FILE IF PROVIDED
	}
	
	@Bean
	public void setTime() {
		System.out.println("+++ SETTING THE TIME REFERENCE +++");
		InitialConfiguration.timeReference = System.currentTimeMillis();
		System.out.println(timeReference);
	}
	
	@Bean(name = "checkTime")
	public TaskExecutor checkTimeExecuter() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.initialize();
		return executor;
	}
	
	@Bean
	public CommandLineRunner runner(TaskExecutor executor) {
		return new CommandLineRunner() {
			public void run (String... args) throws Exception {
				executor.execute(checkDate());
			}

			private Runnable checkDate() throws InterruptedException {
				Integer currentDate = 0;
				WebController controller = new WebController();
				Long timeReference = InitialConfiguration.timeReference;
				Long timeNow = System.currentTimeMillis();
				Thread.sleep(LocalConstants.DAY_IN_MILLIS - (timeNow - timeReference));
				while(true) {
                    Long start = System.currentTimeMillis();
					if (controller.getCurrentDate() > currentDate) {
						currentDate = updateDateAOP(controller);
					}
                    Thread.sleep(2000); // give enough time to write the file
                    updatePayments(controller);
                    try {
        				Scanner scanner1 = new Scanner(new File(LocalConstants.malUsersFile));
                    	updateDDoSUserTrust(scanner1, controller);
								
					} catch (FileNotFoundException e) {
						System.out.println("---- File not found to obtain malicious users! ----");
					} 
                    try {
						Scanner scanner2 = new Scanner(new File(LocalConstants.benignUsersFile));
						restoreBenignUserTrust(scanner2, controller);
					} catch (FileNotFoundException e) {
						System.out.println("---- File not found to obtain benign users! ----");
					}

					Long stop = System.currentTimeMillis();
					Long timeElapsed = stop - start;
					System.out.println("------ time elapsed for operation: "+timeElapsed+"ms -----");
					Thread.sleep(LocalConstants.DAY_IN_MILLIS - timeElapsed);
				}
			}

			private void updatePayments(WebController controller) {
				Integer date = controller.getCurrentDate();
				List<Booking> dueToday = bookingRepo.findByCancelLatest(date - 1); 
				if (!dueToday.isEmpty()) {
					for (Booking b : dueToday) {
						if (!b.getCancel()){
							b.setPayment(true);
							bookingRepo.save(b);
						}
					}
				}
			}

			private void updateDDoSUserTrust(Scanner scanner, WebController controller) {
				List<String> temp = new ArrayList<>();
				while(scanner.hasNextLine()) {
					temp.add(scanner.nextLine());
				}
				scanner.close();
				
				if (temp.size() >= 2) {
					String malUsers = temp.get(temp.size()-2);
					String[] malUsersArr = malUsers.split("#");
					if (malUsersArr.length > 0) {
						System.out.println("---- "+malUsers+" ----");
						for (int i = 0; i < malUsersArr.length; i++) {
							List<User> users = userRepo.findByName(malUsersArr[i]);
							if(users.isEmpty()) {
								System.out.println("Malicious user not found "+malUsersArr[i]);
							} else {
								User u = users.get(0);
								if (u.getTrust() > 0) {
									u.setTrust(u.getTrust()-1);
								}								
								userRepo.save(u);
							}
						}
					} else {
						System.out.println("---- Malicious users file is empty ----");
					}							
				}			
				
			}
			
			private void restoreBenignUserTrust(Scanner scanner, WebController controller) {
				List<String> temp = new ArrayList<>();
				while(scanner.hasNextLine()) {
					temp.add(scanner.nextLine());
				}
				scanner.close();
				
				if (temp.size() >= 2) {
					String benignUsers = temp.get(temp.size()-2);
					String[] benignUsersArr = benignUsers.split("#");
					if (benignUsersArr.length > 0) {
						System.out.println("++++ "+benignUsers+" ++++");
						for (int i = 0; i < benignUsersArr.length; i++) {
							List<User> users = userRepo.findByName(benignUsersArr[i]);
							if(users.isEmpty()) {
								System.out.println("Benign user not found "+benignUsersArr[i]);
							} else {
								User u = users.get(0);
								u.setTrust(LocalConstants.TRUSTLEVEL_HIGH);
								userRepo.save(u);
							}
						}
					} else {
						System.out.println("---- Benign users file is empty ----");
					}							
				}
			}

			private Integer updateDateAOP(WebController controller) {
				return controller.getCurrentDate();				
			}

		};
	}
	



}
