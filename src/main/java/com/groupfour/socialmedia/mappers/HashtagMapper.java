package com.groupfour.socialmedia.mappers;


import java.util.List;

import org.mapstruct.Mapper;

import com.groupfour.socialmedia.dtos.HashtagResponseDto;
import com.groupfour.socialmedia.entities.Hashtag;

@Mapper(componentModel = "spring")
public interface HashtagMapper {
	
	HashtagResponseDto hashtagEntityToDto(Hashtag entity);
	
	List<HashtagResponseDto> hashtagEntitiesToDtos(List<Hashtag> entities);

}
