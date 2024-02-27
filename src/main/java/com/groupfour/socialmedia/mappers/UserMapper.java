package com.groupfour.socialmedia.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.groupfour.socialmedia.dtos.UserResponseDto;
import com.groupfour.socialmedia.entities.User;

@Mapper(componentModel = "spring", uses= {ProfileMapper.class, CredentialsMapper.class})
public interface UserMapper {
	
	@Mapping(target="username", source="credential.username")
	UserResponseDto entityToDto(User entity);

}
