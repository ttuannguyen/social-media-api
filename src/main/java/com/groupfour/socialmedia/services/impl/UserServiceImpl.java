package com.groupfour.socialmedia.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.groupfour.socialmedia.exceptions.BadRequestException;
import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.ProfileDto;
import com.groupfour.socialmedia.dtos.UserRequestDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;
import com.groupfour.socialmedia.entities.User;
import com.groupfour.socialmedia.mappers.CredentialsMapper;
import com.groupfour.socialmedia.mappers.ProfileMapper;
import com.groupfour.socialmedia.mappers.UserMapper;
import com.groupfour.socialmedia.repositories.UserRepository;
import com.groupfour.socialmedia.services.UserService;
import com.groupfour.socialmedia.services.ValidateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final ProfileMapper profileMapper;
	private final CredentialsMapper credentialsMapper;
	private final ValidateService validateService;

	@Override
	public UserResponseDto getUserByUsername(String username) {
		Optional<User> user = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
		if (user.isEmpty()) {
			throw new BadRequestException("No user found with username: " + username);
		}
		return userMapper.entityToDto(user.get());
	}

	@Override
	public UserResponseDto createNewUser(UserRequestDto userRequestDto) {
	    CredentialsDto credentialsDto = userRequestDto.getCredentials();
	    String username = credentialsDto.getUsername();

	    // Check if username already exists
	    if (!validateService.validateUsername(username)) {
	        // Username exists, retrieve user by username
	        Optional<User> existingUserOptional = userRepository.findByCredentialsUsername(username);
	        
	        if (existingUserOptional.isPresent()) {
	            User existingUser = existingUserOptional.get();
	            
	            if (!existingUser.getDeleted()) {
	                // If user exists and is not deleted, throw BadRequestException
	                throw new BadRequestException("Username is taken");
	            } else {
	                // If user exists and is deleted, set deleted back to false
	                existingUser.setDeleted(false);
	                return userMapper.entityToDto(userRepository.saveAndFlush(existingUser));
	            }
	        }
	    }
	    
	    // Create a new user if username doesn't exist or is available
	    User newUser = userMapper.requestDtoToEntity(userRequestDto);
	    return userMapper.entityToDto(userRepository.saveAndFlush(newUser));
	}


}
