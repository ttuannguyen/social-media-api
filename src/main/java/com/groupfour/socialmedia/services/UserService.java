package com.groupfour.socialmedia.services;

import java.util.List;

import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.dtos.UserRequestDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;

public interface UserService {

	UserResponseDto getUserByUsername(String username);

	UserResponseDto createNewUser(UserRequestDto userRequestDto);

	List<UserResponseDto> getFollowing(String username);

	List<TweetResponseDto> getFeed(String username);

	List<TweetResponseDto> getUserTweets(String username);

	List<UserResponseDto> getAllUsers();

}
