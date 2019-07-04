package com.example.demo.configuration;

import org.apache.tomcat.jni.Time;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
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

import com.example.demo.larva.*;

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
	
	//FOR BENCHMARKING
	public static ArrayList<Long> globalTimes = new ArrayList<>();
	public static ArrayList<Long> methodCallTimes = new ArrayList<>();
	public static ArrayList<Long> aspectTimes = new ArrayList<>();
	//FOR BENCHMARKING
	
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
		User malicious = new User("malicious", "6666666666");
		malicious.setTrust(LocalConstants.TRUSTLEVEL_LOW);
		User cancel = new User("cancel", "1019239812");
		cancel.setTrust(LocalConstants.TRUSTLEVEL_MED);
		User trusted = new User("trusted", "1234567890");		
		userRepo.save(malicious);
		userRepo.save(cancel);
		userRepo.save(trusted);
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
                    if ((currentDate % LocalConstants.SIMULATION_DURATION) == 0) {
						saveStatistics();
					}
					Long stop = System.currentTimeMillis();
					Long timeElapsed = stop - start;
					System.out.println("------ time elapsed for operation: "+timeElapsed+"ms -----");
					Thread.sleep(LocalConstants.DAY_IN_MILLIS - timeElapsed);					
				}
			}

			private void saveStatistics() {
				System.out.println("+++ SAVING STATS +++");
				String fileName = "/stats-ddos-";
				String ddosThreshold = "threshold-"+LocalConstants.CRITICAL_NO_ATTACKERS+"-";
				String period = LocalConstants.SIMULATION_DURATION+"-";
				String timeStamp = System.currentTimeMillis()+".txt";
				File file = null;
				File statsFile = null;
				try {
					file = new File(LocalConstants.statisticsDirectory+fileName+ddosThreshold+period+timeStamp);
					if (file.createNewFile()) {
						System.out.println("new file created");
					} else {
						System.out.println("writing to existing file");
					}
					statsFile = new File(LocalConstants.statisticsDirectory+"/time-stats"+System.currentTimeMillis()+".txt");
					if (statsFile.createNewFile()) {
						System.out.println("new file created");
					} else {
						System.out.println("writing to existing file");
					}
				} catch (IOException ioe) {
					System.out.println("IOException occured");
				} 
				
				int allDetectedMalUsers = 0;
				int allMalUsers = 0;
				int allFalsePositives = 0;
				int allFalsePositivesZeroTrust = 0;
				int allBenignUsers = 0;
				float detectionRate = 0;
				float falsePositiveRate = 0;
				float falsePositiveRateZeroTrust = 0;
				List<Service> items;
				
				allDetectedMalUsers = getAllDetectedMalUsers();
				allMalUsers = getAllMalUsers();
				
				try {
					detectionRate = ((float) allDetectedMalUsers / (float) allMalUsers);
				} catch (ArithmeticException e) {
					System.out.println("No malicious users in set, attempted division by zero");
				}
				
				allFalsePositives = getAllFalsePositives();
				allFalsePositivesZeroTrust = getAllFalsePositivesZeroTrust();
				allBenignUsers = getAllBenignUsers();
				
				try {
					falsePositiveRate = ((float) allFalsePositives / (float) allBenignUsers);
					falsePositiveRateZeroTrust = ((float) allFalsePositivesZeroTrust / (float) allBenignUsers);
				} catch (ArithmeticException e) {
					System.out.println("No benign users in set, attempted division by zero");
				}
								
				items = serviceRepo.findAll();
				
				PrintWriter stats = null;
				PrintWriter timeStats = null;
				try {
					stats = new PrintWriter(file);
					stats.println("### DETECTION RATE IS:       "+detectionRate);
					stats.println("### NO. DET MALICIOUS USERS: "+allDetectedMalUsers);
					stats.println("### NO. ALL MALICIOUS USERS: "+allMalUsers);
					stats.println("### ");
					stats.println("### FALSE POSITIVE RATE IS:           "+falsePositiveRate);
					stats.println("### FALSE POSITIVES (WORST CASE)      "+falsePositiveRateZeroTrust);
					stats.println("### NO. WRONGLY DET USERS             "+allFalsePositives);
					stats.println("### NO. WRONGLY DET USERS (WORST CASE)"+allFalsePositivesZeroTrust);
					stats.println("### NO. BENIGN USERS:                 "+allBenignUsers);
					stats.println("###");
					stats.println("DAY - AV ROOMS - OCCUPANCY %");
					
					for (int i = 1; i <= LocalConstants.SIMULATION_DURATION; i++) {
						Service s = items.get(i);
						Integer avRooms = s.getRooms();
						Float occupancyRate = (float) (LocalConstants.NUMBER_ROOMS - avRooms) / (float) LocalConstants.NUMBER_ROOMS;
						stats.println(i+" - "+avRooms+" - "+occupancyRate);
					}
									
					Long minGlobal = findMinValue(globalTimes);
					Long minMethodCall = findMinValue(methodCallTimes);
					Long minAspect = findMinValue(aspectTimes);
					
					Long maxGlobal = findMaxValue(globalTimes);
					Long maxMethodCall = findMaxValue(methodCallTimes);
					Long maxAspect = findMaxValue(aspectTimes);
					
					Double avgGlobal = getAvg(globalTimes);
					Double avgMethodCall = getAvg(methodCallTimes);
					Double avgAspect = getAvg(aspectTimes);
					
					int lenGlobal = globalTimes.size();					
					int lenMethodCall = methodCallTimes.size();					
					int lenAspect = aspectTimes.size();
					
					stats.println();
					stats.println("### BENCHMARK FOR Booking.setCancel GLOBAL");
					stats.println("### AVG GLOBAL:      "+avgGlobal);
					stats.println("### MIN GLOBAL:      "+minGlobal);
					stats.println("### MAX GLOBAL:      "+maxGlobal);
					stats.println("### LEN GLOBAL:      "+lenGlobal);
					stats.println("### AVG METHOD CALL: "+avgMethodCall);
					stats.println("### MIN METHOD CALL: "+minMethodCall);
					stats.println("### MAX METHOD CALL: "+maxMethodCall);
					stats.println("### LEN METHOD CALL: "+lenMethodCall);
					stats.println("### AVG ASPECT:      "+avgAspect);
					stats.println("### MIN ASPECT:      "+minAspect);
					stats.println("### MAX ASPECT:      "+maxAspect);
					stats.println("### LEN ASPECT:      "+lenAspect);
							
					stats.flush();
					stats.close();
					
					timeStats = new PrintWriter(statsFile);
					timeStats.println("### GLOBAL TIMES:");
					for (Long l : globalTimes) {
						timeStats.println(l);
					}
					timeStats.println("### METHOD CALL TIMES:");
					for (Long l : methodCallTimes) {
						timeStats.println(l);
					}
					timeStats.println("### ASPECT TIMES:");
					for (Long l : aspectTimes) {
						timeStats.println(l);
					}
					
					timeStats.flush();
					timeStats.close();			
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				
				// 1) 	Write file with DDOS threshold and date (e.g. stats-ddosX-xxxx)
				// 2.1) DETECTION RATE: How many users with trustlevel 0 and trustlevel 1 AND name like 'malicious%'?
				// 2.2) compared to HOW many users with name like 'malicious%' AND trustlevel 2 and cancellations > 0
				// 3.1) FALSE POSITIVES: How many users where trustreparation > 1 AND name like 'spont%', 'normal%' or 'planned%'
				// 3.2) HOW many were to lowest trust level (i.e. no service) where lowestrustlevel == 0 AND name like 'spont%', 'normal%' or 'planned%'
				// 3.3) compared to: How many users where lowesttrustlevel == 2 AND name like 'spont%', 'normal%' or 'planned%'
				// 4.1) HOW did the IDS impact the occupancy rate?
				// 4.2) Plot bookings without IDS vs. bookings with IDS 
				// 5.1)	WHAT was the performance impact of the RV system 
			}

			private Long findMaxValue(ArrayList<Long> globalTimes) {
				long max = globalTimes.get(0);
				for (Long l : globalTimes) {
					if (l > max) {
						max = l;
					}
				}
				return max;
			}

			private Long findMinValue(ArrayList<Long> globalTimes) {
				Long min = globalTimes.get(0);
				for (Long l : globalTimes) {
					if (l < min) {
						min = l;
					}
				}
				return min;
			}

			private Double getAvg(ArrayList<Long> globalTimes) {
				Long sum = 0L;
				for (Long l : globalTimes) {
					sum = sum + l;
				}
				Double avg = Double.valueOf(sum) / Double.valueOf(globalTimes.size()); 
				return avg;
			}

			private int getAllBenignUsers() {
				List<User> allBenignUsers;
				allBenignUsers = userRepo.allBenignUsers();
				return allBenignUsers.size();
			}
			
			private int getAllMalUsers() {
				List<User> allMalUsers;
				allMalUsers = userRepo.allMaliciousUsers();
				return allMalUsers.size();
			}

			private int getAllFalsePositivesZeroTrust() {
				List<User> allFalsePositivesZeroTrust;
				allFalsePositivesZeroTrust = userRepo.allBenignFalseUntrusted();
				return allFalsePositivesZeroTrust.size();
			}

			private int getAllFalsePositives() {
				List<User> allFalsePositives;
				allFalsePositives = userRepo.allBenignFalsePositives();
				return allFalsePositives.size();
			}

			private int getAllDetectedMalUsers() {
				List<User> allDetectedMalUsers;
				allDetectedMalUsers = userRepo.allMaliciousDetected();
				return allDetectedMalUsers.size();
			}

			private void updatePayments(WebController controller) {
				Integer date = controller.getCurrentDate();
				List<Booking> dueToday = bookingRepo.findByCancelLatest(date - 1); 
				if (!dueToday.isEmpty()) {
					for (Booking b : dueToday) {
						if (!b.getCancel() && b.getConfirm()){
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
									u.setLowestTrustLevel(u.getTrust());
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
								if (u.getTrust() < LocalConstants.TRUSTLEVEL_HIGH) {
									u.setTrust(LocalConstants.TRUSTLEVEL_HIGH);
									u.setTrustReparations(u.getTrustReparations() + 1);
								} else {
									u.setTrust(u.getTrust() + 1);
								}				
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
