package com.groupfour.socialmedia.mappers;

import com.groupfour.socialmedia.dtos.UserRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.groupfour.socialmedia.dtos.UserResponseDto;
import com.groupfour.socialmedia.entities.User;

import java.util.List;

@Mapper(componentModel = "spring", uses= {ProfileMapper.class, CredentialsMapper.class})
public interface UserMapper {
	
	@Mapping(target="username", source="credentials.username")
	UserResponseDto entityToDto(User entity);

	User responseDtoToEntity(UserResponseDto userResponseDto);

	User requestDtoToEntity(UserRequestDto userRequestDto);

	List<UserResponseDto> entitiesToDtos(List<User> entities);

	List<User> dtosToEntities(List<UserResponseDto> userResponseDtos);

}
