package com.example.demo.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.example.demo.constants.LocalConstants;

@Entity
public class Creditcard {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@NotNull
	private String cardNumber;
	
	@NotNull
	private String holder;
	
	public Creditcard() {}
	
	public Creditcard(String holder, String cardNumber) {
		this.holder = holder;
		String regex = "\\d+";
		if (cardNumber.trim().matches(regex)) {
			if (cardNumber.length() == LocalConstants.CREDITCARD_LENGTH) { // might result in issues because of Integer vs. int comparison
				this.cardNumber = cardNumber;
			}
		}
		else {
			this.cardNumber = LocalConstants.CREDITCARD_PLACEHOLDER;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

}
