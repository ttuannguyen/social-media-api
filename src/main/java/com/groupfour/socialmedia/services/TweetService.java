package com.groupfour.socialmedia.services;

import com.groupfour.socialmedia.dtos.TweetRequestDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;

import java.util.List;

public interface TweetService {

    List<TweetResponseDto> getAllTweets();
    TweetResponseDto createTweet(TweetRequestDto tweetRequestDto);
	TweetResponseDto getTweetById(Long id);
	TweetResponseDto deleteTweet(Long id);

}
