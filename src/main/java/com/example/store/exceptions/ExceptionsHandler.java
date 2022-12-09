package com.example.store.exceptions;

import com.example.store.model.responses.ErrorResponse;
import com.example.store.model.responses.WarningResponse;
import com.example.store.services.MailService;
import com.example.store.services.UserService;
import com.example.store.utils.Constants;
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
    @Autowired
    private UserService userService;

    public static final String FORMAT_MESSAGE = "%s%n%s%n%s%n user - %s";
    @Value("${spring.mail.to.email}")
    private String toEmail;

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        mailService.send(toEmail, Constants.ERROR_SUBJECT,
                String.format(FORMAT_MESSAGE, ex.getMessage(), ex.getInfo(), "BadRequestException", userService.getCurrentUser().getLastName()));
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(),
                        ex.getExceptionType().getValue()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionException.class)
    protected ResponseEntity<ErrorResponse> handleTransactionException(TransactionException ex) {
        mailService.send(toEmail, Constants.ERROR_SUBJECT,
                String.format(FORMAT_MESSAGE, ex.getMessage(), ex.getInfo(), "TransactionException", userService.getCurrentUser().getLastName()));
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HoldDocumentException.class)
    protected ResponseEntity<ErrorResponse> handleHoldDocumentException(HoldDocumentException ex) {
        mailService.send(toEmail, Constants.ERROR_SUBJECT,
                String.format(FORMAT_MESSAGE, ex.getMessage(), ex.getInfo(), "HoldDocumentException", userService.getCurrentUser().getLastName()));
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WarningException.class)
    protected ResponseEntity<WarningResponse> handleWarningException(WarningException ex) {
        mailService.send(toEmail, Constants.WARNING_SUBJECT,
                String.format(FORMAT_MESSAGE, ex.getMessage(), ex.getInfo(), "WarningException", userService.getCurrentUser().getLastName()));
        return new ResponseEntity<>(new WarningResponse(ex.getMessage(),
                    ex.getExceptionType().getValue()), HttpStatus.BAD_REQUEST);
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
