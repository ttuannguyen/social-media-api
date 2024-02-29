package com.groupfour.socialmedia.services.impl;

import com.groupfour.socialmedia.entities.Hashtag;
import com.groupfour.socialmedia.repositories.HashtagRepository;
import com.groupfour.socialmedia.services.ValidateService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {
	
	HashtagRepository hashtagRepository;

	@Override	
    public boolean validateTagExists(String label) {
        // Check if a hashtag with the given label exists in the database
        Hashtag hashtag = hashtagRepository.findByLabel(label);
        return hashtag != null;
    }
	
//	public boolean validateTagExists(String label) {
//		
//		for (Hashtag h : hashtagRepository.findAll()) {
//			if (h.getLabel().equals(label)) {
//				return true;
//			}
//		}
//		return false;
//	}
	
}
