package cat.itacademy.s05.t01.exception;

import cat.itacademy.s05.t01.exception.custom.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlayerNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleException(PlayerNotFoundException e) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), System.currentTimeMillis());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    @ExceptionHandler(GameNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleException(GameNotFoundException e) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), System.currentTimeMillis());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    @ExceptionHandler(NoPlayersException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleException(NoPlayersException e) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NO_CONTENT.value(), e.getMessage(), System.currentTimeMillis());
        return Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body(error));
    }

    @ExceptionHandler(InvalidMoveException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleException(InvalidMoveException e) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), System.currentTimeMillis());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

}
