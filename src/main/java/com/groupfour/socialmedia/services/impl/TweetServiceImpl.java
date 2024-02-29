package com.groupfour.socialmedia.services.impl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.TweetRequestDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;
import com.groupfour.socialmedia.entities.Credentials;
import com.groupfour.socialmedia.entities.Tweet;

import com.groupfour.socialmedia.entities.User;
import com.groupfour.socialmedia.exceptions.BadRequestException;
import com.groupfour.socialmedia.mappers.CredentialsMapper;

import com.groupfour.socialmedia.mappers.TweetMapper;
import com.groupfour.socialmedia.mappers.UserMapper;
import com.groupfour.socialmedia.repositories.TweetRepository;
import com.groupfour.socialmedia.repositories.UserRepository;
import com.groupfour.socialmedia.services.TweetService;

import com.groupfour.socialmedia.services.UserService;
import com.groupfour.socialmedia.services.ValidateService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

	private final TweetRepository tweetRepository;
	private final TweetMapper tweetMapper;
	private final UserMapper userMapper;
	private final CredentialsMapper credentialsMapper;
	private final UserRepository userRepository;
	private final ValidateService validateService;
	private final UserService userService;

	private Tweet getTweetEntity(Long id) {

		Optional<Tweet> tweet = tweetRepository.findByIdAndDeletedFalse(id);
		if (tweet.isEmpty()) {
			throw new BadRequestException("No tweet found with id: " + id);
		}
		return tweet.get();
	}

	@Override
	public List<TweetResponseDto> getAllTweets() {
		return tweetMapper.entitiesToDtos(tweetRepository.findAllByDeletedFalseOrderByPostedDesc());

	}

	@Override
	public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {

		Tweet tweetToCreate = tweetMapper.requestDtoToEntity(tweetRequestDto);
		tweetRepository.saveAndFlush(tweetToCreate);

		return tweetMapper.entityToDto(tweetToCreate);
	}

	@Override
	public TweetResponseDto getTweetById(Long id) {

		return tweetMapper.entityToDto(getTweetEntity(id));
	}

	@Override
	public TweetResponseDto deleteTweet(Long id) {
		Tweet tweet = getTweetEntity(id);
		tweet.setDeleted(true);
		return tweetMapper.entityToDto(tweetRepository.saveAndFlush(tweet));
	}

	@Override
	public List<TweetResponseDto> getReposts(Long id) {

		Tweet ogTweet = getTweetEntity(id);
		return tweetMapper.entitiesToDtos(ogTweet.getReposts());
	}

	@Override
	public TweetResponseDto createRepost(CredentialsDto credentialsDto, Long id) {

		Tweet ogTweet = getTweetEntity(id);

		Credentials credentials = credentialsMapper.dtoToEntity(credentialsDto);
		String username = credentials.getUsername();

		if (!validateService.validateUsernameExists(username)) {
			throw new BadRequestException("No user exists with username: " + username);
		}

		User repostingUser = userRepository.findByCredentialsUsername(username).get();

		Tweet newRepost = new Tweet();
		newRepost.setAuthor(repostingUser);
		newRepost.setContent(ogTweet.getContent());
		newRepost.setDeleted(false);
		newRepost.setRepostOf(ogTweet);
		newRepost.setInReplyTo(ogTweet.getInReplyTo());
		List<User> mentionedUsers = new ArrayList<>();
		for (User u : ogTweet.getMentionedUsers()) { // Had to populate the list manually to avoid HibernationException
			mentionedUsers.add(u);
		}
		newRepost.setMentionedUsers(mentionedUsers);

		tweetRepository.saveAndFlush(newRepost); // Save the repost
		ogTweet.getReposts().add(newRepost); // Add repost to original tweet's reposts field
		tweetRepository.saveAndFlush(ogTweet); // Save addition to reposts field

		return tweetMapper.entityToDto(newRepost);
	}

	public List<TweetResponseDto> getReplies(Long id) {
		Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);
		if (optionalTweet.isEmpty()) {
			throw new BadRequestException("No tweet found with id: " + id);
		}

		Tweet tweet = optionalTweet.get();

		List<Tweet> replies = new ArrayList<>();
		for (Tweet t : tweet.getReplies()) {
			if (!t.isDeleted()) {
				replies.add(t);
			}
		}

		return tweetMapper.entitiesToDtos(replies);

	}

	public List<UserResponseDto> getMentionedUsers(@PathVariable Long id) {

		Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);
		if (optionalTweet.isEmpty()) {
			throw new BadRequestException("No tweet found with id: " + id);
		}

		Tweet tweet = optionalTweet.get();
		String content = tweet.getContent();

		List<String> foundUsernames = new ArrayList<>();
		String regex = "@[a-zA-Z0-9_]+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			foundUsernames.add(matcher.group().substring(1)); // The substring() call removes the @
		}

		List<User> foundActiveUsers = new ArrayList<>();
		for (String u : foundUsernames) {
			if (validateService.validateUsernameExists(u)) {
				foundActiveUsers.add(userRepository.findByCredentialsUsername(u).get());
			}
		}

		return userMapper.entitiesToDtos(foundActiveUsers);

	}

}
