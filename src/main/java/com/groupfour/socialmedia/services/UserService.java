package com.groupfour.socialmedia.services;

import java.util.List;

import com.groupfour.socialmedia.dtos.UserRequestDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;

public interface UserService {

//	UserResponseDto getUserByUsername(String username);
	
	List<UserResponseDto> getUserFollowers(String username);


}
