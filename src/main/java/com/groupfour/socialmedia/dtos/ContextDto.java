package com.groupfour.socialmedia.dtos;

import java.util.List;

import com.groupfour.socialmedia.entities.Tweet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ContextDto {
	private TweetResponseDto target;
	private List<TweetResponseDto> before;
	private List<TweetResponseDto> after;

}
