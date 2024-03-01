package com.groupfour.socialmedia.controllers;


import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.HashtagResponseDto;
import com.groupfour.socialmedia.dtos.TweetRequestDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;
import com.groupfour.socialmedia.services.TweetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("tweets")
public class TweetController {

    private final TweetService tweetService;

    @GetMapping
    public List<TweetResponseDto> getAllTweets() {
        return tweetService.getAllTweets();
    }

    @GetMapping("/{id}/reposts")
    public List<TweetResponseDto> getReposts(@PathVariable Long id) {
        return tweetService.getReposts(id);
    }

    @GetMapping("/{id}/replies")
    public List<TweetResponseDto> getReplies(@PathVariable Long id) {
        return tweetService.getReplies(id);
    }

    @GetMapping("/{id}/mentions")
    public List<UserResponseDto> getMentionedUsers(@PathVariable Long id) {
        return tweetService.getMentionedUsers(id);
    }

    @PostMapping
    public TweetResponseDto createTweet(@RequestBody TweetRequestDto tweetRequestDto) {
        return tweetService.createTweet(tweetRequestDto);
    }
    
    @GetMapping("{id}")
    public TweetResponseDto getTweetById(@PathVariable Long id) {
    	return tweetService.getTweetById(id);
    }
    
    @DeleteMapping("{id}")
    public TweetResponseDto deleteTweet(@PathVariable Long id) {
    	return tweetService.deleteTweet(id);
    }

    @PostMapping("/{id}/repost")
    public TweetResponseDto createRepost(@RequestBody CredentialsDto credentialsDto, @PathVariable Long id) {
        return tweetService.createRepost(credentialsDto, id);
    }
    
    
    // MY CREATED ENDPOINTS
    @GetMapping("/{id}/tags")
    public List<HashtagResponseDto> getTagsOfTweet(@PathVariable Long id) {
    	return tweetService.getTagsOfTweet(id);
    }
    
    @PostMapping("/{id}/like")
    public void createLike(@RequestBody CredentialsDto credentialsDto, @PathVariable Long id) {
    	tweetService.createLike(credentialsDto, id);
    }
    
    @PostMapping("/{id}/reply")
    public TweetResponseDto createReply(@RequestBody CredentialsDto credentialsDto, @PathVariable Long id) {
    	return tweetService.createReply(credentialsDto, id);
    }
    
    
    


}

