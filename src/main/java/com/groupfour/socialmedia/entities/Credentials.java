package com.groupfour.socialmedia.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;

@Embeddable
public class Credentials {

	private String username;
	private String password;
}
