package com.groupfour.socialmedia.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.groupfour.socialmedia.dtos.HashtagResponseDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.services.HashtagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("tags")
public class HashtagController {


    private final HashtagService hashTagService;
    @GetMapping
    public List<HashtagResponseDto> getAllHashtags() {
        return hashTagService.getAllHashtags();
    }

	@GetMapping("/{label}")
	public List<TweetResponseDto> getTweetsfromTag(@PathVariable String label) {
		return hashTagService.getTweetsfromTag(label);
	}


	
	
}
