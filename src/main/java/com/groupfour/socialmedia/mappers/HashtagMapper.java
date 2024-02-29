package com.groupfour.socialmedia.mappers;

import com.groupfour.socialmedia.dtos.UserResponseDto;
import com.groupfour.socialmedia.entities.User;
import org.mapstruct.Mapper;

import com.groupfour.socialmedia.dtos.HashtagResponseDto;
import com.groupfour.socialmedia.entities.Hashtag;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HashtagMapper {
	
	HashtagResponseDto hashtagEntityToDto(Hashtag entity);

	List<HashtagResponseDto> hashtagEntitiesToDtos (List<Hashtag> hashtags);

	List<Hashtag> dtosToHashtagEntities(List<HashtagResponseDto> hashtagResponseDtos);

}
