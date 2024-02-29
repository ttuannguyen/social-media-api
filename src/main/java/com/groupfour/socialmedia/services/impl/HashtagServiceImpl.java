package com.groupfour.socialmedia.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.groupfour.socialmedia.dtos.HashtagResponseDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.entities.Hashtag;
import com.groupfour.socialmedia.entities.Tweet;
import com.groupfour.socialmedia.exceptions.BadRequestException;
import com.groupfour.socialmedia.mappers.HashtagMapper;
import com.groupfour.socialmedia.mappers.TweetMapper;
import com.groupfour.socialmedia.repositories.HashtagRepository;
import com.groupfour.socialmedia.repositories.TweetRepository;
import com.groupfour.socialmedia.services.HashtagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

	private final HashtagRepository hashtagRepository;
	private final TweetRepository tweetRepository;
	private final HashtagMapper hashtagMapper;
	private final TweetMapper tweetMapper;
	
	@Override
	public List<HashtagResponseDto> getAllHashtags() {
	
		return hashtagMapper.hashtagEntitiesToDtos(hashtagRepository.findAll());
		
	}

	@Override
	public  List<TweetResponseDto> getTweetsfromTag(String label) {
		
		
		Optional<Hashtag> optionalHashtag = hashtagRepository.findByLabel(label);
		if (optionalHashtag.isEmpty()) {
			
			throw new BadRequestException("No hashtag found with label: " + label);
		}
		Hashtag hashtagFound = optionalHashtag.get();
		
		
		// JPA derived query approach		
		// TODO: The tweets should appear in reverse-chronological order. 
//		List<Tweet> nonDeletedTweets = tweetRepository.findAllByDeletedFalse();
		
		List<Tweet> returnedTweets = new ArrayList<>();
		
		for (Tweet t : hashtagFound.getTaggedTweets()) {
			if (!t.isDeleted() && t.getContent().contains("#" +label)) {
				returnedTweets.add(t);
			}
		}
		
		Collections.sort(returnedTweets, Comparator.comparing(Tweet::getPosted).reversed());
		
		
        return tweetMapper.entitiesToDtos(returnedTweets);
		

	}

}
