package com.demo.common.handler;

import com.demo.modules.error.CustomException;
import com.demo.modules.error.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleUnauthorizedException(AuthenticationException exception) {
        // 클라이언트에게 적절한 HTTP 응답 반환
        CustomException customException = new CustomException(exception);
        return ResponseEntity
                .status(customException.getStatus())
                .body(ErrorDto.builder()
                        .code(customException.getErrorCode().getHttpStatus().value()+"")
                        .msg(customException.getErrorCode().getMessage())
                        .detail(customException.getDetail())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception exception) {
        // 클라이언트에게 적절한 HTTP 응답 반환
        CustomException customException = new CustomException(exception);
        return ResponseEntity
                .status(customException.getStatus())
                .body(ErrorDto.builder()
                        .code(customException.getErrorCode().getHttpStatus().value()+"")
                        .msg(customException.getErrorCode().getMessage())
                        .detail(customException.getDetail())
                        .build());
    }

}