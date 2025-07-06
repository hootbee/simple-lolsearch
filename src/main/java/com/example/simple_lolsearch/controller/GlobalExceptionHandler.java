package com.example.simple_lolsearch.controller;

import com.example.simple_lolsearch.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebClientResponseException.NotFound.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(WebClientResponseException.NotFound e) {
        log.error("리소스를 찾을 수 없습니다: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("소환사를 찾을 수 없습니다."));
    }

    @ExceptionHandler(WebClientResponseException.Forbidden.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(WebClientResponseException.Forbidden e) {
        log.error("API 접근이 거부되었습니다: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("API 접근이 거부되었습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception e) {
        log.error("예상치 못한 오류가 발생했습니다: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("서버 오류가 발생했습니다."));
    }
}
