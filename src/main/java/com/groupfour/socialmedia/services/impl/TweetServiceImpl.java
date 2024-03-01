package com.groupfour.socialmedia.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.HashtagResponseDto;
import com.groupfour.socialmedia.dtos.TweetRequestDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.entities.Credentials;
import com.groupfour.socialmedia.entities.Hashtag;
import com.groupfour.socialmedia.entities.Tweet;
import com.groupfour.socialmedia.entities.User;
import com.groupfour.socialmedia.exceptions.BadRequestException;
import com.groupfour.socialmedia.exceptions.NotFoundException;
import com.groupfour.socialmedia.mappers.CredentialsMapper;
import com.groupfour.socialmedia.mappers.HashtagMapper;
import com.groupfour.socialmedia.mappers.TweetMapper;
import com.groupfour.socialmedia.repositories.TweetRepository;
import com.groupfour.socialmedia.repositories.UserRepository;
import com.groupfour.socialmedia.services.TweetService;
import com.groupfour.socialmedia.services.ValidateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

	private final TweetRepository tweetRepository;
	private final TweetMapper tweetMapper;
	private final CredentialsMapper credentialsMapper;
	private final UserRepository userRepository;
	private final HashtagMapper hashtagMapper;
	private final ValidateService validateService;

	private Tweet getTweetEntity(Long id) {

		Optional<Tweet> tweet = tweetRepository.findByIdAndDeletedFalse(id);
		if (tweet.isEmpty()) {
			throw new BadRequestException("No tweet found with id: " + id);
		}
		return tweet.get();
	}

	@Override
	public List<TweetResponseDto> getAllTweets() {
		return tweetMapper.entitiesToDtos(tweetRepository.findAll());
	}

	@Override
	public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {

		Tweet tweetToCreate = tweetMapper.requestDtoToEntity(tweetRequestDto);
		tweetRepository.saveAndFlush(tweetToCreate);

		return tweetMapper.entityToDto(tweetToCreate);
	}

	@Override
	public List<TweetResponseDto> getReposts(Long id) {

		Optional<Tweet> optionalTweet = tweetRepository.findById(id);
		Tweet ogTweet = null;
		if (optionalTweet.isEmpty()) {
			throw new BadRequestException("No tweet found with id: " + id);
		} else {
			ogTweet = optionalTweet.get();
		}

		if (ogTweet.isDeleted()) {
			throw new BadRequestException("The tweet belonging to id : " + id + " has been deleted");
		}

		List<Tweet> allTweets = tweetRepository.findAll();
		List<Tweet> allReposts = new ArrayList<>();
		for (Tweet t : allTweets) {
			if (t.getRepostOf() == null) {
				continue;
			}
			if ((t.getRepostOf().getId() == id) && (!t.isDeleted())) {
				allReposts.add(t);
			}
		}
		return tweetMapper.entitiesToDtos(allReposts);
	}

	@Override
	public TweetResponseDto createRepost(CredentialsDto credentialsDto, Long id) {

		Optional<Tweet> optionalTweet = tweetRepository.findById(id);
		Tweet ogTweet = null;
		if (optionalTweet.isEmpty()) {
			throw new BadRequestException("No tweet found with id: " + id);
		} else {
			ogTweet = optionalTweet.get();
		}

		if (ogTweet.isDeleted()) {
			throw new BadRequestException("The tweet belonging to id : " + id + " has been deleted");
		}

		Credentials credentials = credentialsMapper.dtoToEntity(credentialsDto);

		List<User> allUsers = userRepository.findAll();
		User repostingUser = null;
		for (User u : allUsers) {
			if (u.getCredentials().equals(credentials)) {
				repostingUser = u;
				break;
			}
		}

		if (repostingUser == null) {
			throw new BadRequestException("No user was found matching the provided credentials");
		}

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
		return tweetMapper.entityToDto(tweetRepository.saveAndFlush(newRepost));
	}

	// MY CREATED ENDPOINTS

	@Override
	public List<HashtagResponseDto> getTagsOfTweet(Long id) {

		Optional<Tweet> optionalTweet = tweetRepository.findById(id);
		// If that tweet is deleted or otherwise doesn't exist, an error should be sent
		// in lieu of a response.
		if (optionalTweet.isEmpty()) {
			throw new NotFoundException("No tweet exists with this id:" + id);
		}
		Tweet tweetFound = optionalTweet.get();

		// IMPORTANT Remember that tags and mentions must be parsed by the server!
		return hashtagMapper.hashtagEntitiesToDtos(tweetFound.getHashtags());
	}

	@Override
	public void createLike(CredentialsDto credentialsDto, Long id) {

		// TODO: test this method
		Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);
		if (optionalTweet.isEmpty()) {
			throw new NotFoundException("No tweet exists with this id:" + id);
		}
		Tweet tweet = optionalTweet.get();

		Credentials credentials = credentialsMapper.dtoToEntity(credentialsDto);

		Optional<User> optionalUser = userRepository.findByCredentials(credentials);
		if (optionalTweet.isEmpty()) {
			throw new NotFoundException("No user exists with these credentials");
		}
		User user = optionalUser.get();

		tweet.getLikedByUsers().add(user);
		tweetRepository.saveAndFlush(tweet);

	}

	@Override
	public TweetResponseDto createReply(CredentialsDto credentialsDto, Long id) {

		Tweet originalTweet = getTweetEntity(id);
		
		
		Credentials credentials = credentialsMapper.dtoToEntity(credentialsDto);
		String username = credentials.getUsername();
		String password = credentials.getPassword();

		if (!validateService.validateCredentialsExist(username, password)) {
			throw new NotFoundException("No user exists with these credentials");
		}
		
		// retrieve the author of the reply
		Optional<User> optionalUser = userRepository.findByCredentials(credentials);
		User replyingUser = optionalUser.get();
		
		// create a new tweet for the reply
		Tweet replyTweet = new Tweet();
		replyTweet.setAuthor(replyingUser);		
		// to account for the inReplyTo property
		replyTweet.setInReplyTo(originalTweet);
		replyTweet.setContent("Content of the reply goes here...");
		
		// process content for mentions and hashtags
	    List<User> mentionedUsers = scanMentionedUsers(replyTweet.getContent());
	    replyTweet.setMentionedUsers(mentionedUsers);
	    List<Hashtag> hashtags = scanHashtags(replyTweet.getContent());
	    replyTweet.setHashtags(hashtags);
	    
		
	    // Save the reply tweet to the database
	    Tweet savedReplyTweet = tweetRepository.saveAndFlush(replyTweet);
		
	    // Return the DTO of the saved reply tweet
	    return tweetMapper.entityToDto(savedReplyTweet);
	

	}
	
	public List<User> scanMentionedUsers(String content) {

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

        return foundActiveUsers;

    }

    public List<String> scanHashtags(String content) {

        List<String> foundHashtags = new ArrayList<>();
        String regex = "#[a-zA-Z0-9_]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            foundHashtags.add(matcher.group());
        }

        return foundHashtags;

	}

}
