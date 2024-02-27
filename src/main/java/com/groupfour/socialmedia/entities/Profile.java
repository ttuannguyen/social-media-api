package com.groupfour.socialmedia.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Embeddable
public class Profile {

	private String firstName;
	private String lastName;
	@Column(nullable=false)
	private String email;
	private String phone;
}
