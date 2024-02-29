package com.groupfour.socialmedia.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.groupfour.socialmedia.services.ValidateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/validate")
public class ValidateController {
	
	private ValidateService validateService;
	
	@GetMapping("username/available/@{username}")
	public boolean valdateUsername(@PathVariable String username) {
		return validateService.validateUsername(username);
	}
	
	@GetMapping("username/exists/@{username}")
	public boolean validateUserNameExists(@PathVariable String username) {
		return validateService.validateUsernameExists(username);
	}
	
	

}
