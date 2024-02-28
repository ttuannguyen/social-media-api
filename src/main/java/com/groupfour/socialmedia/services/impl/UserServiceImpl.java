package com.groupfour.socialmedia.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.groupfour.socialmedia.exceptions.BadRequestException;
import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.ProfileDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.dtos.UserRequestDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;
import com.groupfour.socialmedia.entities.Credentials;
import com.groupfour.socialmedia.entities.Tweet;
import com.groupfour.socialmedia.entities.User;
import com.groupfour.socialmedia.mappers.CredentialsMapper;
import com.groupfour.socialmedia.mappers.ProfileMapper;
import com.groupfour.socialmedia.mappers.TweetMapper;
import com.groupfour.socialmedia.mappers.UserMapper;
import com.groupfour.socialmedia.repositories.UserRepository;
import com.groupfour.socialmedia.services.TweetService;
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
	private final TweetMapper tweetMapper;
	
	private User getUserEntity(String username) {
		if(!validateService.validateUsernameExists(username)) {
			throw new BadRequestException("No user exists with username: " + username);
			
		}
		Optional <User> user = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
		return user.get();
	}
	
	private List<TweetResponseDto> reverseChronological(List<TweetResponseDto> tweets) {
		Collections.sort(tweets, Comparator.comparing(TweetResponseDto::getPosted).reversed());
		return tweets;
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
	public UserResponseDto createNewUser(UserRequestDto userRequestDto) {
	    CredentialsDto credentialsDto = userRequestDto.getCredentials();
	    String username = credentialsDto.getUsername();

	    if (!validateService.validateUsername(username)) {
	        Optional<User> existingUserOptional = userRepository.findByCredentialsUsername(username);
	        
	        if (existingUserOptional.isPresent()) {
	            User existingUser = existingUserOptional.get();
	            
	            if (!existingUser.getDeleted()) {
	                throw new BadRequestException("Username is taken");
	            } else {
	                existingUser.setDeleted(false);
	                return userMapper.entityToDto(userRepository.saveAndFlush(existingUser));
	            }
	        }
	    }
	    
	    User newUser = userMapper.requestDtoToEntity(userRequestDto);
	    return userMapper.entityToDto(userRepository.saveAndFlush(newUser));
	}

	@Override
	public List<UserResponseDto> getFollowing(String username) {
		User user = getUserEntity(username);
		return userMapper.entitiesToDtos(user.getFollowing());
		
		
	}

	@Override
	public List<TweetResponseDto> getFeed(String username) {
		User user = getUserEntity(username);
		List<TweetResponseDto> feed = new ArrayList<>();
		feed.addAll(getUserTweets(username));
		for (User followedUser : user.getFollowing()) {
			Credentials credentials = followedUser.getCredentials();
			String followedUsername = credentials.getUsername();
			feed.addAll(getUserTweets(followedUsername));
		}
		
		
		return reverseChronological(feed);
	}

	@Override
	public List<TweetResponseDto> getUserTweets(String username) {
		User user = getUserEntity(username);
		
		return reverseChronological(tweetMapper.entitiesToDtos(user.getTweets()));
	}

	@Override
	public List<UserResponseDto> getAllUsers() {
		return userMapper.entitiesToDtos(userRepository.findAllByDeletedFalse());
	}


}
