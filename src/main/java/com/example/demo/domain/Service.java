package com.example.demo.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.example.demo.constants.LocalConstants;

@Entity
public class Service {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@NotNull
	private Long day;
	
	//@Positive
	private Integer availableRooms;
	
	// for performance measures
	private Integer totalAvailableRooms;
	
	public Service() {}
	
	
	
	public Service(@Positive Long day, @Positive Integer availableRooms, Integer totalAvailableRooms) {
		this.day = day;
		this.availableRooms = availableRooms;
		this.totalAvailableRooms = totalAvailableRooms;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDay() {
		return day;
	}

	public void setDay(Long day) {
		this.day = day;
	}

	public Integer getRooms() {
		return availableRooms;
	}

	public void setRooms(Integer rooms) {
		this.availableRooms = rooms;
	}

	public Integer getTotalAvailableRooms() {
		return totalAvailableRooms;
	}

	public void setTotalAvailableRooms(Integer totalAvailableRooms) {
		this.totalAvailableRooms = totalAvailableRooms;
	}

	@Override
	public String toString() {
		return "Service [id=" + id + ", day=" + day + ", rooms=" + availableRooms + ", totalBookedRooms=" + totalAvailableRooms
				+ "]";
	}

	
}
