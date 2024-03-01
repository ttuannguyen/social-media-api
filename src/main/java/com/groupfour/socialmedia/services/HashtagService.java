package com.groupfour.socialmedia.services;

import java.util.List;

import com.groupfour.socialmedia.dtos.HashtagResponseDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.entities.Tweet;

public interface HashtagService {

	public List<HashtagResponseDto> getAllHashtags();
	
	HashtagResponseDto createHashtag(String label, Tweet tweet);
	
	public List<TweetResponseDto> getTweetsfromTag(String label);
		
	
}
