package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.UpdateProfileRequest;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.service.UserAccountService;
import com.example.bloglite.service.UserAvatarService;
import com.example.bloglite.util.UserContext;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserAccountService userAccountService;

    private final UserAvatarService userAvatarService;

    public UserController(
            UserAccountService userAccountService,
            UserAvatarService userAvatarService
    ) {
        this.userAccountService = userAccountService;
        this.userAvatarService = userAvatarService;
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

    /**
     * 上传并更新当前登录用户头像
     */
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping(
            value = "/me/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<UserDTO> uploadCurrentUserAvatar(
            @RequestParam("file") MultipartFile file
    ) {
        UserDTO currentUser = UserContext.getCurrentUser();

        return ApiResponse.success(
                userAvatarService.updateAvatar(
                        currentUser.getId(),
                        file
                )
        );
    }
}
