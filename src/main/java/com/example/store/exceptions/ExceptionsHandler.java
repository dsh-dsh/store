package com.example.store.exceptions;

import com.example.store.model.responses.ErrorResponse;
import com.example.store.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MailService mailService;

    @Value("${spring.mail.to.email}")
    private String toEmail;

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<ErrorResponse> handleBadRequestException(
            BadRequestException ex) {

//        mailService.send(toEmail, Constants.ERROR_SUBJECT, ex.getExceptionType().getValue() + ex.getMessage());

        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(),
                        ex.getExceptionType().getValue()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionException.class)
    protected ResponseEntity<ErrorResponse> handleTransactionException(
            TransactionException ex) {

//        mailService.send(toEmail, Constants.ERROR_SUBJECT, ex.getMessage());

        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        List<String> errors = ex.getBindingResult()
                .getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

        return new ResponseEntity<>(new ErrorResponse(errors.get(0)), HttpStatus.BAD_REQUEST);
    }

}
