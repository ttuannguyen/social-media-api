package com.groupfour.socialmedia.services.impl;

import com.groupfour.socialmedia.entities.Credentials;
import com.groupfour.socialmedia.entities.User;
import com.groupfour.socialmedia.repositories.UserRepository;
import com.groupfour.socialmedia.services.ValidateService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

    private final UserRepository userRepository;

    @Override
    public boolean validateUsername(String username) {
        Optional<User> user = userRepository.findByCredentialsUsername(username);
        return user.isEmpty();
    }

    @Override
    public boolean validateUsernameExists(String username) {
        Optional<User> user = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
        return user.isPresent();
    }

    @Override
    public boolean validateCredentialsExist(String username, String password) {
        Optional<User> user = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(username, password);
        return user.isPresent();
    }
	
}
