package com.example.bloglite.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 上传 URL 与现有业务请求 DTO 的兼容性测试
 */
class MediaUrlBusinessValidationTest {

    private final Validator validator = Validation
            .buildDefaultValidatorFactory()
            .getValidator();

    @Test
    void shouldAcceptUploadedUrlInBusinessRequests() {
        String imageUrl = "/uploads/article/2026/10/example.png";

        AdminArticleCreateRequest article = new AdminArticleCreateRequest();
        article.setTitle("integration article");
        article.setContent("integration content");
        article.setCoverUrl(imageUrl);
        article.setStatus(3);

        AdminTalkSaveRequest talk = new AdminTalkSaveRequest();
        talk.setContent("integration talk");
        talk.setImages(List.of(imageUrl));
        talk.setStatus(1);

        AdminPhotoAlbumSaveRequest album = new AdminPhotoAlbumSaveRequest();
        album.setName("integration album");
        album.setCoverUrl(imageUrl);
        album.setStatus(1);

        AdminPhotoSaveRequest photo = new AdminPhotoSaveRequest();
        photo.setPhotoUrl(imageUrl);

        AdminFriendLinkSaveRequest link = new AdminFriendLinkSaveRequest();
        link.setName("integration link");
        link.setAddress("https://example.com");
        link.setAvatarUrl(imageUrl);
        link.setStatus(1);

        assertTrue(validator.validate(article).isEmpty());
        assertTrue(validator.validate(talk).isEmpty());
        assertTrue(validator.validate(album).isEmpty());
        assertTrue(validator.validate(photo).isEmpty());
        assertTrue(validator.validate(link).isEmpty());
    }
}
