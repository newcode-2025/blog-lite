package com.example.bloglite.controller;

import com.example.bloglite.comment.ApiResponse;
import com.example.bloglite.dto.UpdateProfileRequest;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.service.UserAccountService;
import com.example.bloglite.util.UserContext;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserAccountService userAccountService;

    public UserController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    /**
     * 查询当前登录用户
     */
    @GetMapping("/me")
    public ApiResponse<UserDTO> getCurrentUser() {
        return ApiResponse.success(
                UserContext.getCurrentUser()
        );
    }

    /**
     * 修改当前登录用户资料
     */
    @PutMapping("/me")
    public ApiResponse<UserDTO> updateCurrentUser(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserDTO currentUser = UserContext.getCurrentUser();

        return ApiResponse.success(
                userAccountService.updateCurrentUser(
                        currentUser.getId(),
                        request
                )
        );
    }
}
