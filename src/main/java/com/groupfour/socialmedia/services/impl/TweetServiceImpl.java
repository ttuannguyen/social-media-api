package com.groupfour.socialmedia.services.impl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.TweetRequestDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;
import com.groupfour.socialmedia.entities.Credentials;
import com.groupfour.socialmedia.entities.Hashtag;
import com.groupfour.socialmedia.entities.Tweet;

import com.groupfour.socialmedia.entities.User;
import com.groupfour.socialmedia.exceptions.BadRequestException;
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
    private final HashtagMapper hashtagMapper;
    private final HashtagService hashtagService;
    private final HashtagRepository hashtagRepository;


    private Tweet getTweetEntity(Long id) {
        Optional<Tweet> tweet = tweetRepository.findByIdAndDeletedFalse(id);
        if (tweet.isEmpty()) {
            throw new BadRequestException("No tweet found with id: " + id);
        }
        return tweet.get();
    }

    @Override
    public List<TweetResponseDto> getAllTweets() {
        List<TweetResponseDto> allTweets = tweetMapper.entitiesToDtos(tweetRepository.findAllByDeletedFalse());
        Collections.sort(allTweets, Comparator.comparing(TweetResponseDto::getPosted).reversed());
        return allTweets;
    }

    @Override
    public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {

        Tweet tweetToCreate = tweetMapper.requestDtoToEntity(tweetRequestDto);
        List<User> mentionedUsers = userMapper.dtosToEntities(getMentionedUsers(tweetToCreate.getId()));
        List<String> hashtagStrings = getHashtags(tweetToCreate.getId());
        List<Hashtag> hashtagList = new ArrayList<>();

        // Determine which hashtags are new and which are not
        List<String> newHashtags = new ArrayList<>();
        List<String> existingHashtags = new ArrayList<>();
        for (String h : hashtagStrings) {
            if(!validateService.validateHashtagExists(h)) {
                newHashtags.add(h);
            }
            else {
                existingHashtags.add(h);
            }
        }

        // Create + Save all nonexistent hashtags
        for (String h : newHashtags) {
            hashtagService.createHashtag(h, tweetToCreate); // This includes a saveAndFlush()
        }

        // Update + Save all existing hashtags
        for (String h : existingHashtags) {
            Hashtag hashtag = hashtagRepository.findByLabel(h).get();
            List<Tweet> taggedTweets = hashtag.getTaggedTweets();
            taggedTweets.add(tweetToCreate);
            // UPDATE lastUsed
            hashtagRepository.saveAndFlush(hashtag);
        }

        // Set new tweet's hashtag list
        for (String s : hashtagStrings) {
            Hashtag hashtag = hashtagRepository.findByLabel(s).get();
            hashtagList.add(hashtag);
        }
        tweetToCreate.setHashtags(hashtagList);

        // Set new tweet's mentionedUsers list
        tweetToCreate.setMentionedUsers(mentionedUsers);

        tweetRepository.saveAndFlush(tweetToCreate);
        return tweetMapper.entityToDto(tweetToCreate);
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

        if (!validateService.validateUsernameExists(username))
        {
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
        ogTweet.getReposts().add(newRepost);     // Add repost to original tweet's reposts field
        tweetRepository.saveAndFlush(ogTweet);   // Save addition to reposts field

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
            if(validateService.validateUsernameExists(u)) {
                foundActiveUsers.add(userRepository.findByCredentialsUsername(u).get());
            }
        }

        return userMapper.entitiesToDtos(foundActiveUsers);

    }

    public List<String> getHashtags(Long id) {

        Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);
        if (optionalTweet.isEmpty()) {
            throw new BadRequestException("No tweet found with id: " + id);
        }

        Tweet tweet = optionalTweet.get();
        String content = tweet.getContent();

        List<String> foundHashtags = new ArrayList<>();
        String regex = "#[a-zA-Z0-9_]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            foundHashtags.add(matcher.group());
        }

//        List<String> newHashtags = new ArrayList<>();
//        for (String h : foundHashtags) {
//            if(!validateService.validateHashtagExists(h)) {
//                newHashtags.add(h);
//            }
//        }

        return foundHashtags;

    }



}

