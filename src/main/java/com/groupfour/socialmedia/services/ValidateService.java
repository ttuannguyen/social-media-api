package com.groupfour.socialmedia.services;

public interface ValidateService {

	boolean validateUsername(String username);

	boolean validateUsernameExists(String username);

	boolean validateCredentialsExist(String username, String password);

    boolean validateHashtagExists(String label);

}
