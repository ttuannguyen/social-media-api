package com.groupfour.socialmedia.services;

import java.util.List;

import com.groupfour.socialmedia.dtos.HashtagResponseDto;
import com.groupfour.socialmedia.dtos.TweetRequestDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;

public interface HashtagService {

	public List<HashtagResponseDto> getAllHashtags();

	public List<TweetResponseDto> getTweetsfromTag(String label);
		
	
}
