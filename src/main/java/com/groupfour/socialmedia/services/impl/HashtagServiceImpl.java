package com.groupfour.socialmedia.services.impl;

import java.util.ArrayList;
import java.util.List;

import com.groupfour.socialmedia.dtos.HashtagResponseDto;
import com.groupfour.socialmedia.entities.Hashtag;
import com.groupfour.socialmedia.entities.Tweet;
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

	@Override
	public HashtagResponseDto createHashtag(String label, Tweet tweet) {
		Hashtag hashtag = new Hashtag();
		hashtag.setLabel(label);
		List<Tweet> taggedTweets = new ArrayList<>();
		taggedTweets.add(tweet);
		// SET firstUsed
		// SET lastUsed

		return hashtagMapper.hashtagEntityToDto(hashtagRepository.saveAndFlush(hashtag));
	}

}
