package com.groupfour.socialmedia.services;


import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;

public interface UserService {

    UserResponseDto getUserByUsername(String username);

    void unfollow(CredentialsDto credentialsDto, String username);

}
