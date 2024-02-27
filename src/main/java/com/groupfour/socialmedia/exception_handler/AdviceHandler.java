package com.groupfour.socialmedia.exception_handler;

import com.groupfour.socialmedia.exceptions.TempException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdviceHandler {

    @ExceptionHandler(value = {
            TempException.class
    })
    public ResponseEntity<Object> handleTempExceptions(Exception e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        if (e instanceof TempException) {
            TempException tempException = new TempException(
                    e.getMessage()
            );
            return new ResponseEntity<>(tempException, badRequest);
        }
        return null;
    }

}
