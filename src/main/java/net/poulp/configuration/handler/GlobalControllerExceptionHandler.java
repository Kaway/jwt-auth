package net.poulp.configuration.handler;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(value =  HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public void badCredentials() {
    }

}
