package com.groupfour.socialmedia.services;

import com.groupfour.socialmedia.dtos.CredentialsDto;

public interface ValidateService {
	
	boolean validateUsername(String username);

	boolean validateUsernameExists(String username);
	
	boolean validateCredentialsExist(String username, String password);
	
	boolean validateHashtagExists(String label);

	
	
}
