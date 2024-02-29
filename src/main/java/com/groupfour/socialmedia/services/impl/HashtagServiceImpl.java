package com.groupfour.socialmedia.services.impl;

import java.util.List;

import com.groupfour.socialmedia.dtos.HashtagResponseDto;
import com.groupfour.socialmedia.mappers.HashtagMapper;
import com.groupfour.socialmedia.repositories.HashtagRepository;
import com.groupfour.socialmedia.services.HashtagService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

	private final HashtagRepository hashtagRepository;
	private final HashtagMapper hashtagMapper;
	
	@Override
	public List<HashtagResponseDto> getAllHashtags() {
	
		return hashtagMapper.hashtagEntitiesToDtos(hashtagRepository.findAll());
		
	}

}
