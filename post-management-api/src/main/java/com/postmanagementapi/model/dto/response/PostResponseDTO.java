package com.postmanagementapi.model.dto.response;

import com.postmanagementapi.model.Tag;
import com.postmanagementapi.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDTO {

    private Long id;

    private String title;

    private String content;

    private UserOutput user;

    private List<TagOut> tags;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserOutput{
        private Long id;
        private String userName;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TagOut{
        private Long id;
        private String title;
    }
}