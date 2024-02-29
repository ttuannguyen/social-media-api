package com.groupfour.socialmedia.controllers;

import com.groupfour.socialmedia.dtos.CredentialsDto;
import org.springframework.web.bind.annotation.*;

import com.groupfour.socialmedia.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	
	private final UserService userService;

	@PostMapping("/@{username}/unfollow")
	public void unfollow(@RequestBody CredentialsDto credentialsDto, @PathVariable String username) {
		userService.unfollow(credentialsDto, username);
	}

}
