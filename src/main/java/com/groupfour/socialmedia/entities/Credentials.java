package com.groupfour.socialmedia.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;

@Embeddable
public class Credentials {

	@Column(nullable=false, unique=true)
	private String username;
	@Column(nullable=false)
	private String password;
}
