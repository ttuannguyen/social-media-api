package com.groupfour.socialmedia.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.TweetRequestDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.entities.Credentials;
import com.groupfour.socialmedia.entities.Tweet;

import com.groupfour.socialmedia.entities.User;
import com.groupfour.socialmedia.exceptions.BadRequestException;
import com.groupfour.socialmedia.mappers.CredentialsMapper;
import com.groupfour.socialmedia.mappers.TweetMapper;
import com.groupfour.socialmedia.repositories.TweetRepository;
import com.groupfour.socialmedia.repositories.UserRepository;
import com.groupfour.socialmedia.services.TweetService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;
    private final CredentialsMapper credentialsMapper;
    private final UserRepository userRepository;


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
        }
        else {
            ogTweet = optionalTweet.get();
        }

        if (ogTweet.isDeleted()) {
            throw new BadRequestException("The tweet belonging to id : " + id + " has been deleted");
        }

        List<Tweet> allTweets = tweetRepository.findAll();
        List<Tweet> allReposts = new ArrayList<>();
        for (Tweet t : allTweets)
        {
            if (!t.isDeleted()) {
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
        }
        else {
            ogTweet = optionalTweet.get();
        }

        if (ogTweet.isDeleted()) {
            throw new BadRequestException("The tweet belonging to id : " + id + " has been deleted");
        }

        Credentials credentials = credentialsMapper.dtoToEntity(credentialsDto);

        List<User> allUsers = userRepository.findAll();
        User repostingUser = null;
        for (User u : allUsers)
        {
            if (u.getCredentials().equals(credentials))
            {
                repostingUser = u;
                break;
            }
        }

        if (repostingUser == null){
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



}

