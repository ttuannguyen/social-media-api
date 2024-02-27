package com.groupfour.socialmedia.controllers.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.groupfour.socialmedia.exceptions.BadRequestException;
import com.groupfour.socialmedia.dtos.ErrorDto;
import com.groupfour.socialmedia.exceptions.NotAuthorizedException;

@ControllerAdvice(basePackages = { "com.groupfour.socialmedia.controllers" })
@ResponseBody
public class SocialMediaControllersAdvice {

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(NotAuthorizedException.class)
	public ErrorDto handleBadRequestException(NotAuthorizedException notAuthorizedException) {
		return new ErrorDto(notAuthorizedException.getMessage());
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorDto handleBadRequestException(BadRequestException badRequestException) {
        return new ErrorDto(badRequestException.getMessage());
    }

	

}
