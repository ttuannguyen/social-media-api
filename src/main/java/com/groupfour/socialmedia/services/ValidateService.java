package com.groupfour.socialmedia.services;

public interface ValidateService {
	
	boolean validateUsername(String username);

	boolean validateUsernameExists(String username);
	
	boolean validateTagExists(String label);
	
}
