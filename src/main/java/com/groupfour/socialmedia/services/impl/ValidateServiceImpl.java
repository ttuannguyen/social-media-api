package com.groupfour.socialmedia.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.groupfour.socialmedia.entities.User;
import com.groupfour.socialmedia.repositories.UserRepository;
import com.groupfour.socialmedia.services.ValidateService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

	private final UserRepository userRepository;

	@Override
	public boolean validateUsername(String username) {
		Optional<User> user = userRepository.findByCredentialsUsername(username);
		return user.isEmpty();
	}

}
