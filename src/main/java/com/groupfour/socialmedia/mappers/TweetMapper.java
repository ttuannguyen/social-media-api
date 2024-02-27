package com.groupfour.socialmedia.mappers;

import java.util.List;

import com.groupfour.socialmedia.dtos.TweetRequestDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.dtos.TweetRequestDto;
import com.groupfour.socialmedia.entities.Tweet;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")                  // add a uses = {...} here?
public interface TweetMapper {

    TweetResponseDto entityToDto(Tweet entity);

    List<TweetResponseDto> entitiesToDtos(List<Tweet> entities);

    Tweet requestDtoToEntity(TweetRequestDto questionRequestDto);

    List<Tweet> requestDtosToEntities(List<TweetRequestDto> tweetRequestDtos);

}

