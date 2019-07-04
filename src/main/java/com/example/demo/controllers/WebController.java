package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.configuration.InitialConfiguration;
import com.example.demo.constants.LocalConstants;
import com.example.demo.domain.Booking;
import com.example.demo.domain.Service;
import com.example.demo.domain.User;
import com.example.demo.repository.BookingRepo;
import com.example.demo.repository.CreditcardRepo;
import com.example.demo.repository.ServiceRepo;
import com.example.demo.repository.UserRepo;

@Controller
@RequestMapping
public class WebController {
	
	@Autowired
	UserRepo userRepo;
	@Autowired
	BookingRepo bookingRepo;
	@Autowired
	CreditcardRepo creditcardRepo; // NOT REALLY NEEDED
	@Autowired
	ServiceRepo serviceRepo;
	
	public static Long controllerTimeReference = 0L;
		
	// Redirect to bookingservice URL
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String forwardHome() {
		return "redirect:/bookingservice";
	}	

	// Display home page
	@RequestMapping(value="/bookingservice", method=RequestMethod.GET)
	public String getHome(Model model) {
		Integer timeUnit = getCurrentDate();
		model.addAttribute("timeUnit", timeUnit);
		
		return "Web/home";
	}
	
	// Display reservation page
	@RequestMapping(value="/reservation", method=RequestMethod.GET)
	public String getReservation(Model model) {
		Integer timeUnit = getCurrentDate();
		model.addAttribute("timeUnit", timeUnit);
		 
		return "Web/reservation";
	}
	
	// Handle reservation and display contact data page
	@RequestMapping(value="/contact", method=RequestMethod.POST)
	public String makeReservation(HttpServletRequest request, Model model) throws NumberFormatException{
		String name = request.getParameter("name").trim();
		String message; 
		try {
			Integer rooms = Integer.parseInt(request.getParameter("rooms"));
			Integer arrival = Integer.parseInt(request.getParameter("arrival"));
			Integer departure = Integer.parseInt(request.getParameter("departure")) + arrival;
			Integer cancelLatest = 0;
			List<User> users = userRepo.findByName(name);
			User u;
			if (users.isEmpty()) {
				u = new User(name, LocalConstants.CREDITCARD_PLACEHOLDER);
				cancelLatest = arrival - getCancellationPeriod(u, arrival);
				if (cancelLatest < getCurrentDate()) {
					cancelLatest = getCurrentDate();
				}
			} else {
				u = users.get(0);
				cancelLatest = arrival - getCancellationPeriod(u, arrival);
				
				if (cancelLatest < getCurrentDate() + 1) {
					cancelLatest = getCurrentDate() + 1;
				}
			}
			if (rooms > 0 && rooms <= 4 && arrival >= getCurrentDate() && departure >= arrival) {
				Integer availableRooms = getAvailableRooms(rooms, arrival, departure);
				boolean hasActiveBookings = false;
				if (availableRooms >= rooms && !hasActiveBookings) {
					Booking booking = new Booking(name, rooms, arrival, departure, cancelLatest); 
					bookingRepo.save(booking);
					message = "The booking is being processed ... ";
				} else if (availableRooms < rooms){
					message = "ERROR: Unfortunately, we are already fully booked during this period.";
				} else {
					message = "ERROR: You still have bookings pending.";
				}
			} else {
				message = "ERROR: You can only book between 1-4 rooms and enter valid dates.";
			}
		} catch (NumberFormatException ex) {
			message = "ERROR: You can only enter numbers in the fields!";
		}
		Integer timeUnit = getCurrentDate();
		model.addAttribute("timeUnit", timeUnit);
		model.addAttribute("message", message);		
		
		return "Web/contact";
	}
	
	/*
	private boolean checkCurrentBookings(String name) {
		boolean hasCurrentBookings = false;
		List<Booking> bookings = bookingRepo.findByName(name);
		for (Booking b : bookings) {
			if (b.getConfirm() && !b.getCancel() && !b.getPayment()) {
				hasCurrentBookings = true;
			}		
		}		
		return hasCurrentBookings;
	}
	*/

