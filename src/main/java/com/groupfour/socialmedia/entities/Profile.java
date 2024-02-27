package com.groupfour.socialmedia.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;

@Embeddable
public class Profile {
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
}
