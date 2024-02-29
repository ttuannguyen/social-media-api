package com.groupfour.socialmedia.services;

import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.TweetRequestDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface TweetService {

    List<TweetResponseDto> getAllTweets();
    TweetResponseDto createTweet(CredentialsDto credentialsDto, String content);

    List<TweetResponseDto> getReposts(Long id);

    List<TweetResponseDto> getReplies(Long id);

    List<UserResponseDto> getMentionedUsers(Long id);

    TweetResponseDto createRepost(CredentialsDto credentialsDto, Long id);


}
