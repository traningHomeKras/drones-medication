package telran.drones.exceptions.controller;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import telran.drones.exceptions.NotFoundException;

@ControllerAdvice
@Slf4j
public class DronesExceptionsHandler {
@ExceptionHandler(NotFoundException.class)
ResponseEntity<String> notFoundHandler(NotFoundException e){
	return returnResponse(e.getMessage(), HttpStatus.NOT_FOUND);
	
}

private ResponseEntity<String> returnResponse(String message, HttpStatus status) {
	log.error(message);
	return new ResponseEntity<String>(message, status);
}

@ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
ResponseEntity<String> bedRequestHandler(RuntimeException e){
return	returnResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
}

@ExceptionHandler(MethodArgumentNotValidException.class)
ResponseEntity<String> methodArgumentNotValidHandler(MethodArgumentNotValidException e){
	String message = e.getAllErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.joining(";"));
	return returnResponse(message, HttpStatus.BAD_REQUEST);
}

@ExceptionHandler(HttpMessageNotReadableException.class)
ResponseEntity<String> httpMessageNotReadableHandler(HttpMessageNotReadableException e){
	
	return returnResponse("Wrong JSON format", HttpStatus.BAD_REQUEST);
}
}
