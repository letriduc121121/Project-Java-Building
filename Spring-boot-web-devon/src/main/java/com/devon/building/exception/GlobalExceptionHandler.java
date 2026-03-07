package com.devon.building.exception;

import com.devon.building.model.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidEntityException.class)
    public ResponseEntity<ResponseDTO> handleInvalidBuildingException(InvalidEntityException e) {
        ResponseDTO response=new ResponseDTO();
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
