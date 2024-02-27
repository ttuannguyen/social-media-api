package com.groupfour.socialmedia.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class NotAuthorizedException extends RuntimeException{

	private static final long serialVersionUID = -9004356055787700568L;
	private String message;
}
