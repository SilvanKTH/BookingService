package com.example.demo.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.example.demo.constants.LocalConstants;

@Entity
//@Table(uniqueConstraints={@UniqueConstraint(columnNames=("name"))})
public class User {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@NotNull
	private String name;

	//@Positive
	private Integer trust;

	@NotNull
	private String creditcard;
	
	private Integer bookings;
	
	private Integer cancellations;
	
	private Integer trustReparations;
	
	private Integer lowestTrustLevel;
	
	public User () {}
	
	public User(String name, String creditcard) {
		this.name = name.trim();
		this.creditcard = creditcard;
		this.trust = LocalConstants.TRUSTLEVEL_HIGH; // Default setting, may be changed 
		this.bookings = 0;
		this.cancellations = 0;
		this.trustReparations = 0;
		this.lowestTrustLevel = LocalConstants.TRUSTLEVEL_HIGH;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreditcard() {
		return creditcard;
	}

	public void setCreditcard(String creditcard) {
		this.creditcard = creditcard;
	}

	public Integer getTrust() {
		return trust;
	}
	
	public void setTrust(Integer trust) { // probably no good practise
		if(trust >= LocalConstants.TRUSTLEVEL_LOW) {
				//&& trust <= LocalConstants.TRUSTLEVEL_HIGH) {
			this.trust = trust;
		}	
	}

	public Integer getBookings() {
		return bookings;
	}

	public void setBookings(Integer bookings) {
		this.bookings = bookings;
	}

	public Integer getCancellations() {
		return cancellations;
	}

	public void setCancellations(Integer cancellations) {
		this.cancellations = cancellations;
	}

	public Integer getTrustReparations() {
		return trustReparations;
	}

	public void setTrustReparations(Integer trustReparations) {
		this.trustReparations = trustReparations;
	}

	public Integer getLowestTrustLevel() {
		return lowestTrustLevel;
	}

	public void setLowestTrustLevel(Integer lowestTrustLevel) {
		this.lowestTrustLevel = lowestTrustLevel;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", trust=" + trust + ", creditcard=" + creditcard + ", bookings="
				+ bookings + ", cancellations=" + cancellations + ", trustReparations=" + trustReparations
				+ ", lowestTrustLevel=" + lowestTrustLevel + "]";
	}

	
	
}
