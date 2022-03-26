package com.example.store.exceptions;

import com.example.store.model.responses.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<ErrorResponse> handleBadRequestException(
            BadRequestException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionException.class)
    protected ResponseEntity<ErrorResponse> handleTransactionException(
            TransactionException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
//    protected ResponseEntity<ErrorResponse> handleAuthenticationCredentialsNotFoundException(
//            AuthenticationCredentialsNotFoundException ex) {
//        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
//    }

//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex,
//            HttpHeaders headers,
//            HttpStatus status,
//            WebRequest request) {
//
//        List<String> errors = ex.getBindingResult()
//                .getAllErrors().stream()
//                .map(ObjectError::getDefaultMessage)
//                .collect(Collectors.toList());
//
//        return new ResponseEntity<>(new ErrorResponse(errors.get(0)), HttpStatus.BAD_REQUEST);
//    }


}
