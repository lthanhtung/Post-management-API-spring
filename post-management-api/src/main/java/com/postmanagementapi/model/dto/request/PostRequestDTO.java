package com.postmanagementapi.model.dto.request;

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
public class PostRequestDTO {

    @NotBlank(message = "title không được để trống")
    private String title;

    @NotBlank(message = "content không được để trống")
    private String content;

    private UserInput user;

    private List<TagInput> tags;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserInput{
        private Long id;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class TagInput{
        private String name;
    }
}


