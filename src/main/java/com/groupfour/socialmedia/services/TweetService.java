package com.groupfour.socialmedia.services;


import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.HashtagResponseDto;
import com.groupfour.socialmedia.dtos.TweetRequestDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;

import java.util.List;

public interface TweetService {

    List<TweetResponseDto> getAllTweets();
    TweetResponseDto createTweet(TweetRequestDto tweetRequestDto);

    List<TweetResponseDto> getReposts(Long id);

    TweetResponseDto createRepost(CredentialsDto credentialsDto, Long id);
    
    List<HashtagResponseDto> getTagsOfTweet(Long id);
    
	void createLike(CredentialsDto credentialsDto, Long id);


}
