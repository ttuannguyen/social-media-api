package com.groupfour.socialmedia.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.groupfour.socialmedia.entities.Hashtag;
import com.groupfour.socialmedia.entities.User;
import com.groupfour.socialmedia.exceptions.BadRequestException;
import com.groupfour.socialmedia.repositories.HashtagRepository;
import com.groupfour.socialmedia.repositories.UserRepository;
import com.groupfour.socialmedia.services.ValidateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;



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


	@Override	
    public boolean validateHashtagExists(String label) {
        Optional<Hashtag> optionalHashtag = hashtagRepository.findByLabel(label);
        return optionalHashtag.isPresent();
    }


}
