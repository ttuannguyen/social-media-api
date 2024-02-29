package com.groupfour.socialmedia.services;


import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto getUserByUsername(String username);

    void unfollow(CredentialsDto credentialsDto, String username);

    List<TweetResponseDto> getMentions(String username);

}
