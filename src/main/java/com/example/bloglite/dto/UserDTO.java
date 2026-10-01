package com.example.bloglite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 返回给客户端的脱敏用户信息
 */
@Data
@AllArgsConstructor
public class UserDTO {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String avatarUrl;

    private Integer status;

    private LocalDateTime createdAt;
}
