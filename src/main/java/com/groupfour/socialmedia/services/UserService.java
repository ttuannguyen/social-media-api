package com.groupfour.socialmedia.services;

import com.groupfour.socialmedia.dtos.UserRequestDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;

public interface UserService {

	UserResponseDto getUserByUsername(String username);

	UserResponseDto createNewUser(UserRequestDto userRequestDto);

}
