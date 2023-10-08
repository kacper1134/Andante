package org.andante.activity.controller.validation;

import cz.jirutka.rsql.parser.RSQLParserException;
import lombok.RequiredArgsConstructor;
import org.andante.activity.exception.ActivityException;
import org.andante.activity.exception.NewsletterException;
import org.andante.activity.exception.UserConflictException;
import org.andante.activity.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.mail.MessagingException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ActivityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Set<String>> handleConstraintViolationException(ConstraintViolationException e) {
        Set<String> errorMessages = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                .body(errorMessages);
    }

    @ExceptionHandler({ActivityException.class, NewsletterException.class, UserNotFoundException.class, UserConflictException.class, IllegalArgumentException.class})
    public ResponseEntity<Set<String>> handleDomainException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                .body(Set.of(e.getMessage()));
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<Set<String>> handleMessagingException(MessagingException e) {
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY)
                .body(Set.of(e.getMessage()));
    }

    @ExceptionHandler({RSQLParserException.class})
    public ResponseEntity<Set<String>> handleRSQLParserException(RSQLParserException rsqlParserException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Set.of("Provided RSQL query had malformed syntax"));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Set<String> errorMessages = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toSet());

        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                .body(errorMessages);
    }
}
