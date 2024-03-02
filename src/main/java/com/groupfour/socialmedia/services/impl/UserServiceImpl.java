package com.groupfour.socialmedia.services.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.groupfour.socialmedia.entities.Profile;
import org.springframework.stereotype.Service;

import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.ProfileDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.dtos.UserRequestDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;
import com.groupfour.socialmedia.entities.Credentials;
import com.groupfour.socialmedia.entities.Tweet;
import com.groupfour.socialmedia.entities.User;
import com.groupfour.socialmedia.exceptions.BadRequestException;
import com.groupfour.socialmedia.exceptions.NotAuthorizedException;
import com.groupfour.socialmedia.exceptions.NotFoundException;
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

	@Override
	public User getUserEntity(String username) {
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
		User unfollowUser = getUserEntity(username);
		if (!validateService.validateCredentialsExist(credUsername, credPassword)) {
			throw new BadRequestException("Provided credentials does not match any existing user");
		}
		

		User credUser = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(credUsername, credPassword).get();

		List<User> following = credUser.getFollowing();
		Boolean userFound = false;
		for (User u : following) {
			if (u.equals(unfollowUser)) {
				userFound = true;
				break;
			}
		}
		if (!userFound) {
			throw new BadRequestException("There is no following relationship between the two users");
		}

		following.remove(unfollowUser);
		credUser.setFollowing(following);

		List<User> followers = unfollowUser.getFollowers();
		followers.remove(credUser);

		userRepository.saveAndFlush(credUser);
		userRepository.saveAndFlush(unfollowUser);

	}

	public List<TweetResponseDto> getMentions(String username) {
		if(!validateService.validateUsernameExists(username)) {
			throw new BadRequestException("No user exists with username: " + username);

		}
		List<Tweet> mentionTweets = new ArrayList<>();
		for (Tweet t : tweetRepository.findAllByDeletedFalse()) {
			if (t.getContent() == null) {
				continue;
			}
			if (t.getContent().contains("@" + username)) {
				mentionTweets.add(t);
			}
		}
		return reverseChronological(tweetMapper.entitiesToDtos(mentionTweets));
	}

	private List<TweetResponseDto> reverseChronological(List<TweetResponseDto> tweets) {
		Collections.sort(tweets, Comparator.comparing(TweetResponseDto::getPosted).reversed());
		return tweets;
	}



	@Override
	public UserResponseDto createNewUser(UserRequestDto userRequestDto) {

		if (userRequestDto == null)
		{
			throw new BadRequestException("Empty request");
		}
		if (userRequestDto.getCredentials() == null)
		{
			throw new BadRequestException("No credentials were provided");
		}
		if (userRequestDto.getProfile() == null)
		{
			throw new BadRequestException("No profile was provided");
		}
		if (userRequestDto.getProfile().getEmail() == null)
		{
			throw new BadRequestException("No email was provided");
		}

		CredentialsDto credentialsDto = userRequestDto.getCredentials();

		if (credentialsDto.getUsername() == null)
		{
			throw new BadRequestException("No username was provided");
		}
		if (credentialsDto.getPassword() == null)
		{
			throw new BadRequestException("No password was provided");
		}

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
	
	@Override
	public List<UserResponseDto> getUserFollowers(String username) {
		User userFound = getUserEntity(username);
		List<User> followers = new ArrayList<>();
		for (User follower : userFound.getFollowers()) {
			if (!follower.getDeleted()) {
				followers.add(follower);
			}
		}
		
		return userMapper.entitiesToDtos(followers);
	}

	@Override
	public UserResponseDto updateUser(UserRequestDto userRequestDto, String username) {

		if (!validateService.validateUsernameExists(username)) {
			throw new BadRequestException("The provided user does not exist");
		}

		if (userRequestDto.getCredentials() == null) {
			throw new BadRequestException("No credentials provided");
		}

		if (userRequestDto.getProfile() == null) {
			throw new BadRequestException("No profile provided");
		}

		String credUsername = userRequestDto.getCredentials().getUsername();
		String credPassword = userRequestDto.getCredentials().getPassword();

		if (!validateService.validateCredentialsExist(credUsername, credPassword)) {
			throw new NotAuthorizedException("Invalid credentials provided");
		}

		if (!(credUsername.equals(username))) {
			throw new NotAuthorizedException("Provided credentials do not match the user");
		}

		User userFound = getUserEntity(credUsername);
		Profile newProfile = profileMapper.dtoToEntity(userRequestDto.getProfile());

		// If the email is null, just keep the old profile
		// Had to add this in order to pass some of the tests
		if (newProfile.getEmail() == null) {
			System.out.println("EMAIL IS NULL, KEEPING THE OLD PROFILE");
			newProfile = userFound.getProfile();
		}

		userFound.setProfile(newProfile);
		userRepository.saveAndFlush(userFound);

		return userMapper.entityToDto(userFound);
	          
	}

}