	// Determines service class 
	// if TrustLevel >= high --> cancel on short notice possible
	// if TrustLevel == med  --> longer cancellation period
	// if TrustLevel == low  --> prepaid, i.e. payment on the same day
	private Integer getCancellationPeriod(User u, Integer arrival) {
		Integer high = LocalConstants.TRUSTLEVEL_HIGH;
		Integer med = LocalConstants.TRUSTLEVEL_MED;
		Integer low = LocalConstants.TRUSTLEVEL_LOW;
		if (u.getTrust() >= high) {
			return 1;
		} else if (u.getTrust() == med) {
			return 5;
		} else if (u.getTrust() == low){
			return arrival - getCurrentDate();
		} else {
			return arrival - getCurrentDate(); //should not occur
		}
	}

	// Display availability check page
	@RequestMapping(value="/check-availability", method=RequestMethod.GET)
	public String getAvailablity(Model model) {
		Integer timeUnit = getCurrentDate();
		model.addAttribute("timeUnit", timeUnit);
		
		return "Web/check-availability";
	}
	
	// Display login page
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String getLogin(Model model) {
		Integer timeUnit = getCurrentDate();
		model.addAttribute("timeUnit", timeUnit);
		
		return "Web/login";
	}
	
	// Handle login input and display user booking page
	@RequestMapping(value="/my-bookings", method=RequestMethod.POST)
	public String getBookings(HttpServletRequest request, Model model) {
		String username = request.getParameter("username");
		model.addAttribute("username", username);
		List<Booking> bookings = bookingRepo.findByName(username);
		model.addAttribute("bookings", bookings);
		Integer timeUnit = getCurrentDate();
		model.addAttribute("timeUnit", timeUnit);
		
		return "Web/my-bookings";
	}
	
	// Handle user data input and display confirmation page	
	@RequestMapping(value="/confirmation", method=RequestMethod.POST)
	public String setContactData(HttpServletRequest request, Model model) {
		String name = request.getParameter("name");
		String creditcard = request.getParameter("creditcard");
		String username, rooms, arrival, departure, cancelLatest, message;
		List<Booking> bookingList = bookingRepo.findByName(name);	
		int length = bookingList.size();
		if (length <= 0) {
			username = name;
			rooms = "0";
			arrival = "0";
			departure = "0";
			cancelLatest = "0";
			message = "ERROR: No matching booking for user = "+name+" found in DB.";
		} else {
			Booking latestBooking = bookingList.get(length-1);
			username = latestBooking.getName();			
			List<User> userList = userRepo.findByName(name);
			if (userList.size() <= 0) {
				User u = new User(name, creditcard);
				userList.add(u);
				userRepo.save(u);
			} else {
				User u = userList.get(0);
				if (!u.getCreditcard().equals(creditcard)) {
					u.setCreditcard(creditcard);
					userRepo.save(u);
				}
			}
			
			Integer userRooms = latestBooking.getRooms();
			Integer userArrival = latestBooking.getArrival();
			Integer userDeparture = latestBooking.getDeparture();
			Integer userCancelLatest = latestBooking.getCancelLatest();
		
			Integer availableRooms = getAvailableRooms(userRooms, userArrival, userDeparture);
			if (availableRooms >= userRooms) {
				if (handleReservation(userRooms, userArrival, userDeparture)) {
					latestBooking.setConfirm(true);
					User u = userList.get(0);
					u.setBookings(u.getBookings() + 1);
					userRepo.save(u);
					bookingRepo.save(latestBooking);
					message = "Thanks for your booking, "+username+"! Your booking ID is "+latestBooking.getId()+" .";
				} else {
					message = "ERROR: Writing to DB was not successful.";
				}
			} else {
				message = "Unfortunately, we are fully booked during your requested period!";
			}
			rooms = String.valueOf(userRooms);
			arrival = String.valueOf(userArrival);
			departure = String.valueOf(userDeparture);
			cancelLatest = String.valueOf(userCancelLatest);
		}
		Integer timeUnit = getCurrentDate();
		model.addAttribute("timeUnit", timeUnit);			
		model.addAttribute("username", username);
		model.addAttribute("rooms", rooms);
		model.addAttribute("arrival", arrival);
		model.addAttribute("departure", departure);
		model.addAttribute("cancelLatest", cancelLatest);
		model.addAttribute("message", message);
				
		return "Web/confirmation";
	}

