package com.ecommerce.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex
    ) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex
    ) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex
    ) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        String message = ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex
    ) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
         //       .message("Something went wrong")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }


    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(
            AuthorizationDeniedException ex
    ) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .message("Access denied")
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenException(InvalidRefreshTokenException ex) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }


}
