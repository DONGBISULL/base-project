package com.demo.common.advicer;

import com.demo.modules.dto.ErrorDto;
import com.demo.modules.enums.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorDto> handleCustomException(CustomException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorDto.builder()
                        .code(ex.getErrorCode().getHttpStatus().value()+"")
                        .msg(ex.getErrorCode().getMessage())
                        .detail(ex.getDetail())
                        .build());
    }

}
