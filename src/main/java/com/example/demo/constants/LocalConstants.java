package com.example.demo.constants;

public class LocalConstants {
	
	//Determine the time used in the simulation
	//E.g. one day will simulated in one minute 
	public static Long DAY_IN_MILLIS = 60000L;
	
	// Regarding the user trust level 
	public static final Integer TRUSTLEVEL_HIGH = 2;
	public static Integer TRUSTLEVEL_LOW = 0;
	
	// Regarding the available items
	public static Integer NUMBER_ROOMS = 200; 
	public static Integer INITIAL_PERIOD = 100;
	
	// Regarding User naming
	public static String USER_PLACEHOLDER = "PLACEHOLDER";
	
	// Regarding Creditcard number
	public static Integer CREDITCARD_LENGTH = 10;
	public static String CREDITCARD_PLACEHOLDER = "0000000000";
	
	// Regarding Business Rules: 
	public static String HIGH_TRUST_MESSAGE = "Thanks for your booking.\n"
			+ "You may cancel your reservation for free until 1 time unit before arrival.";
	public static String MED_TRUST_MESSAGE = "Thanks for your booking.\n"
			+ "We are processing your reservation.\n"
			+ "You may cancel your reservation through dialing our service center until 1 time unit before your arrival.";
	public static String LOW_TRUST_MESSAGE = "Thanks for your booking.\n"
			+ "We are processing your reservation.\n"
			+ "Please acknowledge that we charge a reservation fee of 20 %.\n"
			+ "You may cancel your reservation until 1 time unit before your arrival.";
	
	// Regarding Monitoring File
	public static String malUsersFile = "/Users/silvanzeller/Desktop/TCOMM/Master Thesis/BookingService/src/main/java/com/example/demo///mal_users.txt";
	public static String trustedUsersFile = "/Users/silvanzeller/Desktop/TCOMM/Master Thesis/BookingService/src/main/java/com/example/demo///trusted_users.txt";
}
