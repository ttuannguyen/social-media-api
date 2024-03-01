package com.groupfour.socialmedia.mappers;

import org.mapstruct.Mapper;

import com.groupfour.socialmedia.dtos.ProfileDto;
import com.groupfour.socialmedia.entities.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
	ProfileDto entityToDto(Profile entity);
	Profile dtoToEntity(ProfileDto profileDto);
}
