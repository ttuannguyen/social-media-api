package com.groupfour.socialmedia.mappers;

import org.mapstruct.Mapper;

import com.groupfour.socialmedia.dtos.HashtagResponseDto;
import com.groupfour.socialmedia.entities.Hashtag;

@Mapper(componentModel = "spring")
public interface HashtagMapper {
	
	HashtagResponseDto hashtagEntityToDto(Hashtag entity);

}
