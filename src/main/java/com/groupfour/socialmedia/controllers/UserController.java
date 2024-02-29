package com.groupfour.socialmedia.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.groupfour.socialmedia.dtos.UserRequestDto;
import com.groupfour.socialmedia.dtos.UserResponseDto;
import com.groupfour.socialmedia.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	
	private final UserService userService;
	
//	@GetMapping("@{username}")
//	public UserResponseDto getUserByUsername(@PathVariable String username) {
//		return userService.getUserByUsername(username);
//	}
	
	
	@GetMapping("/@{username}/followers")
	public List<UserResponseDto> getFollowers(@PathVariable String username) {
		return userService.getFollowers(username);
	}

}
