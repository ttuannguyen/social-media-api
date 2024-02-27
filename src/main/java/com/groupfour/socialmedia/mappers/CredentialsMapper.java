package com.groupfour.socialmedia.mappers;

import org.mapstruct.Mapper;

import com.groupfour.socialmedia.dtos.CredentialsDto;
import com.groupfour.socialmedia.entities.Credentials;

@Mapper(componentModel = "spring")
public interface CredentialsMapper {
	CredentialsDto entityToDto(Credentials entity);
}
