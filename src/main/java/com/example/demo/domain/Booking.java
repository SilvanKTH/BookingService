package com.example.demo.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

@Entity
public class Booking {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long Id;
	//@ManyToOne //Maps one or more bookings to a user
	//private User user; 
	@NotNull
	private String name;
	@Range(min=0, max=10)
	private Integer rooms;
	@Range(min=0)
	private Integer arrival;
	@Range(min=0)
	private Integer departure;
	@Range(min=0)
	private Integer cancelLatest;
	private Boolean confirm;
	private Boolean cancel;
	@Range(min=0)
	private Integer cancelDate;
	
	public Booking() {}

	public Booking(@NotNull String name, @Range(min = 0, max = 10) Integer rooms, @Range(min = 0) Integer arrival,
			@Range(min = 0) Integer departure, @Range(min = 0) Integer cancelLatest) {
		super();
		this.name = name.trim();
		this.rooms = rooms;
		this.arrival = arrival;
		this.departure = departure;
		this.cancelLatest = cancelLatest;
		this.confirm = false;
		this.cancel = false;
		this.cancelDate = null;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getRooms() {
		return rooms;
	}

	public void setRooms(Integer rooms) {
		this.rooms = rooms;
	}

	public Integer getArrival() {
		return arrival;
	}

	public void setArrival(Integer arrival) {
		this.arrival = arrival;
	}

	public Integer getDeparture() {
		return departure;
	}

	public void setDeparture(Integer departure) {
		this.departure = departure;
	}
	
	public Integer getCancelLatest() {
		return cancelLatest;
	}

	public void setCancelLatest(Integer cancelLatest) {
		this.cancelLatest = cancelLatest;
	}

	public Boolean getConfirm() {
		return confirm;
	}

	public void setConfirm(Boolean confirm) {
		this.confirm = confirm;
	}

	public Boolean getCancel() {
		return cancel;
	}

	public void setCancel(Boolean cancel) {
		this.cancel = cancel;
	}
	
	public Integer getCancelDate() {
		return cancelDate;
	}
	
	public void setCancelDate(Integer cancelDate) {
		this.cancelDate = cancelDate;
	}

	@Override
	public String toString() {
		return "Booking [Id=" + Id + ", name=" + name + ", rooms=" + rooms + ", arrival=" + arrival + ", departure="
				+ departure + ", cancelLatest=" + cancelLatest + ", confirm=" + confirm + ", cancel=" + cancel
				+ ", cancelDate=" + cancelDate + "]";
	}

}
