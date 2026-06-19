package com.github.harisherdiansyah.seapediaapi.utils;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ApiResponse<T> {
    private int statusCode;
    private boolean isSuccess;
    private String message;
    private T data;
    private String timestamp;

    private ApiResponse(int statusCode, boolean isSuccess, String message, T data) {
        this.statusCode = statusCode;
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static <T> ApiResponse<T> success(int statusCode, String message, T data) {
        return new ApiResponse<>(statusCode, true, message, data);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message, T data) {
        return new ApiResponse<>(statusCode, false, message, data);
    }
}
