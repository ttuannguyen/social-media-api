package com.groupfour.socialmedia.services.impl;

import com.groupfour.socialmedia.entities.User;
import com.groupfour.socialmedia.exceptions.BadRequestException;
import com.groupfour.socialmedia.services.ValidateService;
import org.springframework.stereotype.Service;

import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.ProfileDto;
import com.groupfour.socialmedia.dtos.UserRequestDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;
import com.groupfour.socialmedia.mappers.CredentialsMapper;
import com.groupfour.socialmedia.mappers.ProfileMapper;
import com.groupfour.socialmedia.mappers.UserMapper;
import com.groupfour.socialmedia.repositories.UserRepository;
import com.groupfour.socialmedia.services.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final ProfileMapper profileMapper;
	private final CredentialsMapper credentialsMapper;
	private final ValidateService validateService;

	private User getUserEntity(String username) {
		if(!validateService.validateUsernameExists(username)) {
			throw new BadRequestException("No user exists with username: " + username);

		}
		Optional<User> user = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
		return user.get();
	}

	@Override
	public UserResponseDto getUserByUsername(String username) {
		Optional<User> user = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
		if (user.isEmpty()) {
			throw new BadRequestException("No user found with username: " + username);
		}
		return userMapper.entityToDto(user.get());
	}

	@Override
	public void unfollow(CredentialsDto credentialsDto, String username) {

	}
	
	

}
