package com.example.bloglite.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse <T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(20000,"操作成功",data);
    }

    public static <T> ApiResponse<T> error(String message){
        return new ApiResponse<>(50000,message,null);
    }
}
