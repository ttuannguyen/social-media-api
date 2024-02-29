package com.groupfour.socialmedia.controllers;

import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import org.springframework.web.bind.annotation.*;

import com.groupfour.socialmedia.services.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	
	private final UserService userService;

	@PostMapping("/@{username}/unfollow")
	public void unfollow(@RequestBody CredentialsDto credentialsDto, @PathVariable String username) {
		userService.unfollow(credentialsDto, username);
	}

	@GetMapping("/@{username}/mentions")
	public List<TweetResponseDto> getMentions(@PathVariable String username) {
		return userService.getMentions(username);
	}

}