	// Handle cancellation input and display cancel confirmation page
	@RequestMapping(value="/cancel-confirmation", method=RequestMethod.POST)
	public String cancelConfirmation(HttpServletRequest request, Model model) throws NumberFormatException {
		Long globalStartTimeNanos = System.nanoTime();
		Long methodStartTimeNanos = 0L;
		Long methodStopTimeNanos = 0L;
		String name, message;
		Long id;
		Integer cancelDate;
		try {
			id = Long.valueOf(request.getParameter("id"));
			Optional<Booking> booking = bookingRepo.findById(id);
			
			if (booking.isPresent()) {
				Booking b = booking.get();
				name = b.getName();
				cancelDate = getCurrentDate();
				b.setCancelDate(cancelDate);
				if (handleCancellation(b)) {
					if (!b.getCancel()) {
						methodStartTimeNanos = System.nanoTime();
						b.setCancel(true);
						methodStopTimeNanos = System.nanoTime();
						returnRooms(b);
						List<User> userList = userRepo.findByName(name);
						User u = userList.get(0);
						u.setCancellations(u.getCancellations() + 1);
						userRepo.save(u);
						bookingRepo.save(b);
						message = "We hope to seeing you again soon!";
					} else {
						message = "You have already cancelled this reservation";
					}						
				}
				else {
					message = "You have tried cancelling your reservation after the due period";
				}
			} else {
				name = "but there was no booking found in our DB.";
				message = "Please check your current bookings!";
			}
			model.addAttribute("id", id);
			model.addAttribute("name", name);
			model.addAttribute("message", message);
		} catch (NumberFormatException ex) {
			id = 0L;
			name = "USER NOT FOUND";
			message = "ERROR: You can only enter numbers in the fields!";
			model.addAttribute("name", name);
			model.addAttribute("message", message);
		}		
		Integer timeUnit = getCurrentDate();
		model.addAttribute("timeUnit", timeUnit);
		
		Long globalStopTimeNanos = System.nanoTime();
		Long globalTime = globalStopTimeNanos - globalStartTimeNanos;
		Long methodCallTime = methodStopTimeNanos - methodStartTimeNanos;
		InitialConfiguration.globalTimes.add(globalTime);
		InitialConfiguration.methodCallTimes.add(methodCallTime);
		return "Web/cancel-confirmation";
	}
	
	private void returnRooms(Booking b) {
		Integer rooms = b.getRooms();
		Integer arrival = b.getArrival();
		Integer departure = b.getDeparture();
		for (int i = arrival; i <= departure; i++) {
			Optional<Service> o = serviceRepo.findByDay(Long.valueOf(i));
			if (o.isPresent()) {
				Service s = o.get();
				Integer items = s.getRooms() + rooms;
				s.setRooms(items);
				//s.setTotalAvailableRooms(items);
			}			
		}
	}

	// Handle availability request data and return available rooms 
	@RequestMapping(value="/available-rooms", method=RequestMethod.POST)
	public String getAvailability(HttpServletRequest request, Model model) throws NumberFormatException{
		Integer rooms, arrival, departure;
		Integer minAvailableRooms = 0;
		String message;
		try {
			rooms = Integer.parseInt(request.getParameter("rooms"));		
			arrival = Integer.parseInt(request.getParameter("arrival"));
			departure = Integer.parseInt(request.getParameter("departure")) + arrival;
			if (rooms > 0 && rooms <= 4 && arrival >= 0 && departure >= arrival) {
				minAvailableRooms = getAvailableRooms(rooms, arrival, departure);	
				arrival = getCurrentDate() + arrival; //this is for making the scripting easier
				departure = getCurrentDate() + departure;
				message = "Thanks for your request!";
			} else {
				rooms = 0;
				arrival = 0;
				departure = 0;
				message = "ERROR: You can only book between 1-4 rooms and enter valid dates";
			}								
		} catch (NumberFormatException ex) {
			rooms = 0;
			arrival = 0;
			departure = 0;
			message = "ERROR: You can only enter numbers in the fields!";
		}
		Integer timeUnit = getCurrentDate();
		rooms = minAvailableRooms;
		model.addAttribute("rooms", rooms);
		model.addAttribute("arrival", arrival);
		model.addAttribute("departure", departure);
		model.addAttribute("message", message);
		model.addAttribute("timeUnit", timeUnit);
		
		return "Web/available-rooms";
	}

