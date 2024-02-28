package com.groupfour.socialmedia.services;

import com.groupfour.socialmedia.entities.User;
import com.groupfour.socialmedia.repositories.UserRepository;

import java.util.Optional;

public interface ValidateService {

    boolean validateUsername(String username);

    boolean validateUsernameExists(String username);

}
