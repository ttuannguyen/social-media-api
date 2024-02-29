package com.groupfour.socialmedia.services.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.dtos.UserRequestDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;
import com.groupfour.socialmedia.entities.Credentials;
import com.groupfour.socialmedia.entities.Tweet;
import com.groupfour.socialmedia.entities.User;
import com.groupfour.socialmedia.exceptions.BadRequestException;
import com.groupfour.socialmedia.exceptions.NotAuthorizedException;
import com.groupfour.socialmedia.mappers.CredentialsMapper;
import com.groupfour.socialmedia.mappers.ProfileMapper;
import com.groupfour.socialmedia.mappers.TweetMapper;
import com.groupfour.socialmedia.mappers.UserMapper;
import com.groupfour.socialmedia.repositories.TweetRepository;
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



	private final TweetRepository tweetRepository;
	private final TweetMapper tweetMapper;

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

		Credentials receivedCreds = credentialsMapper.dtoToEntity(credentialsDto);
		String credUsername = receivedCreds.getUsername();
		String credPassword = receivedCreds.getPassword();
		if (!validateService.validateCredentialsExist(credUsername, credPassword)) {
			throw new BadRequestException("Provided credentials does not match any existing user");
		}
		if (!validateService.validateUsernameExists(username)) {
			throw new BadRequestException("Provided username does not match any existing user");
		}

		User credUser = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(credUsername, credPassword).get();
		User unfollowUser = userRepository.findByCredentialsUsernameAndDeletedFalse(username).get();

		List<User> followers = credUser.getFollowers();
		Boolean userFound = false;
		for (User u : followers) {
			if (u.equals(unfollowUser)) {
				userFound = true;
				break;
			}
		}
		if (!userFound) {
			throw new BadRequestException("There is no following relationship between the two users");
		}

		followers.remove(unfollowUser);
		credUser.setFollowers(followers);

		List<User> following = unfollowUser.getFollowing();
		following.remove(credUser);

		userRepository.saveAndFlush(credUser);
		userRepository.saveAndFlush(unfollowUser);

	}

	public List<TweetResponseDto> getMentions(String username) {
		List<Tweet> mentionTweets = new ArrayList<>();
		for (Tweet t : tweetRepository.findAllByDeletedFalse()) {
			if (t.getContent() == null) {
				continue;
			}
			if (t.getContent().contains("@" + username)) {
				mentionTweets.add(t);
			}
		}
		return tweetMapper.entitiesToDtos(mentionTweets);
	}

	

	
	private List<TweetResponseDto> reverseChronological(List<TweetResponseDto> tweets) {
		Collections.sort(tweets, Comparator.comparing(TweetResponseDto::getPosted).reversed());
		return tweets;
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

	@Override
	public UserResponseDto deleteUser(String username, CredentialsDto credentials) {
		User userToDelete = getUserEntity(username);
		Credentials userCredentials = userToDelete.getCredentials();
		if (!userCredentials.getPassword().equals(credentials.getPassword()) || !username.equals(credentials.getUsername())) {
			throw new NotAuthorizedException("You do not have authorization to delete this user.");
		}
		userToDelete.setDeleted(true);
		return userMapper.entityToDto(userRepository.saveAndFlush(userToDelete));
	}

	@Override
	public void addFollow(String username, CredentialsDto credentialsDto) {
		User user = getUserEntity(credentialsDto.getUsername());
		Credentials userCredentials = user.getCredentials();
		if (!userCredentials.getPassword().equals(credentialsDto.getPassword())) {
			throw new NotAuthorizedException("Invalid password");
		}
		User userToFollow = getUserEntity(username);
		List<User> userToFollowFollowers = userToFollow.getFollowers();
		if (userToFollowFollowers.contains(user)) {
			throw new BadRequestException("You are already following that user.");
		}
		userToFollowFollowers.add(user);
		userToFollow.setFollowers(userToFollowFollowers);
		List<User> followedByUser = user.getFollowing();
		followedByUser.add(userToFollow);
		user.setFollowing(followedByUser);
		userRepository.saveAllAndFlush(Arrays.asList(user, userToFollow));
	}


}
