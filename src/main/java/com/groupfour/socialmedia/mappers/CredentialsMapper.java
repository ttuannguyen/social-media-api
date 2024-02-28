package com.groupfour.socialmedia.mappers;

import com.groupfour.socialmedia.dtos.TweetRequestDto;
import com.groupfour.socialmedia.dtos.TweetResponseDto;
import com.groupfour.socialmedia.entities.Tweet;
import org.mapstruct.Mapper;

import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.entities.Credentials;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CredentialsMapper {
	CredentialsDto entityToDto(Credentials entity);
	Credentials dtoToEntity(CredentialsDto credentialsDto);


}
