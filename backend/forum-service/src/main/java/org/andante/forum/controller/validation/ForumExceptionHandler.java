package org.andante.forum.controller.validation;

import cz.jirutka.rsql.parser.RSQLParserException;
import exception.PostException;
import exception.PostResponseException;
import exception.TopicException;
import exception.UserException;
import lombok.RequiredArgsConstructor;
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

import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ForumExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({UserException.class, TopicException.class, PostException.class, PostResponseException.class})
    public ResponseEntity<Set<String>> handleDomainException(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Set.of(exception.getMessage()));
    }

    @ExceptionHandler(RSQLParserException.class)
    public ResponseEntity<Set<String>> handleRSQLParserException(RSQLParserException rsqlParserException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Set.of("Provided RSQL query had malformed syntax"));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        Set<String> errorMessages = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toSet());

        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                .body(errorMessages);
    }
}