	private synchronized Integer getAvailableRooms(@Positive Integer rooms, @Positive Integer arrival, @Positive Integer departure) {
		List<Service> allDates = serviceRepo.findAll();
		Integer latestDateInCalendar = allDates.size() - 1; 
		if (arrival > latestDateInCalendar) {
			//System.out.println("ARRIVAL AFTER LAST DAY IN CALENDAR");
			for (int i = latestDateInCalendar + 1; i < arrival; i++) {
				Long day = Long.valueOf(i);
				Service s = new Service(day, LocalConstants.NUMBER_ROOMS, LocalConstants.NUMBER_ROOMS);
				serviceRepo.save(s);
				//System.out.println("Before arrival, saving "+s.toString());
			}
		}
		
		if (departure > latestDateInCalendar) {
			//System.out.println("DEPARTURE AFTER LAST DAY IN CALENDAR");
			for (int i = arrival; i <= departure; i++) {
				if (i > latestDateInCalendar) {
					Long day = Long.valueOf(i);
					Service s = new Service(day, LocalConstants.NUMBER_ROOMS, LocalConstants.NUMBER_ROOMS);
					serviceRepo.save(s);
					//System.out.println("Between arrival and departure, saving "+s.toString());
				}
			}
		}
		
		Integer availableRooms = LocalConstants.NUMBER_ROOMS;
		for (int i = arrival; i <= departure; i++) {		
			Optional<Service> o = serviceRepo.findByDay(Long.valueOf(i));
			if (o.isPresent()) {
				Service s = o.get();
				if (s.getRooms() < availableRooms) {
					availableRooms = s.getRooms();
				}
			}
			else {
				availableRooms = 0;
			}
		}
		return availableRooms;
	}
	
	public Integer getCurrentDate() {
		Long timeReference = InitialConfiguration.timeReference;
		Long timeSinceStartup = (System.currentTimeMillis() - timeReference);
		Long normedTime = timeSinceStartup / LocalConstants.DAY_IN_MILLIS;		
		
		return normedTime.intValue() % Integer.MAX_VALUE;
	}

	private synchronized boolean handleReservation(Integer rooms, Integer arrival, Integer departure) {
		boolean success = true;
		for (int i = arrival; i <= departure; i++) {
			Optional<Service> o = serviceRepo.findByDay(Long.valueOf(i));
			if (o.isPresent()) {
				Service s = o.get();
				Integer items = s.getRooms() - rooms; 
				if (items < 0) {
					success = false;
					restoreServiceDB(arrival, i, rooms);
					break; 
				}
				s.setRooms(items);
				s.setTotalAvailableRooms(items);
				serviceRepo.save(s);
			} else {
				success = false; 
				break;
			}
		}		
		return success;
	}
	
	private void restoreServiceDB(Integer arrival, int lastItem, Integer rooms) {
		for (int j = arrival; j <= lastItem; j++) {
			Optional<Service> o = serviceRepo.findByDay(Long.valueOf(j));
			if (o.isPresent()) {
				Service s = o.get();
				Integer items = s.getRooms() + rooms;
				Integer virtualItems = s.getTotalAvailableRooms() + rooms;
				s.setRooms(items);
				s.setTotalAvailableRooms(virtualItems);
				serviceRepo.save(s);
			} else {
				break;
			}
		}
	}

	private synchronized boolean handleCancellation(Booking b) {
		boolean success = true;
		if (getCurrentDate() > b.getCancelLatest()) {
			success = false;
		}
		
		return success;
	}
}
