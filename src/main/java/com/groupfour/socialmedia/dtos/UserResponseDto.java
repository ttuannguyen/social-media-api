package com.groupfour.socialmedia.dtos;

import java.sql.Timestamp;

import com.groupfour.socialmedia.entities.Profile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponseDto {

	private String username;
	private ProfileDto profile;
	private Timestamp joined;
}
