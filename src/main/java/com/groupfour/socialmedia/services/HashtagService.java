package com.groupfour.socialmedia.services;

import java.util.List;

import com.groupfour.socialmedia.dtos.HashtagResponseDto;

public interface HashtagService {

	List<HashtagResponseDto> getAllHashtags();
	
}
