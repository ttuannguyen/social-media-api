package com.groupfour.socialmedia.services.impl;

import com.groupfour.socialmedia.dtos.*;
import com.groupfour.socialmedia.entities.Credentials;
import com.groupfour.socialmedia.entities.Tweet;
import com.groupfour.socialmedia.entities.User;
import com.groupfour.socialmedia.exceptions.BadRequestException;
import com.groupfour.socialmedia.mappers.TweetMapper;
import com.groupfour.socialmedia.repositories.TweetRepository;
import com.groupfour.socialmedia.services.ValidateService;
import org.springframework.stereotype.Service;

import com.groupfour.socialmedia.mappers.CredentialsMapper;
import com.groupfour.socialmedia.mappers.ProfileMapper;
import com.groupfour.socialmedia.mappers.UserMapper;
import com.groupfour.socialmedia.repositories.UserRepository;
import com.groupfour.socialmedia.services.UserService;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	
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
	
	

}
