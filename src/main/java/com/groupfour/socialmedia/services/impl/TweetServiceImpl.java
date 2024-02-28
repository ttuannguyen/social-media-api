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

        Tweet ogTweet = getTweetEntity(id);
        return tweetMapper.entitiesToDtos(ogTweet.getReposts());
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

        tweetRepository.saveAndFlush(newRepost); // Save the repost
        ogTweet.getReposts().add(newRepost);     // Add repost to original tweet's reposts field
        tweetRepository.saveAndFlush(ogTweet);   // Save addition to reposts field

        return tweetMapper.entityToDto(newRepost);
    }



}

