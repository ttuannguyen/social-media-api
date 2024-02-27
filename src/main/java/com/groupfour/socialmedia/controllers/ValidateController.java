package com.groupfour.socialmedia.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.groupfour.socialmedia.services.ValidateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("validate")
public class ValidateController {
	
	private ValidateService validateService;
	

}