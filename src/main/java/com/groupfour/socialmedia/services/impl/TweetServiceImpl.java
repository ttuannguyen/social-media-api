package com.groupfour.socialmedia.services.impl;

import java.sql.Timestamp;
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
import com.groupfour.socialmedia.dtos.UserResponseDto;
import com.groupfour.socialmedia.entities.Credentials;
import com.groupfour.socialmedia.entities.Hashtag;
import com.groupfour.socialmedia.entities.Tweet;
import com.groupfour.socialmedia.entities.User;
import com.groupfour.socialmedia.exceptions.BadRequestException;
import com.groupfour.socialmedia.exceptions.NotFoundException;
import com.groupfour.socialmedia.mappers.CredentialsMapper;
import com.groupfour.socialmedia.mappers.HashtagMapper;
import com.groupfour.socialmedia.mappers.TweetMapper;
import com.groupfour.socialmedia.mappers.UserMapper;
import com.groupfour.socialmedia.repositories.HashtagRepository;
import com.groupfour.socialmedia.repositories.TweetRepository;
import com.groupfour.socialmedia.repositories.UserRepository;
import com.groupfour.socialmedia.services.HashtagService;
import com.groupfour.socialmedia.services.TweetService;
import com.groupfour.socialmedia.services.UserService;
import com.groupfour.socialmedia.services.ValidateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {


    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;
    private final TweetMapper tweetMapper;
    private final UserMapper userMapper;
    private final HashtagMapper hashtagMapper;
    private final CredentialsMapper credentialsMapper;
    private final ValidateService validateService;
    private final UserService userService;
    private final HashtagService hashtagService;
    

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
 
        Credentials creds = credentialsMapper.dtoToEntity(tweetRequestDto.getCredentials());
		if (creds == null) {
			throw new BadRequestException("Credentials are required");
		}
        String content = tweetRequestDto.getContent();
		if (content == null) {
			throw new BadRequestException("Content is required");
		}
        String username = creds.getUsername();
        String password = creds.getPassword();
        User author = userService.getUserEntity(username);
        if (!validateService.validateCredentialsExist(username, password))
        {
            throw new BadRequestException("Invalid credentials received");
        }
        Tweet tweetToCreate = tweetRepository.saveAndFlush(tweetMapper.requestDtoToEntity(tweetRequestDto));
        
        tweetToCreate.setAuthor(author);
        tweetToCreate.setContent(content);
        tweetRepository.save(tweetToCreate); // Saving the tweet here before saving users and hashtags that reference it

        List<User> mentionedUsers = scanMentionedUsers(content);
        List<String> hashtagStrings = scanHashtags(content);

        // Determine which hashtags are new and which are not
        List<String> newHashtags = new ArrayList<>();
        List<String> existingHashtags = new ArrayList<>();
        for (String h : hashtagStrings) {
			Optional<Hashtag> optionalHashtag = hashtagRepository.findByLabel(h);
            if(optionalHashtag.isEmpty()) {
                newHashtags.add(h);
            }
            else {
                existingHashtags.add(h);
            }
        }

        // Create + Save all new hashtags
        for (String h : newHashtags) {
            hashtagService.createHashtag(h, tweetToCreate); // This includes a saveAndFlush()
        }

        // Update + Save all existing hashtags
        for (String h : existingHashtags) {
            Hashtag hashtag = hashtagRepository.findByLabel(h).get();

            List<Tweet> taggedTweets = hashtag.getTaggedTweets();
            taggedTweets.add(tweetToCreate);
            hashtag.setTaggedTweets(taggedTweets);
            hashtag.setLastUsed(new Timestamp(System.currentTimeMillis()));

            hashtagRepository.saveAndFlush(hashtag);
        }

        // Update all mentionedUsers' mentioned list
        for (User u : tweetToCreate.getMentionedUsers()) {
            List<Tweet> mentions = u.getMentions();
            mentions.add(tweetToCreate);
            u.setMentions(mentions);
        }

        // Set new tweet's hashtag list
        List<Hashtag> hashtagList = new ArrayList<>();
        for (String s : hashtagStrings) {
            Hashtag hashtag = hashtagRepository.findByLabel(s).get();
            hashtagList.add(hashtag);
        }
        tweetToCreate.setHashtags(hashtagList);

        // Set new tweet's mentionedUsers list
        tweetToCreate.setMentionedUsers(mentionedUsers);

        // SET TIMESTAMP posted

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
        String password = credentials.getPassword();

        if (!validateService.validateCredentialsExist(username, password))
        {
            throw new BadRequestException("Invalid credentials received");
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

    public List<UserResponseDto> getMentionedUsers(Long id) {

		Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);
		if (optionalTweet.isEmpty()) {
			throw new BadRequestException("No tweet found with id: " + id);
		}

        Tweet tweet = optionalTweet.get();
        return userMapper.entitiesToDtos(tweet.getMentionedUsers());

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
		
		// TODO: process content for mentions and hashtags
	    List<User> mentionedUsers = scanMentionedUsers(replyTweet.getContent());
	    replyTweet.setMentionedUsers(mentionedUsers);
//
//	    List<Hashtag> hashtags = scanHashtags(replyTweet.getContent());
//	    replyTweet.setHashtags(hashtags);
		
	    // Save the reply tweet to the database
	    Tweet savedReplyTweet = tweetRepository.saveAndFlush(replyTweet);
		
	    // Return the DTO of the saved reply tweet
	    return tweetMapper.entityToDto(savedReplyTweet);
	    
	

	}

}
