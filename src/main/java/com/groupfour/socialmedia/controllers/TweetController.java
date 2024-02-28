package com.groupfour.socialmedia.controllers;


import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.TweetRequestDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;
import com.groupfour.socialmedia.services.TweetService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

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


}

